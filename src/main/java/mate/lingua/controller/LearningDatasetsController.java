package mate.lingua.controller;

import mate.lingua.model.LearningDataset;
import mate.lingua.model.TranslationUnit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning-datasets")
public class LearningDatasetsController {

    @GetMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<LearningDataset>> getLearningDatasets() {
        // TODO
        return null;
    }

    @GetMapping(value = "/{learningDatasetId}", produces = "application/json")
    public ResponseEntity<LearningDataset> getLearningDataset(@PathVariable("learningDatasetId") Long learningDatasetId) {
        // TODO
        return null;
    }

    @DeleteMapping(value = "/{learningDatasetId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteLearningDataset(@PathVariable("learningDatasetId") Long learningDatasetId) {
        // TODO
    }

    @PatchMapping(value = "/{learningDatasetId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LearningDataset> patchLearningDataset(@PathVariable("learningDatasetId") Long learningDatasetId) {
        // TODO
        return null;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<LearningDataset> createLearningDataset() {
        // TODO
        return null;
    }

    @PostMapping(value = "/{learningDatasetId}/translation-units", consumes = "application/json",
            produces = "application/json")
    public ResponseEntity<TranslationUnit> createTranslationUnit(@PathVariable("learningDatasetId") Long learningDatasetId) {
        // TODO
        return null;
    }

    @GetMapping(value = "/{learningDatasetId}/translation-units", produces = "application/json")
    public ResponseEntity<TranslationUnit> getTranslationUnits(@PathVariable("learningDatasetId") Long learningDatasetId) {
        // TODO
        return null;
    }

    @DeleteMapping(value = "/{learningDatasetId}/translation-units/{translationUnitId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTranslationUnit(@PathVariable("learningDatasetId") Long learningDatasetId,
                                      @PathVariable("translationUnitId") Long translationUnitId) {
        // TODO
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
