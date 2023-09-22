package mate.lingua.service;

import mate.lingua.model.TranslationUnit;

import java.util.Optional;

public interface TranslationUnitService {

    TranslationUnit save(TranslationUnit translationUnit);

    Optional<TranslationUnit> getById(Long translationUnitId);

    boolean deleteById(Long translationUnitId);
}
