package mate.lingua.exception.service;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TranslationUnitDoesNotExistException extends Exception {
    private long translationUnitId;
}
