package entities;

import java.util.Objects;

public final class Good {
    private String name;
    private String description;
    private double price;
    private String producer;
    private int amount;
    private static final String ERROR = "Something is off, -_-";

    public Good(String name, String description, double price, String producer, int amount) {
        if (amount < 0 || price < 0 || name == null)
            throw new IllegalArgumentException(ERROR);
        this.name = name;
        this.description = description;
        this.price = price;
        this.producer = producer;
        this.amount = amount;
    }

    public Good(String name) {
        if (name == null)
            throw new IllegalArgumentException(ERROR);
        this.name = name;
        this.description = "NOT SET";
        this.price = Integer.MAX_VALUE;
        this.producer = "NOT SET";
        this.amount = 0;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0)
            throw new IllegalArgumentException(ERROR);
        this.price = price;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount < 0)
            throw new IllegalArgumentException(ERROR);
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Good good = (Good) o;
        return name.equals(good.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
