package mate.lingua.service;

import mate.lingua.exception.service.*;
import mate.lingua.model.FlashCardsGame;

public interface FlashCardsGameService {
    void startGame(long learningDatasetId) throws NoTranslationUnitsToStartFlashCardsGameException,
            FlashCardsGameAlreadyExistsException, LearningDataSetDoesNotExistException;

    void finishGame(long learningDatasetId) throws NoActiveFlashCardsGameException,
            LearningDataSetDoesNotExistException;

    void markCurrentlyLearnedTranslationUnitAsLearned(long learningDatasetId)
            throws LearningDataSetDoesNotExistException, NoActiveFlashCardsGameException,
            NoCurrentlyLearnedTranslationUnitException;

    void markCurrentlyLearnedTranslationUnitForRepetition(long learningDatasetId) throws LearningDataSetDoesNotExistException,
            NoActiveFlashCardsGameException, NoCurrentlyLearnedTranslationUnitException;

    FlashCardsGame getActiveGame(long learningDatasetId) throws NoActiveFlashCardsGameException,
            LearningDataSetDoesNotExistException;


    void unmarkLearnedTranslationUnit(long learningDatasetId, long translationUnitId)
            throws LearningDataSetDoesNotExistException, NoActiveFlashCardsGameException,
            TranslationUnitIsNotLearnedAndCannotBeUnmarkedException, TranslationUnitDoesNotExistException;
}
