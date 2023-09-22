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
        public static final String FLASH_CARDS_GAME_WITH_ID_0_DOES_NOT_EXIST =
                "Flash cards game with id {0} does not exist";
        public static final String FLASH_CARDS_GAME_STATE_TRANSLATION_UNIT_IDS_DO_NOT_MATCH_THOSE_FROM_THE_CORRESPONDING_LEARNING_DATASET =
                "Flash cards game state translation unit IDs do no match those from the corresponding learning dataset";

        public static final String FLASH_CARDS_GAME_STATE_IS_INCOMPLETE = "Flash cards game state is incomplete";
        public static final String CURRENTLY_LEARNED_TRANSLATION_UNIT_ID_IS_NOT_VALID =
                "Currently learned translation unit id is not valid";
        public static final String CURRENTLY_LEARNED_TRANSLATION_UNIT_ID_CANNOT_BE_PRESENT_IN_LEARNED_TRANSLATION_UNIT_IDS_LIST =
                "Currently learned translation unit id cannot be present in the learned translation unit ids list";
    }
}
