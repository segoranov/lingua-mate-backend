package mate.lingua.controller;

import lombok.AllArgsConstructor;
import mate.lingua.Constants;
import mate.lingua.exception.BadRequestException;
import mate.lingua.exception.ResourceNotFoundException;
import mate.lingua.exception.service.*;
import mate.lingua.model.FlashCardsGame;
import mate.lingua.model.FlashCardsGameState;
import mate.lingua.model.IdRequest;
import mate.lingua.service.FlashCardsGameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class FlashCardsGameController {

    private FlashCardsGameService flashCardsGameService;

    @PostMapping(value = "/learning-datasets/{learningDatasetId}/actions/start-flashcards-game",
            produces = "application/json")
    public ResponseEntity<Void> startFlashCardsGame(@PathVariable("learningDatasetId") Long learningDatasetId) {
        try {
            flashCardsGameService.startGame(learningDatasetId);
        } catch (NoTranslationUnitsToStartFlashCardsGameException e) {
            throw new BadRequestException(Constants.Errors.CANNOT_START_FLASH_CARDS_GAME_BECAUSE_NO_TRANSLATION_UNITS_EXIST_IN_LEARNING_DATASET);
        } catch (FlashCardsGameAlreadyExistsException e) {
            throw new BadRequestException(Constants.Errors.FLASH_CARDS_GAME_FOR_LEARNING_DATASET_ALREADY_IN_PROGRESS);
        } catch (LearningDataSetDoesNotExistException e) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/learning-datasets/{learningDatasetId}/actions/finish-flashcards-game")
    public ResponseEntity<Void> finishFlashCardsGame(@PathVariable("learningDatasetId") Long learningDatasetId) {
        try {
            flashCardsGameService.finishGame(learningDatasetId);
        } catch (NoActiveFlashCardsGameException e) {
            throw new BadRequestException(Constants.Errors.NO_ACTIVE_FLASH_CARDS_GAME_EXISTS);
        } catch (LearningDataSetDoesNotExistException e) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/learning-datasets/{learningDatasetId}/active-flashcards-game/state",
            produces = "application/json")
    ResponseEntity<FlashCardsGameState> getActiveFlashCardsGameState(@PathVariable("learningDatasetId") Long learningDatasetId) {
        return ResponseEntity.ok(getActiveGame(learningDatasetId).getFlashCardsGameState());
    }

    @PostMapping(value = "/learning-datasets/{learningDatasetId}/active-flashcards-game/actions/mark-learned")
    public ResponseEntity<Void> markCurrentlyLearnedTranslationUnitAsLearned(@PathVariable("learningDatasetId") Long learningDatasetId) {
        try {
            flashCardsGameService.markCurrentlyLearnedTranslationUnitAsLearned(learningDatasetId);
        } catch (LearningDataSetDoesNotExistException e) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        } catch (NoActiveFlashCardsGameException e) {
            throw new BadRequestException(Constants.Errors.NO_ACTIVE_FLASH_CARDS_GAME_EXISTS);
        } catch (NoCurrentlyLearnedTranslationUnitException e) {
            throw new BadRequestException(Constants.Errors.ALL_TRANSLATION_UNITS_ARE_ALREADY_LEARNED);
        }

        return ResponseEntity.noContent().build();
    }

    private FlashCardsGame getActiveGame(long learningDatasetId) {
        try {
            return flashCardsGameService.getActiveGame(learningDatasetId);
        } catch (NoActiveFlashCardsGameException e) {
            throw new BadRequestException(Constants.Errors.NO_ACTIVE_FLASH_CARDS_GAME_EXISTS);
        } catch (LearningDataSetDoesNotExistException e) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }
    }

    @PostMapping(value = "/learning-datasets/{learningDatasetId}/active-flashcards-game/actions/repeat")
    public ResponseEntity<Void> markCurrentlyLearnedTranslationUnitForRepetition(
            @PathVariable("learningDatasetId") Long learningDatasetId) {
        try {
            flashCardsGameService.markCurrentlyLearnedTranslationUnitForRepetition(learningDatasetId);
        } catch (LearningDataSetDoesNotExistException e) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        } catch (NoActiveFlashCardsGameException e) {
            throw new BadRequestException(Constants.Errors.NO_ACTIVE_FLASH_CARDS_GAME_EXISTS);
        } catch (NoCurrentlyLearnedTranslationUnitException e) {
            throw new BadRequestException(Constants.Errors.ALL_TRANSLATION_UNITS_ARE_ALREADY_LEARNED);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/learning-datasets/{learningDatasetId}/active-flashcards-game/actions/unmark-learned",
            consumes = "application/json")
    public ResponseEntity<Void> unmarkLearnedTranslationUnit(@RequestBody IdRequest translationUnitIdRequest,
                                                             @PathVariable("learningDatasetId") Long learningDatasetId) {
        try {
            flashCardsGameService.unmarkLearnedTranslationUnit(learningDatasetId, translationUnitIdRequest.getId());
        } catch (LearningDataSetDoesNotExistException e) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        } catch (NoActiveFlashCardsGameException e) {
            throw new BadRequestException(Constants.Errors.NO_ACTIVE_FLASH_CARDS_GAME_EXISTS);
        } catch (TranslationUnitIsNotLearnedAndCannotBeUnmarkedException e) {
            throw new BadRequestException(Constants.Errors.CANNOT_UNMARK_TRANSLATION_UNIT_WHICH_IS_NOT_LEARNED);
        } catch (TranslationUnitDoesNotExistException e) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.TRANSLATION_UNIT_WITH_ID_0_DOES_NOT_EXIST, translationUnitIdRequest.getId()));
        }

        return ResponseEntity.noContent().build();
    }
}
