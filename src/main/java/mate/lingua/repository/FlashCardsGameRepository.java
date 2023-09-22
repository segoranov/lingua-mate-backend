package mate.lingua.repository;

import mate.lingua.model.FlashCardsGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashCardsGameRepository extends JpaRepository<FlashCardsGame, Long> {
}
