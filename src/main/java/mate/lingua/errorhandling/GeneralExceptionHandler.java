package mate.lingua.errorhandling;


import mate.lingua.Constants;
import mate.lingua.exception.FlashCardsGameAlreadyExistsException;
import mate.lingua.exception.InvalidFlashCardsGameStateException;
import mate.lingua.exception.NoTranslationUnitsToStartFlashCardsGameException;
import mate.lingua.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice
public class GeneralExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ApiError.builder().timestamp(new Date()).error(ex.getMessage()).build();
    }

    @ExceptionHandler(FlashCardsGameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleFlashCardsGameAlreadyExistsException(FlashCardsGameAlreadyExistsException ex) {
        return ApiError
                .builder()
                .timestamp(new Date())
                .error(Constants.Errors.FLASH_CARDS_GAME_FOR_LEARNING_DATASET_ALREADY_IN_PROGRESS)
                .build();
    }

    @ExceptionHandler(NoTranslationUnitsToStartFlashCardsGameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleFlashCardsGameAlreadyExistsException(NoTranslationUnitsToStartFlashCardsGameException ex) {
        return ApiError
                .builder()
                .timestamp(new Date())
                .error(Constants.Errors.CANNOT_START_FLASH_CARDS_GAME_BECAUSE_NO_TRANSLATION_UNITS_EXIST_IN_LEARNING_DATASET)
                .build();
    }

    @ExceptionHandler(InvalidFlashCardsGameStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidFlashCardsGameStateException(InvalidFlashCardsGameStateException ex) {
        return ApiError
                .builder()
                .timestamp(new Date())
                .error(ex.getMessage())
                .build();
    }
}
