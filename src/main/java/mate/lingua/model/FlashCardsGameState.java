package mate.lingua.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Queue;

@Getter
@Builder
public class FlashCardsGameState {
    @JsonProperty("learnedTranslationUnitsIds")
    private List<Long> learnedTranslationUnitsIdsList;
    @JsonProperty("notLearnedTranslationUnitsIds")
    private Queue<Long> notLearnedTranslationUnitsIdsQueue;
    @Setter
    private TranslationUnit currentlyLearnedTranslationUnit;
}
