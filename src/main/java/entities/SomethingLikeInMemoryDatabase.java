package entities;

import java.util.HashSet;
import java.util.Set;

public class SomethingLikeInMemoryDatabase {
    public final Set<GoodGroup> groups;
    private static SomethingLikeInMemoryDatabase instance;

    private SomethingLikeInMemoryDatabase(Set<GoodGroup> set) {
        groups = set;
    }

    public static SomethingLikeInMemoryDatabase getInstance() {
        synchronized (Good.class) {
            if (instance == null) {
                instance = new SomethingLikeInMemoryDatabase(new HashSet<>());
            }
            return instance;
        }
    }
}
