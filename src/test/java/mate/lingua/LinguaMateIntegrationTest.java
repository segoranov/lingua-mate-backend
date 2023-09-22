package mate.lingua;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

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

    @Test
    void validate_flash_cards_game() throws Exception {
        LearningDataset dataset1 = createLearningDataset("dataset4");
        LearningDataset dataset2 = createLearningDataset("dataset5");
        LearningDataset dataset3 = createLearningDataset("dataset6");

        // TODO

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
}
