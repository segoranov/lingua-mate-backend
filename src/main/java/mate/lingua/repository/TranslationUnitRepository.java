package mate.lingua.repository;

import mate.lingua.model.TranslationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationUnitRepository extends JpaRepository<TranslationUnit, Long> {
}
