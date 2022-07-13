import entities.Good;
import entities.GoodGroup;
import entities.utils.GoodJsonConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TestJsonSerialization {
    @Test
    void testGood() {
        Good good = new Good("name", "desc", 10, "producer", 100);
        String json = GoodJsonConverter.toJson(good);
        Good good1 = GoodJsonConverter.fromJsonGood(json);
        Assertions.assertEquals(good.getName(), good1.getName());
        Assertions.assertEquals(good.getPrice(), good1.getPrice());
        Assertions.assertEquals(good.getDescription(), good1.getDescription());
        Assertions.assertEquals(good.getAmount(), good1.getAmount());
        Assertions.assertEquals(good.getProducer(), good1.getProducer());
    }

    @Test
    void testGroup() {
        GoodGroup good = new GoodGroup("name", "desc");
        String json = GoodJsonConverter.toJson(good);
        GoodGroup good1 = GoodJsonConverter.fromJsonGroup(json);
        Assertions.assertEquals(good.getName(), good1.getName());
        Assertions.assertEquals(good.getDescription(), good1.getDescription());
    }
}
