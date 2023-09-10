package mate.lingua.repository;

import mate.lingua.model.LearningDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningDatasetRepository extends JpaRepository<LearningDataset, Long> {
}
