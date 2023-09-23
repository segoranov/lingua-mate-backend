package mate.lingua.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FlashCardsGameState {
    private List<Long> learnedTranslationUnitsIds;
    private List<Long> notLearnedTranslationUnitsIds;
    private long currentlyLearnedTranslationUnitId;
}
