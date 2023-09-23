package mate.lingua;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.lingua.model.FlashCardsGameState;
import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(properties = {
        "spring.liquibase.change-log=classpath:config/liquibase/master.xml"
})
@AutoConfigureMockMvc
public class LinguaMateIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:15.4");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    public static void init() {
        String jdbcUrl = postgresqlContainer.getJdbcUrl();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, postgresqlContainer.getUsername(),
                postgresqlContainer.getPassword());
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE linguamatedb");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create the database", e);
        }
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgresqlContainer.getJdbcUrl() + "linguamatedb");
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
    }

    @Test
    void validate_basic_CRUD_operations() throws Exception {
        LearningDataset dataset1 = createLearningDataset("dataset1");
        LearningDataset dataset2 = createLearningDataset("dataset2");
        LearningDataset dataset3 = createLearningDataset("dataset3");

        // TODO test patch learning dataset

        TranslationUnit translationUnit1 =
                createTranslationUnit(TranslationUnit.builder().text("testText1").translation("testTranslation1").build(),
                        dataset1.getId());
        TranslationUnit translationUnit2 =
                createTranslationUnit(TranslationUnit.builder().text("testText2").translation("testTranslation3").build(),
                        dataset1.getId());
        TranslationUnit translationUnit3 =
                createTranslationUnit(TranslationUnit.builder().text("testText3").translation("testTranslation3").build(),
                        dataset2.getId());
        TranslationUnit translationUnit4 =
                createTranslationUnit(TranslationUnit.builder().text("testText4").translation("testTranslation4").build(),
                        dataset2.getId());
        TranslationUnit translationUnit5 =
                createTranslationUnit(TranslationUnit.builder().text("testText5").translation("testTranslation5").build(),
                        dataset3.getId());
        TranslationUnit translationUnit6 =
                createTranslationUnit(TranslationUnit.builder().text("testText6").translation("testTranslation6").build(),
                        dataset3.getId());

        List<TranslationUnit> dataset1TranslationUnits = getTranslationUnits(dataset1.getId());
        List<TranslationUnit> dataset2TranslationUnits = getTranslationUnits(dataset1.getId());
        List<TranslationUnit> dataset3TranslationUnits = getTranslationUnits(dataset1.getId());

        assertThat(dataset1TranslationUnits).hasSize(2);
        assertThat(dataset2TranslationUnits).hasSize(2);
        assertThat(dataset3TranslationUnits).hasSize(2);

        deleteTranslationUnit(dataset1.getId(), translationUnit1.getId());
        deleteTranslationUnit(dataset1.getId(), translationUnit2.getId());
        dataset1TranslationUnits = getTranslationUnits(dataset1.getId());
        assertThat(dataset1TranslationUnits).hasSize(0);
        assertThat(dataset2TranslationUnits).hasSize(2);
        assertThat(dataset3TranslationUnits).hasSize(2);

        // TODO test patch translation unit

        deleteLearningDataset(dataset1.getId());
        deleteLearningDataset(dataset2.getId());
        deleteLearningDataset(dataset3.getId());
    }

    private LearningDataset createLearningDataset(String name) throws Exception {
        String content = String.format("{\"name\": \"%s\"}", name);
        String datasetStr = mockMvc.perform(post("/api/v1/learning-datasets")
                        .contentType("application/json")
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("http://.+/api/v1/learning-datasets/\\d+")))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(datasetStr, LearningDataset.class);
    }

    private TranslationUnit createTranslationUnit(TranslationUnit translationUnit, long learningDatasetId) throws Exception {
        String uri = "/api/v1/learning-datasets/" + learningDatasetId + "/translation-units";
        String translationUnitStr = mockMvc.perform(post(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(translationUnit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value(translationUnit.getText()))
                .andExpect(jsonPath("$.translation").value(translationUnit.getTranslation()))
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(translationUnitStr, TranslationUnit.class);
    }

    private List<TranslationUnit> getTranslationUnits(long learningDatasetId) throws Exception {
        String uri = "/api/v1/learning-datasets/" + learningDatasetId + "/translation-units";
        String translationUnitsStr = mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(translationUnitsStr, new TypeReference<>() {
        });
    }

    private void deleteTranslationUnit(long learningDatasetId, long translationUnitId) throws Exception {
        String uri = "/api/v1/learning-datasets/" + learningDatasetId + "/translation-units/" + translationUnitId;
        mockMvc.perform(delete(uri)).andExpect(status().isNoContent());
        mockMvc.perform(delete(uri)).andExpect(status().isNotFound());
    }

    private void deleteLearningDataset(long learningDatasetId) throws Exception {
        mockMvc.perform(delete("/api/v1/learning-datasets/" + learningDatasetId))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/v1/learning-datasets/" + learningDatasetId))
                .andExpect(status().isNotFound());
    }

    @Test
    void validate_flash_cards_game() throws Exception {
        LearningDataset dataset = createLearningDataset("dataset4");

        TranslationUnit translationUnit1 =
                createTranslationUnit(TranslationUnit.builder().text("testText1").translation("testTranslation1").build(),
                        dataset.getId());
        TranslationUnit translationUnit2 =
                createTranslationUnit(TranslationUnit.builder().text("testText2").translation("testTranslation3").build(),
                        dataset.getId());
        TranslationUnit translationUnit3 =
                createTranslationUnit(TranslationUnit.builder().text("testText3").translation("testTranslation3").build(),
                        dataset.getId());

        startGameWithoutExpectingError(dataset);
        startGameExpectingGameAlreadyInProgressError(dataset);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(translationUnit1)
                        .learnedTranslationUnitsIdsList(Collections.emptyList())
                        .notLearnedTranslationUnitsIdsQueue(
                                new LinkedList<>(List.of(translationUnit2.getId(), translationUnit3.getId())))
                        .build());

        markCurrentTranslationUnitAsLearned(dataset);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(translationUnit2)
                        .learnedTranslationUnitsIdsList(List.of(translationUnit1.getId()))
                        .notLearnedTranslationUnitsIdsQueue(
                                new LinkedList<>(List.of(translationUnit3.getId())))
                        .build());

        markCurrentTranslationUnitAsLearned(dataset);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(translationUnit3)
                        .learnedTranslationUnitsIdsList(List.of(translationUnit1.getId(), translationUnit2.getId()))
                        .notLearnedTranslationUnitsIdsQueue(new LinkedList<>())
                        .build());

        markCurrentTranslationUnitAsLearned(dataset);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(null)
                        .learnedTranslationUnitsIdsList(
                                List.of(translationUnit1.getId(), translationUnit2.getId(), translationUnit3.getId()))
                        .notLearnedTranslationUnitsIdsQueue(new LinkedList<>())
                        .build());

        unmarkLearnedTranslationUnit(dataset, translationUnit1);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(translationUnit1)
                        .learnedTranslationUnitsIdsList(List.of(translationUnit2.getId(), translationUnit3.getId()))
                        .notLearnedTranslationUnitsIdsQueue(new LinkedList<>())
                        .build());

        unmarkLearnedTranslationUnit(dataset, translationUnit2);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(translationUnit1)
                        .learnedTranslationUnitsIdsList(List.of(translationUnit3.getId()))
                        .notLearnedTranslationUnitsIdsQueue(new LinkedList<>(List.of(translationUnit2.getId())))
                        .build());

        repeatCurrentTranslationUnit(dataset);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(translationUnit2)
                        .learnedTranslationUnitsIdsList(List.of(translationUnit3.getId()))
                        .notLearnedTranslationUnitsIdsQueue(new LinkedList<>(List.of(translationUnit1.getId())))
                        .build());

        repeatCurrentTranslationUnit(dataset);

        validateFlashcardsGameState(dataset.getId(),
                FlashCardsGameState
                        .builder()
                        .currentlyLearnedTranslationUnit(translationUnit1)
                        .learnedTranslationUnitsIdsList(List.of(translationUnit3.getId()))
                        .notLearnedTranslationUnitsIdsQueue(new LinkedList<>(List.of(translationUnit2.getId())))
                        .build());

        finishGameWithoutExpectingError(dataset);
        finishGameExpectingNoActiveGameError(dataset);
    }

    private void startGameWithoutExpectingError(LearningDataset dataset) throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets/" + dataset.getId() + "/actions/start-flashcards-game"))
                .andExpect(status().isNoContent());
    }

    private void startGameExpectingGameAlreadyInProgressError(LearningDataset dataset) throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets/" + dataset.getId() + "/actions/start-flashcards-game"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.Errors.FLASH_CARDS_GAME_FOR_LEARNING_DATASET_ALREADY_IN_PROGRESS));
    }


    private void markCurrentTranslationUnitAsLearned(LearningDataset dataset) throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets/" + dataset.getId() +
                        "/active-flashcards-game/actions/mark-learned"))
                .andExpect(status().isNoContent());
    }

    private void unmarkLearnedTranslationUnit(LearningDataset dataset, TranslationUnit translationUnit) throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets/" + dataset.getId() +
                        "/active-flashcards-game/actions/unmark-learned")
                        .contentType("application/json")
                        .content(String.format("{\"id\": %d }", translationUnit.getId())))
                .andExpect(status().isNoContent());
    }

    private void repeatCurrentTranslationUnit(LearningDataset dataset) throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets/" + dataset.getId() +
                        "/active-flashcards-game/actions/repeat"))
                .andExpect(status().isNoContent());
    }

    private void validateFlashcardsGameState(long learningDatasetId, FlashCardsGameState expectedFlashCardsGameState) throws Exception {
        ResultActions resultActions = getActiveFlashcardsGameStateResultActions(learningDatasetId);
        validateCurrentlyLearnedTranslationUnit(expectedFlashCardsGameState, resultActions);
        validateNotLearnedTranslationUnits(expectedFlashCardsGameState, resultActions);
        validateLearnedTranslationUnits(expectedFlashCardsGameState, resultActions);
    }

    private ResultActions getActiveFlashcardsGameStateResultActions(long learningDatasetId) throws Exception {
        return mockMvc.perform(get("/api/v1/learning-datasets/" +
                        learningDatasetId +
                        "/active-flashcards-game/state"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    private void validateCurrentlyLearnedTranslationUnit(FlashCardsGameState flashCardsGameState,
                                                         ResultActions resultActions) throws Exception {
        TranslationUnit currentlyLearnedTranslationUnit = flashCardsGameState.getCurrentlyLearnedTranslationUnit();
        if (currentlyLearnedTranslationUnit == null) {
            resultActions
                    .andExpect(jsonPath("$.currentlyLearnedTranslationUnit").value(Matchers.nullValue()));
        } else {
            resultActions
                    .andExpect(jsonPath("$.currentlyLearnedTranslationUnit.id").value(currentlyLearnedTranslationUnit.getId()))
                    .andExpect(jsonPath("$.currentlyLearnedTranslationUnit.text").value(currentlyLearnedTranslationUnit.getText()))
                    .andExpect(jsonPath("$.currentlyLearnedTranslationUnit.translation").value(currentlyLearnedTranslationUnit.getTranslation()));
        }
    }

    private void validateNotLearnedTranslationUnits(FlashCardsGameState expectedFlashCardsGameState,
                                                    ResultActions resultActions) throws Exception {
        Queue<Long> notLearnedTranslationUnitsIdsQueue =
                expectedFlashCardsGameState.getNotLearnedTranslationUnitsIdsQueue();
        if (notLearnedTranslationUnitsIdsQueue.isEmpty()) {
            resultActions.andExpect(jsonPath("$.notLearnedTranslationUnitsIds").isEmpty());
        } else {
            for (int el = 0; el < notLearnedTranslationUnitsIdsQueue.size(); el++) {
                String path = String.format("$.notLearnedTranslationUnitsIds[%d]", el);
                long id = notLearnedTranslationUnitsIdsQueue.poll();
                resultActions.andExpect(jsonPath(path).value(id));
            }
        }
    }

    private void validateLearnedTranslationUnits(FlashCardsGameState expectedFlashCardsGameState,
                                                 ResultActions resultActions) throws Exception {
        List<Long> learnedTranslationUnitsIdsList = expectedFlashCardsGameState.getLearnedTranslationUnitsIdsList();
        if (learnedTranslationUnitsIdsList.isEmpty()) {
            resultActions.andExpect(jsonPath("$.learnedTranslationUnitsIds").isEmpty());
        } else {
            for (int el = 0; el < learnedTranslationUnitsIdsList.size(); el++) {
                String path = String.format("$.learnedTranslationUnitsIds[%d]", el);
                resultActions.andExpect(jsonPath(path).value(learnedTranslationUnitsIdsList.get(el)));
            }
        }
    }

    private void finishGameWithoutExpectingError(LearningDataset dataset) throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets/" + dataset.getId() + "/actions/finish-flashcards-game"))
                .andExpect(status().isNoContent());
    }

    private void finishGameExpectingNoActiveGameError(LearningDataset dataset) throws Exception {
        mockMvc.perform(post("/api/v1/learning-datasets/" + dataset.getId() + "/actions/finish-flashcards-game"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.Errors.NO_ACTIVE_FLASH_CARDS_GAME_EXISTS));
    }
}
