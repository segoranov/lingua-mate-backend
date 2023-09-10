package mate.lingua.service;


import mate.lingua.model.LearningDataset;

import java.util.List;
import java.util.Optional;

public interface LearningDatasetService {
    List<LearningDataset> getLearningDatasets();

    Optional<LearningDataset> getById(Long id);
}
