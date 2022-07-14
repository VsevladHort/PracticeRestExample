package entities;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GoodGroup implements Comparable<GoodGroup> {
    private String name;
    private String description;
    private final Map<String, Good> goods;
    private static final String ERROR = "Name should not be null";

    public GoodGroup(String name, String description) {
        if (name == null)
            throw new IllegalArgumentException(ERROR);
        this.name = name;
        this.description = description;
        goods = new ConcurrentHashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, Good> getGoods() {
        return goods;
    }

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException(ERROR);
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodGroup goodGroup = (GoodGroup) o;
        return name.equals(goodGroup.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(GoodGroup o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "GoodGroup{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
