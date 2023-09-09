package mate.lingua.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LearningDataset {
    private String name;
    private List<TranslationUnit> translationUnits;
}
