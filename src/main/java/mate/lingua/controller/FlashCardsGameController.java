package mate.lingua.controller;

import lombok.AllArgsConstructor;
import mate.lingua.Constants;
import mate.lingua.exception.ResourceNotFoundException;
import mate.lingua.model.FlashCardsGame;
import mate.lingua.service.FlashCardsGameService;
import mate.lingua.service.LearningDatasetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class FlashCardsGameController {

    private FlashCardsGameService flashCardsGameService;
    private LearningDatasetService learningDatasetService;


    @PostMapping(value = "/learning-datasets/{learningDatasetId}/flashcards-games",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<FlashCardsGame> createFlashCardsGame(@PathVariable("learningDatasetId") Long learningDatasetId) {
        validateLearningDatasetExists(learningDatasetId);

        FlashCardsGame createdFlashCardsGame = flashCardsGameService.create(learningDatasetId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFlashCardsGame.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdFlashCardsGame);
    }

    @DeleteMapping("/learning-datasets/{learningDatasetId}/flashcards-games/{flashCardsGameId}")
    public ResponseEntity<FlashCardsGame> deleteFlashCardsGame(@PathVariable("learningDatasetId") Long learningDatasetId,
                                                               @PathVariable("flashCardsGameId") Long flashCardsGameId
    ) {
        validateLearningDatasetExists(learningDatasetId);

        if (flashCardsGameService.deleteById(flashCardsGameId)) {
            return ResponseEntity.noContent().build();
        }

        throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.FLASH_CARDS_GAME_WITH_ID_0_DOES_NOT_EXIST, flashCardsGameId));
    }

    @PatchMapping(value = "/learning-datasets/{learningDatasetId}/flashcards-games/{flashCardsGameId}",
            consumes = "application/json",
            produces = "application/json")
    ResponseEntity<FlashCardsGame> patchFlashCardsGame(@RequestBody FlashCardsGame flashCardsGame,
                                                       @PathVariable("learningDatasetId") Long learningDatasetId,
                                                       @PathVariable("flashCardsGameId") Long flashCardsGameId) {
        validateLearningDatasetExists(learningDatasetId);
        Optional<FlashCardsGame> optionalFlashCardsGame = flashCardsGameService.getById(flashCardsGameId);
        if (optionalFlashCardsGame.isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.FLASH_CARDS_GAME_WITH_ID_0_DOES_NOT_EXIST, flashCardsGameId));
        }
        FlashCardsGame updatedFlashCardsGame = flashCardsGameService.updateState(flashCardsGameId,
                flashCardsGame.getFlashCardsGameState());
        return ResponseEntity.ok(updatedFlashCardsGame);
    }

    @GetMapping(value = "/learning-datasets/{learningDatasetId}/flashcards-games/{flashCardsGameId}",
            produces = "application/json")
    ResponseEntity<FlashCardsGame> getFlashCardsGame(@PathVariable("learningDatasetId") Long learningDatasetId,
                                                     @PathVariable("flashCardsGameId") Long flashCardsGameId) {
        validateLearningDatasetExists(learningDatasetId);

        Optional<FlashCardsGame> optionalFlashCardsGame = flashCardsGameService.getById(flashCardsGameId);
        if (optionalFlashCardsGame.isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.FLASH_CARDS_GAME_WITH_ID_0_DOES_NOT_EXIST, flashCardsGameId));
        }
        return ResponseEntity.ok(optionalFlashCardsGame.get());
    }

    @GetMapping(value = "/learning-datasets/{learningDatasetId}/flashcards-games", produces = "application/json")
    public ResponseEntity<List<FlashCardsGame>> getFlashCardGames(@PathVariable("learningDatasetId") Long learningDatasetId) {
        validateLearningDatasetExists(learningDatasetId);

        List<FlashCardsGame> learningDatasets = flashCardsGameService.getFlashCardsGames();
        return ResponseEntity.ok(learningDatasets);
    }

    private void validateLearningDatasetExists(Long learningDatasetId) {
        if (learningDatasetService.getById(learningDatasetId).isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }
    }
}
