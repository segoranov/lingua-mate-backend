package mate.lingua.util;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class CollectionsUtil {
    public static <T> List<T> concat(List<T> list1, List<T> list2) {
        return Stream.concat(list1.stream(), list2.stream()).toList();
    }

    public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return list1.size() == list2.size() && new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}
