package mate.lingua.service;

import lombok.AllArgsConstructor;
import mate.lingua.model.LearningDataset;
import mate.lingua.repository.LearningDatasetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LearningDatasetServiceImpl implements LearningDatasetService {

    private LearningDatasetRepository learningDatasetRepository;

    @Override
    public List<LearningDataset> getLearningDatasets() {
        return learningDatasetRepository.findAll();
    }

    @Override
    public Optional<LearningDataset> getById(Long id) {
        return learningDatasetRepository.findById(id);
    }

    @Override
    public LearningDataset save(LearningDataset learningDataset) {
        return learningDatasetRepository.save(learningDataset);
    }

    @Override
    public boolean deleteById(Long learningDatasetId) {
        if (learningDatasetRepository.existsById(learningDatasetId)) {
            learningDatasetRepository.deleteById(learningDatasetId);
            return true;
        }
        return false;
    }
}
