package entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class GoodGroup {
    private String name;
    private String description;
    private Set<Good> goods;
    private static final String ERROR = "Name should not be null";

    public GoodGroup(String name, String description) {
        if (name == null)
            throw new IllegalArgumentException(ERROR);
        this.name = name;
        this.description = description;
        goods = new HashSet<>();
    }

    public String getName() {
        return name;
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
}
