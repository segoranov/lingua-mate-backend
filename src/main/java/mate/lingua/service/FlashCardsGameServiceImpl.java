package mate.lingua.service;

import lombok.AllArgsConstructor;
import mate.lingua.Constants;
import mate.lingua.exception.FlashCardsGameAlreadyExistsException;
import mate.lingua.exception.NoTranslationUnitsToStartFlashCardsGameException;
import mate.lingua.exception.ResourceNotFoundException;
import mate.lingua.model.FlashCardsGame;
import mate.lingua.model.FlashCardsGameState;
import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import mate.lingua.repository.FlashCardsGameRepository;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FlashCardsGameServiceImpl implements FlashCardsGameService {

    private FlashCardsGameRepository flashCardsGameRepository;
    private LearningDatasetService learningDatasetService;

    @Override
    public FlashCardsGame create(long learningDatasetId) {
        validateLearningDataset(learningDatasetId);

        LearningDataset learningDataset = learningDatasetService.getById(learningDatasetId).get();
        return flashCardsGameRepository.save(FlashCardsGame
                .builder()
                .flashCardsGameState(createInitialFlashCardsGameState(learningDataset))
                .learningDataset(learningDataset)
                .build());
    }

    private void validateLearningDataset(long learningDatasetId) {
        Optional<LearningDataset> optionalLearningDataset = learningDatasetService.getById(learningDatasetId);
        if (optionalLearningDataset.isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST,
                    learningDatasetId));
        }

        LearningDataset learningDataset = optionalLearningDataset.get();
        if (learningDataset.getFlashCardsGame() != null) {
            throw new FlashCardsGameAlreadyExistsException();
        }

        if (learningDataset.getTranslationUnits().isEmpty()) {
            throw new NoTranslationUnitsToStartFlashCardsGameException();
        }
    }

    private FlashCardsGameState createInitialFlashCardsGameState(LearningDataset learningDataset) {
        List<Long> translationUnitIds = learningDataset
                .getTranslationUnits()
                .stream()
                .map(TranslationUnit::getId)
                .toList();

        return FlashCardsGameState
                .builder()
                .notLearnedTranslationUnitsIds(translationUnitIds)
                .learnedTranslationUnitsIds(Collections.emptyList())
                .currentlyLearnedTranslationUnitId(translationUnitIds.get(0))
                .build();
    }

    @Override
    public Optional<FlashCardsGame> getById(long id) {
        return flashCardsGameRepository.findById(id);
    }

    @Override
    public List<FlashCardsGame> getFlashCardsGames() {
        return flashCardsGameRepository.findAll();
    }

    @Override
    public boolean deleteById(long id) {
        if (flashCardsGameRepository.existsById(id)) {
            flashCardsGameRepository.deleteById(id);
            return true;
        }
        return false;
    }
}