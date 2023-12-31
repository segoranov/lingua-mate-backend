package mate.lingua;

public class Constants {
    public static class Errors {
        public static final String FLASH_CARDS_GAME_FOR_LEARNING_DATASET_ALREADY_IN_PROGRESS =
                "Flash cards game for learning dataset is already in progress";
        public static final String CANNOT_START_FLASH_CARDS_GAME_BECAUSE_NO_TRANSLATION_UNITS_EXIST_IN_LEARNING_DATASET =
                "Cannot start flash cards game because no translation units exist in learning dataset";
        public static final String LEARNING_DATASET_WITH_ID_0_DOES_NOT_EXIST =
                "Learning dataset with id {0} does not exist";
        public static final String TRANSLATION_UNIT_WITH_ID_0_DOES_NOT_EXIST =
                "Translation unit with id {0} does not exist";
        public static final String NO_ACTIVE_FLASH_CARDS_GAME_EXISTS = "No active flash cards game exists";
        public static final String ALL_TRANSLATION_UNITS_ARE_ALREADY_LEARNED =
                "All translation units are already learned";
        public static final String CANNOT_UNMARK_TRANSLATION_UNIT_WHICH_IS_NOT_LEARNED =
                "Cannot unmark translation unit which is not learned";
    }
}
