package mate.lingua.service;

import lombok.AllArgsConstructor;
import mate.lingua.model.TranslationUnit;
import mate.lingua.repository.TranslationUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TranslationUnitServiceImpl implements TranslationUnitService {

    private TranslationUnitRepository translationUnitRepository;

    @Override
    public List<TranslationUnit> getTranslationUnitsForLearningDataset(Long learningDatasetId) {
        return translationUnitRepository.findAll();
    }

    @Override
    public TranslationUnit save(TranslationUnit translationUnit) {
        return translationUnitRepository.save(translationUnit);
    }

    @Override
    public Optional<TranslationUnit> getById(Long translationUnitId) {
        return translationUnitRepository.findById(translationUnitId);
    }

    @Override
    public void deleteById(Long translationUnitId) {
        translationUnitRepository.deleteById(translationUnitId);
    }
}
