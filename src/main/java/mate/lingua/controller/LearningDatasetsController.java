package mate.lingua.controller;

import lombok.AllArgsConstructor;
import mate.lingua.Constants;
import mate.lingua.exception.ResourceNotFoundException;
import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import mate.lingua.service.LearningDatasetService;
import mate.lingua.service.TranslationUnitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/learning-datasets")
@AllArgsConstructor
public class LearningDatasetsController {

    private TranslationUnitService translationUnitService;
    private LearningDatasetService learningDatasetService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<List<LearningDataset>> getLearningDatasets() {
        List<LearningDataset> learningDatasets = learningDatasetService.getLearningDatasets();
        return ResponseEntity.ok(learningDatasets);
    }

    @GetMapping(value = "/{learningDatasetId}", produces = "application/json")
    public ResponseEntity<LearningDataset> getLearningDataset(@PathVariable("learningDatasetId") Long learningDatasetId) {
        if (learningDatasetService.getById(learningDatasetId).isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }
        return ResponseEntity.ok(learningDatasetService.getById(learningDatasetId).get());
    }

    @DeleteMapping(value = "/{learningDatasetId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteLearningDataset(@PathVariable("learningDatasetId") Long learningDatasetId) {
        if (!learningDatasetService.deleteById(learningDatasetId)) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/{learningDatasetId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LearningDataset> patchLearningDataset(@PathVariable("learningDatasetId") Long learningDatasetId) {
        // TODO (add the partial patch object as request body)
        return null;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<LearningDataset> createLearningDataset(@RequestBody LearningDataset learningDataset) {
        LearningDataset savedLearningDataset = learningDatasetService.save(learningDataset);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedLearningDataset.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(learningDataset);
    }

    @PostMapping(value = "/{learningDatasetId}/translation-units", consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<TranslationUnit> createTranslationUnit(
            @PathVariable("learningDatasetId") Long learningDatasetId,
            @RequestBody TranslationUnit translationUnit) {
        Optional<LearningDataset> optionalLearningDataset = learningDatasetService.getById(learningDatasetId);
        if (optionalLearningDataset.isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }

        LearningDataset learningDataset = optionalLearningDataset.get();
        translationUnit.setLearningDataset(learningDataset);
        TranslationUnit savedTranslationUnit = translationUnitService.save(translationUnit);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedTranslationUnit.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(savedTranslationUnit);
    }

    @GetMapping(value = "/{learningDatasetId}/translation-units", produces = "application/json")
    public ResponseEntity<List<TranslationUnit>> getTranslationUnits(@PathVariable("learningDatasetId") Long learningDatasetId) {
        Optional<LearningDataset> optionalLearningDataset = learningDatasetService.getById(learningDatasetId);
        if (optionalLearningDataset.isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }

        return ResponseEntity.ok(optionalLearningDataset.get().getTranslationUnits());
    }

    @DeleteMapping(value = "/{learningDatasetId}/translation-units/{translationUnitId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTranslationUnit(@PathVariable("learningDatasetId") Long learningDatasetId,
                                                      @PathVariable("translationUnitId") Long translationUnitId) {
        // TODO if a game is in progress, and we delete unit, it will probably break something, investigate this
        if (learningDatasetService.getById(learningDatasetId).isEmpty()) {
            throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST, learningDatasetId));
        }

        if (translationUnitService.deleteById(translationUnitId)) {
            return ResponseEntity.noContent().build();
        }

        throw new ResourceNotFoundException(MessageFormat.format(Constants.Errors.TRANSLATION_UNIT_WITH_ID_0_DOES_NOT_EXIST, translationUnitId));
    }

    @PatchMapping(
            value = "/{learningDatasetId}/translation-units/{translationUnitId}",
            consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<TranslationUnit> patchTranslationUnit(@PathVariable("learningDatasetId") Long learningDatasetId,
                                                                @PathVariable("translationUnitId") Long translationUnitId) {
        // TODO
        return null;
    }
}
