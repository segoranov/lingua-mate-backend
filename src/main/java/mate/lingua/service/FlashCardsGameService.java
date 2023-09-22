package mate.lingua.service;

import mate.lingua.model.FlashCardsGame;
import mate.lingua.model.FlashCardsGameState;

import java.util.List;
import java.util.Optional;

public interface FlashCardsGameService {
    FlashCardsGame create(long learningDatasetId);

    Optional<FlashCardsGame> getById(long id);

    List<FlashCardsGame> getFlashCardsGames();

    boolean deleteById(long id);

    FlashCardsGame updateState(long flashCardsGameId, FlashCardsGameState newFlashCardsGameState);
}
