package mate.lingua.service;

import mate.lingua.model.TranslationUnit;

import java.util.List;

public interface TranslationUnitService {
    List<TranslationUnit> getTranslationUnitsForLearningDataset(Long learningDatasetId);
}
