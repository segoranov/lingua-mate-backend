package mate.lingua.validation;

import mate.lingua.Constants;
import mate.lingua.exception.InvalidFlashCardsGameStateException;
import mate.lingua.model.FlashCardsGameState;
import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import mate.lingua.util.CollectionsUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlashCardsGameStateValidatorTest {

    @Test
    void shouldNotThrowAnyException_whenStateIsValid() {
        // Arrange
        List<Long> learnedTranslationUnitsIds = List.of(1L, 2L, 3L, 4L);
        List<Long> notLearnedTranslationUnitsIds = List.of(5L, 6L, 7L, 8L);

        FlashCardsGameState validState = FlashCardsGameState.builder()
                .currentlyLearnedTranslationUnitId(5L)
                .learnedTranslationUnitsIds(learnedTranslationUnitsIds)
                .notLearnedTranslationUnitsIds(notLearnedTranslationUnitsIds)
                .build();

        List<TranslationUnit> translationUnits =
                CollectionsUtil.concat(learnedTranslationUnitsIds, notLearnedTranslationUnitsIds)
                        .stream()
                        .map(id -> TranslationUnit.builder().id(id).build())
                        .toList();

        LearningDataset learningDataset = LearningDataset.builder().translationUnits(translationUnits).build();

        FlashCardsGameStateValidator flashCardsGameStateValidator =
                new FlashCardsGameStateValidator(learningDataset, validState);

        // Act & Assert
        assertThatCode(flashCardsGameStateValidator::validate).doesNotThrowAnyException();
    }

    @Test
    void shouldThrowException_whenTranslationUnitIds_doNotMatch() {
        // Arrange
        List<Long> learnedTranslationUnitsIds = List.of(1L, 2L, 3L, 4L);
        List<Long> notLearnedTranslationUnitsIds = List.of(5L, 6L, 7L, 8L);

        FlashCardsGameState validState = FlashCardsGameState.builder()
                .currentlyLearnedTranslationUnitId(1L)
                .learnedTranslationUnitsIds(learnedTranslationUnitsIds)
                .notLearnedTranslationUnitsIds(notLearnedTranslationUnitsIds)
                .build();

        List<Long> someRandomTranslationUnitIds = List.of(1L, 50L, 34L, 12L, 18L, 56L);
        List<TranslationUnit> translationUnits = someRandomTranslationUnitIds
                .stream()
                .map(id -> TranslationUnit.builder().id(id).build())
                .toList();

        LearningDataset learningDataset = LearningDataset.builder().translationUnits(translationUnits).build();

        FlashCardsGameStateValidator flashCardsGameStateValidator =
                new FlashCardsGameStateValidator(learningDataset, validState);

        // Act & Assert
        assertThatThrownBy(flashCardsGameStateValidator::validate)
                .isInstanceOf(InvalidFlashCardsGameStateException.class)
                .hasMessage(Constants.Errors.FLASH_CARDS_GAME_STATE_TRANSLATION_UNIT_IDS_DO_NOT_MATCH_THOSE_FROM_THE_CORRESPONDING_LEARNING_DATASET);
    }

    @Test
    void shouldThrowException_whenCurrentlyLearnedId_isNotValid() {
        // Arrange
        List<Long> learnedTranslationUnitsIds = List.of(1L, 2L, 3L, 4L);
        List<Long> notLearnedTranslationUnitsIds = List.of(5L, 6L, 7L, 8L);
        long invalidId = 50;

        FlashCardsGameState validState = FlashCardsGameState.builder()
                .currentlyLearnedTranslationUnitId(invalidId)
                .learnedTranslationUnitsIds(learnedTranslationUnitsIds)
                .notLearnedTranslationUnitsIds(notLearnedTranslationUnitsIds)
                .build();

        List<TranslationUnit> translationUnits =
                CollectionsUtil.concat(learnedTranslationUnitsIds, notLearnedTranslationUnitsIds)
                        .stream()
                        .map(id -> TranslationUnit.builder().id(id).build())
                        .toList();

        LearningDataset learningDataset = LearningDataset.builder().translationUnits(translationUnits).build();

        FlashCardsGameStateValidator flashCardsGameStateValidator =
                new FlashCardsGameStateValidator(learningDataset, validState);

        // Act & Assert
        assertThatThrownBy(flashCardsGameStateValidator::validate)
                .isInstanceOf(InvalidFlashCardsGameStateException.class)
                .hasMessage(Constants.Errors.CURRENTLY_LEARNED_TRANSLATION_UNIT_ID_IS_NOT_VALID);
    }

    @Test
    void shouldThrowException_whenCurrentlyLearnedId_isPresentInLearnedTranslationUnitIdsList() {
        // Arrange
        List<Long> learnedTranslationUnitsIds = List.of(1L, 2L, 3L, 4L);
        List<Long> notLearnedTranslationUnitsIds = List.of(5L, 6L, 7L, 8L);
        long idFromLearnedTranslationUnitIds = learnedTranslationUnitsIds.get(2);

        FlashCardsGameState validState = FlashCardsGameState.builder()
                .currentlyLearnedTranslationUnitId(idFromLearnedTranslationUnitIds)
                .learnedTranslationUnitsIds(learnedTranslationUnitsIds)
                .notLearnedTranslationUnitsIds(notLearnedTranslationUnitsIds)
                .build();

        List<TranslationUnit> translationUnits =
                CollectionsUtil.concat(learnedTranslationUnitsIds, notLearnedTranslationUnitsIds)
                        .stream()
                        .map(id -> TranslationUnit.builder().id(id).build())
                        .toList();

        LearningDataset learningDataset = LearningDataset.builder().translationUnits(translationUnits).build();

        FlashCardsGameStateValidator flashCardsGameStateValidator =
                new FlashCardsGameStateValidator(learningDataset, validState);

        // Act & Assert
        assertThatThrownBy(flashCardsGameStateValidator::validate)
                .isInstanceOf(InvalidFlashCardsGameStateException.class)
                .hasMessage(Constants.Errors.CURRENTLY_LEARNED_TRANSLATION_UNIT_ID_CANNOT_BE_PRESENT_IN_LEARNED_TRANSLATION_UNIT_IDS_LIST);
    }
}
