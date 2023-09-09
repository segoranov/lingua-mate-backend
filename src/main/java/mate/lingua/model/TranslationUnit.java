package mate.lingua.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TranslationUnit {
    private String text;
    private String translation;
}
