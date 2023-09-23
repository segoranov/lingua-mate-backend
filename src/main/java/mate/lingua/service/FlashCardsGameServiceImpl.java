package mate.lingua.service;

import lombok.AllArgsConstructor;
import mate.lingua.exception.service.*;
import mate.lingua.model.FlashCardsGame;
import mate.lingua.model.FlashCardsGameState;
import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import mate.lingua.repository.FlashCardsGameRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class FlashCardsGameServiceImpl implements FlashCardsGameService {

    private FlashCardsGameRepository flashCardsGameRepository;
    private LearningDatasetService learningDatasetService;
    private TranslationUnitService translationUnitService;

    @Override
    public void startGame(long learningDatasetId)
            throws NoTranslationUnitsToStartFlashCardsGameException, FlashCardsGameAlreadyExistsException,
            LearningDataSetDoesNotExistException {
        validateLearningDataset(learningDatasetId);

        LearningDataset learningDataset = learningDatasetService.getById(learningDatasetId).get();
        flashCardsGameRepository.save(FlashCardsGame
                .builder()
                .flashCardsGameState(createInitialFlashCardsGameState(learningDataset))
                .learningDataset(learningDataset)
                .build());
    }

    @Override
    public void finishGame(long learningDatasetId)
            throws NoActiveFlashCardsGameException, LearningDataSetDoesNotExistException {
        LearningDataset learningDataset = getLearningDataset(learningDatasetId);
        if (learningDataset.getFlashCardsGame() == null) {
            throw new NoActiveFlashCardsGameException();
        }

        flashCardsGameRepository.deleteById(learningDataset.getFlashCardsGame().getId());
    }

    private void validateLearningDataset(long learningDatasetId)
            throws FlashCardsGameAlreadyExistsException, NoTranslationUnitsToStartFlashCardsGameException,
            LearningDataSetDoesNotExistException {
        LearningDataset learningDataset = getLearningDataset(learningDatasetId);
        if (learningDataset.getFlashCardsGame() != null) {
            throw new FlashCardsGameAlreadyExistsException();
        }

        if (learningDataset.getTranslationUnits().isEmpty()) {
            throw new NoTranslationUnitsToStartFlashCardsGameException();
        }
    }

    private FlashCardsGameState createInitialFlashCardsGameState(LearningDataset learningDataset) {
        Queue<TranslationUnit> translationUnits = new LinkedList<>(learningDataset.getTranslationUnits());
        TranslationUnit currentlyLearnedTranslationUnit = translationUnits.poll();
        LinkedList<Long> translationUnitsIds =
                new LinkedList<>(translationUnits.stream().map(TranslationUnit::getId).toList());

        return FlashCardsGameState
                .builder()
                .notLearnedTranslationUnitsIdsQueue(translationUnitsIds)
                .learnedTranslationUnitsIdsList(Collections.emptyList())
                .currentlyLearnedTranslationUnit(currentlyLearnedTranslationUnit)
                .build();
    }

    @Override
    public void markCurrentlyLearnedTranslationUnitAsLearned(long learningDatasetId)
            throws LearningDataSetDoesNotExistException, NoActiveFlashCardsGameException,
            NoCurrentlyLearnedTranslationUnitException {
        FlashCardsGame flashCardsGame = getActiveGame(learningDatasetId);
        FlashCardsGameState flashCardsGameState = flashCardsGame.getFlashCardsGameState();

        if (flashCardsGameState.getCurrentlyLearnedTranslationUnit() == null) {
            throw new NoCurrentlyLearnedTranslationUnitException();
        }

        TranslationUnit currentlyLearnedTranslationUnit = flashCardsGameState.getCurrentlyLearnedTranslationUnit();
        flashCardsGameState.getLearnedTranslationUnitsIdsList().add(currentlyLearnedTranslationUnit.getId());
        flashCardsGameState.setCurrentlyLearnedTranslationUnit(getNextToLearnTranslationUnit(flashCardsGameState));
        flashCardsGameRepository.save(flashCardsGame);
    }

    @Override
    public void markCurrentlyLearnedTranslationUnitForRepetition(long learningDatasetId) throws LearningDataSetDoesNotExistException, NoActiveFlashCardsGameException, NoCurrentlyLearnedTranslationUnitException {
        FlashCardsGame flashCardsGame = getActiveGame(learningDatasetId);
        FlashCardsGameState flashCardsGameState = flashCardsGame.getFlashCardsGameState();

        if (flashCardsGameState.getCurrentlyLearnedTranslationUnit() == null) {
            throw new NoCurrentlyLearnedTranslationUnitException();
        }

        TranslationUnit currentlyLearnedTranslationUnit = flashCardsGameState.getCurrentlyLearnedTranslationUnit();
        flashCardsGameState.getNotLearnedTranslationUnitsIdsQueue().offer(currentlyLearnedTranslationUnit.getId());
        flashCardsGameState.setCurrentlyLearnedTranslationUnit(getNextToLearnTranslationUnit(flashCardsGameState));
        flashCardsGameRepository.save(flashCardsGame);
    }

    private TranslationUnit getNextToLearnTranslationUnit(FlashCardsGameState flashCardsGameState) {
        if (flashCardsGameState.getNotLearnedTranslationUnitsIdsQueue().isEmpty()) {
            // This means that all are learned and hence the next will be null, i.e. nothing to learn more
            return null;
        }

        long nextToLearnTranslationUnitId = flashCardsGameState.getNotLearnedTranslationUnitsIdsQueue().poll();
        return translationUnitService.getById(nextToLearnTranslationUnitId).get(); // TODO what if it does not exist?
    }

    @Override
    public void unmarkLearnedTranslationUnit(long learningDatasetId, long translationUnitId)
            throws LearningDataSetDoesNotExistException, NoActiveFlashCardsGameException,
            TranslationUnitIsNotLearnedAndCannotBeUnmarkedException, TranslationUnitDoesNotExistException {
        FlashCardsGame flashCardsGame = getActiveGame(learningDatasetId);
        FlashCardsGameState flashCardsGameState = flashCardsGame.getFlashCardsGameState();

        TranslationUnit translationUnit = getTranslationUnit(translationUnitId);

        List<Long> learnedTranslationUnitsIdsList = flashCardsGameState.getLearnedTranslationUnitsIdsList();
        if (!learnedTranslationUnitsIdsList.contains(translationUnit.getId())) {
            throw new TranslationUnitIsNotLearnedAndCannotBeUnmarkedException();
        }

        learnedTranslationUnitsIdsList.remove(translationUnit.getId());
        Queue<Long> notLearnedTranslationUnitsIdsQueue = flashCardsGameState.getNotLearnedTranslationUnitsIdsQueue();

        if (flashCardsGameState.getCurrentlyLearnedTranslationUnit() != null) {
            notLearnedTranslationUnitsIdsQueue.offer(translationUnit.getId());
        } else {
            flashCardsGameState.setCurrentlyLearnedTranslationUnit(translationUnit);
        }

        flashCardsGameRepository.save(flashCardsGame);
    }

    @Override
    public FlashCardsGame getActiveGame(long learningDatasetId)
            throws NoActiveFlashCardsGameException, LearningDataSetDoesNotExistException {
        LearningDataset learningDataset = getLearningDataset(learningDatasetId);
        if (learningDataset.getFlashCardsGame() == null) {
            throw new NoActiveFlashCardsGameException();
        }
        return learningDataset.getFlashCardsGame();
    }

    private LearningDataset getLearningDataset(long learningDatasetId) throws LearningDataSetDoesNotExistException {
        Optional<LearningDataset> optionalLearningDataset = learningDatasetService.getById(learningDatasetId);
        if (optionalLearningDataset.isEmpty()) {
            throw LearningDataSetDoesNotExistException.builder().learningDataSetId(learningDatasetId).build();
        }

        return optionalLearningDataset.get();
    }

    private TranslationUnit getTranslationUnit(long translationUnitId) throws TranslationUnitDoesNotExistException {
        Optional<TranslationUnit> optionalTranslationUnit = translationUnitService.getById(translationUnitId);
        if (optionalTranslationUnit.isEmpty()) {
            throw TranslationUnitDoesNotExistException.builder().translationUnitId(translationUnitId).build();
        }

        return optionalTranslationUnit.get();
    }
}
