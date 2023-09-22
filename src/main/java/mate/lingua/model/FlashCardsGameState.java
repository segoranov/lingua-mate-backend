package mate.lingua.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlashCardsGameState {
    private List<Long> learnedTranslationUnitsIds;
    private List<Long> notLearnedTranslationUnitsIds;
    private Long currentlyLearnedTranslationUnitId;
}
