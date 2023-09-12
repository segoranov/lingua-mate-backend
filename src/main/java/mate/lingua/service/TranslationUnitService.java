package mate.lingua.service;

import mate.lingua.model.TranslationUnit;

import java.util.List;
import java.util.Optional;

public interface TranslationUnitService {
    List<TranslationUnit> getTranslationUnitsForLearningDataset(Long learningDatasetId);

    TranslationUnit save(TranslationUnit translationUnit);

    Optional<TranslationUnit> getById(Long translationUnitId);

    void deleteById(Long translationUnitId);
}
