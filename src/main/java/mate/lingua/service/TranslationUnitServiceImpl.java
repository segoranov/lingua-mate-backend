package mate.lingua.service;

import lombok.AllArgsConstructor;
import mate.lingua.model.TranslationUnit;
import mate.lingua.repository.TranslationUnitRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class TranslationUnitServiceImpl implements TranslationUnitService {

    private TranslationUnitRepository translationUnitRepository;

    @Override
    public TranslationUnit save(TranslationUnit translationUnit) {
        return translationUnitRepository.save(translationUnit);
    }

    @Override
    public Optional<TranslationUnit> getById(Long translationUnitId) {
        return translationUnitRepository.findById(translationUnitId);
    }

    @Override
    public boolean deleteById(Long translationUnitId) {
        if (translationUnitRepository.existsById(translationUnitId)) {
            translationUnitRepository.deleteById(translationUnitId);
            return true;
        }
        return false;
    }
}
