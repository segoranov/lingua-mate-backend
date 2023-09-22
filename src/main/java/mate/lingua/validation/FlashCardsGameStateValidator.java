package mate.lingua.validation;

import mate.lingua.Constants;
import mate.lingua.exception.InvalidFlashCardsGameStateException;
import mate.lingua.model.FlashCardsGameState;
import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import mate.lingua.util.CollectionsUtil;

import java.util.List;

public class FlashCardsGameStateValidator {

    private final LearningDataset learningDataset;
    private final FlashCardsGameState stateToValidate;

    public FlashCardsGameStateValidator(LearningDataset learningDataset, FlashCardsGameState stateToValidate) {
        this.learningDataset = learningDataset;
        this.stateToValidate = stateToValidate;
    }

    public void validate() {
        validateStateIsComplete();
        validateAllTranslationUnitIdsArePresentInState();
        validateCurrentlyLearnedTranslationUnitId();
    }

    private void validateStateIsComplete() {
        if (stateToValidate.getLearnedTranslationUnitsIds() == null ||
                stateToValidate.getNotLearnedTranslationUnitsIds() == null ||
                stateToValidate.getCurrentlyLearnedTranslationUnitId() == null) {
            throw new InvalidFlashCardsGameStateException(Constants.Errors.FLASH_CARDS_GAME_STATE_IS_INCOMPLETE);
        }
    }

    private void validateAllTranslationUnitIdsArePresentInState() {
        List<Long> translationUnitIds = getTranslationUnitIds();

        List<Long> allTranslationUnitIdsFromState =
                CollectionsUtil.concat(stateToValidate.getLearnedTranslationUnitsIds(),
                        stateToValidate.getNotLearnedTranslationUnitsIds());


        if (!CollectionsUtil.listEqualsIgnoreOrder(translationUnitIds, allTranslationUnitIdsFromState)) {
            throw new InvalidFlashCardsGameStateException(
                    Constants.Errors.FLASH_CARDS_GAME_STATE_TRANSLATION_UNIT_IDS_DO_NOT_MATCH_THOSE_FROM_THE_CORRESPONDING_LEARNING_DATASET);
        }
    }

    private List<Long> getTranslationUnitIds() {
        return learningDataset.getTranslationUnits().stream().map(TranslationUnit::getId).toList();
    }


    private void validateCurrentlyLearnedTranslationUnitId() {
        List<Long> translationUnitIds = getTranslationUnitIds();

        if (!translationUnitIds.contains(stateToValidate.getCurrentlyLearnedTranslationUnitId())) {
            throw new InvalidFlashCardsGameStateException(Constants.Errors.CURRENTLY_LEARNED_TRANSLATION_UNIT_ID_IS_NOT_VALID);
        }

        if (stateToValidate.getLearnedTranslationUnitsIds().contains(stateToValidate.getCurrentlyLearnedTranslationUnitId())) {
            throw new InvalidFlashCardsGameStateException(
                    Constants.Errors.CURRENTLY_LEARNED_TRANSLATION_UNIT_ID_CANNOT_BE_PRESENT_IN_LEARNED_TRANSLATION_UNIT_IDS_LIST);
        }
    }
}
