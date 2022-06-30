package entities;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SomethingLikeInMemoryDatabase {
    public final Map<String, GoodGroup> groups;
    private static SomethingLikeInMemoryDatabase instance;

    private SomethingLikeInMemoryDatabase(Map<String, GoodGroup> set) {
        groups = set;
    }

    public static SomethingLikeInMemoryDatabase getInstance() {
        synchronized (Good.class) {
            if (instance == null) {
                instance = new SomethingLikeInMemoryDatabase(new ConcurrentHashMap<>());
            }
            return instance;
        }
    }
}
