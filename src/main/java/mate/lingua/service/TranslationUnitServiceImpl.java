package mate.lingua.service;

import lombok.AllArgsConstructor;
import mate.lingua.model.TranslationUnit;
import mate.lingua.repository.TranslationUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TranslationUnitServiceImpl implements TranslationUnitService {

    private TranslationUnitRepository translationUnitRepository;

    @Override
    public List<TranslationUnit> getTranslationUnitsForLearningDataset(Long learningDatasetId) {
        return translationUnitRepository.findAll();
    }
}
