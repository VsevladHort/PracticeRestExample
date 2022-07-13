import dao.CriteriaGood;
import dao.CriteriaGoodGroup;
import dao.DBService;
import dao.Dao;
import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;
import org.junit.jupiter.api.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

class DaoTest {
    private static final String TEST_DB = "test";
    private static Dao dao;

    @BeforeAll
    static void before() throws DaoWrapperException {
        DBService.initializeConnection(TEST_DB);
        dao = new DBService(TEST_DB);
    }

    @BeforeEach
    public void beforeEach() throws DaoWrapperException {
        dao = new DBService(TEST_DB);
    }

    @Test
    void testGroupInsertion() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        dao.createGroup(group);
        var gottenGroup = dao.getGroup("1");
        Assertions.assertEquals(group.getName(), gottenGroup.getName());
        Assertions.assertEquals(group.getDescription(), gottenGroup.getDescription());
    }

    @Test
    void testGroupUpdate() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        dao.createGroup(group);
        group.setDescription("NewDesc");
        dao.updateGroup(group);
        var gottenGroup = dao.getGroup("1");
        Assertions.assertEquals(group.getDescription(), gottenGroup.getDescription());
    }

    @Test
    void testGroupDelete() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        dao.createGroup(group);
        group.setDescription("NewDesc");
        dao.deleteGoodGroup(group.getName());
        var gottenGroup = dao.getGroup("1");
        Assertions.assertNull(gottenGroup);
    }

    @Test
    void testGroupListByName() throws DaoWrapperException {
        var group1 = new GoodGroup("1", "c");
        var group2 = new GoodGroup("2", "b");
        var group3 = new GoodGroup("3", "a");
        dao.createGroup(group1);
        dao.createGroup(group2);
        dao.createGroup(group3);
        var gottenGroupsName = dao.getGroups(2, CriteriaGoodGroup.NAME);
        var expectedOrderName = new ArrayList<GoodGroup>();
        expectedOrderName.add(group3);
        expectedOrderName.add(group2);
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(expectedOrderName.get(i).getName(), gottenGroupsName.get(i).getName());
        }
    }

    @Test
    void testGroupListByDescription() throws DaoWrapperException {
        var group1 = new GoodGroup("1", "c");
        var group2 = new GoodGroup("2", "b");
        var group3 = new GoodGroup("3", "a");
        dao.createGroup(group1);
        dao.createGroup(group2);
        dao.createGroup(group3);
        var gottenGroupsDescription = dao.getGroups(2, CriteriaGoodGroup.DESCRIPTION);
        var expectedOrderDescription = new ArrayList<GoodGroup>();
        expectedOrderDescription.add(group1);
        expectedOrderDescription.add(group2);
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(expectedOrderDescription.get(i).getDescription(), gottenGroupsDescription.get(i).getDescription());
        }
    }

    @Test
    void testGoodInsertion() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good = new Good("good");
        dao.createGroup(group);
        dao.createGood(group.getName(), good);
        var gottenGood = dao.getGood("good");
        Assertions.assertEquals(good.getName(), gottenGood.getName());
    }

    @Test
    void testGoodListByName() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good1 = new Good("1", "a", 3, "c", 1);
        var good2 = new Good("2", "b", 2, "b", 2);
        var good3 = new Good("3", "c", 1, "a", 3);
        dao.createGroup(group);
        dao.createGood(group.getName(), good1);
        dao.createGood(group.getName(), good2);
        dao.createGood(group.getName(), good3);
        var gottenGoods = dao.getGoods(group.getName(), 2, CriteriaGood.NAME);
        var expectedGottenGoods = new ArrayList<Good>();
        expectedGottenGoods.add(good3);
        expectedGottenGoods.add(good2);
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(expectedGottenGoods.get(i).getDescription(), gottenGoods.get(i).getDescription());
        }
    }

    @Test
    void testGoodListByDescription() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good1 = new Good("1", "a", 3, "c", 1);
        var good2 = new Good("2", "b", 2, "b", 2);
        var good3 = new Good("3", "c", 1, "a", 3);
        dao.createGroup(group);
        dao.createGood(group.getName(), good1);
        dao.createGood(group.getName(), good2);
        dao.createGood(group.getName(), good3);
        var gottenGoods = dao.getGoods(group.getName(), 2, CriteriaGood.DESCRIPTION);
        var expectedGottenGoods = new ArrayList<Good>();
        expectedGottenGoods.add(good3);
        expectedGottenGoods.add(good2);
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(expectedGottenGoods.get(i).getDescription(), gottenGoods.get(i).getDescription());
        }
    }

    @Test
    void testGoodListByPrice() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good1 = new Good("1", "a", 3, "c", 1);
        var good2 = new Good("2", "b", 2, "b", 2);
        var good3 = new Good("3", "c", 1, "a", 3);
        dao.createGroup(group);
        dao.createGood(group.getName(), good1);
        dao.createGood(group.getName(), good2);
        dao.createGood(group.getName(), good3);
        var gottenGoods = dao.getGoods(group.getName(), 2, CriteriaGood.PRICE);
        var expectedGottenGoods = new ArrayList<Good>();
        expectedGottenGoods.add(good1);
        expectedGottenGoods.add(good2);
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(expectedGottenGoods.get(i).getDescription(), gottenGoods.get(i).getDescription());
        }
    }

    @Test
    void testGoodListByProducer() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good1 = new Good("1", "a", 3, "c", 1);
        var good2 = new Good("2", "b", 2, "b", 2);
        var good3 = new Good("3", "c", 1, "a", 3);
        dao.createGroup(group);
        dao.createGood(group.getName(), good1);
        dao.createGood(group.getName(), good2);
        dao.createGood(group.getName(), good3);
        var gottenGoods = dao.getGoods(group.getName(), 2, CriteriaGood.PRODUCER);
        var expectedGottenGoods = new ArrayList<Good>();
        expectedGottenGoods.add(good1);
        expectedGottenGoods.add(good2);
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(expectedGottenGoods.get(i).getDescription(), gottenGoods.get(i).getDescription());
        }
    }

    @Test
    void testGoodListByAmount() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good1 = new Good("1", "a", 3, "c", 1);
        var good2 = new Good("2", "b", 2, "b", 2);
        var good3 = new Good("3", "c", 1, "a", 3);
        dao.createGroup(group);
        dao.createGood(group.getName(), good1);
        dao.createGood(group.getName(), good2);
        dao.createGood(group.getName(), good3);
        var gottenGoods = dao.getGoods(group.getName(), 2, CriteriaGood.AMOUNT);
        var expectedGottenGoods = new ArrayList<Good>();
        expectedGottenGoods.add(good3);
        expectedGottenGoods.add(good2);
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals(expectedGottenGoods.get(i).getDescription(), gottenGoods.get(i).getDescription());
        }
    }

    @Test
    void testGoodUpdate() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good = new Good("good");
        dao.createGroup(group);
        dao.createGood(group.getName(), good);
        good.setPrice(10);
        dao.updateGood(good);
        var gottenGood = dao.getGood("good");
        Assertions.assertEquals(good.getPrice(), gottenGood.getPrice());
    }

    @Test
    void testGoodDelete() throws DaoWrapperException {
        var group = new GoodGroup("1", "desc");
        var good = new Good("good");
        dao.createGroup(group);
        dao.createGood(group.getName(), good);
        dao.deleteGood(good.getName());
        var gottenGood = dao.getGood("good");
        Assertions.assertNull(gottenGood);
    }

    @AfterEach
    void afterEach() {
        var con = DBService.getConnection();
        try (PreparedStatement select1 =
                     con.prepareStatement(
                             """
                                      DROP TABLE IF EXISTS users;
                                     """
                     );
             PreparedStatement select2 =
                     con.prepareStatement(
                             """
                                      DROP TABLE IF EXISTS goods;
                                     """
                     );
             PreparedStatement select3 =
                     con.prepareStatement(
                             """
                                      DROP TABLE IF EXISTS good_groups;
                                     """
                     )) {
            select1.executeUpdate();
            select2.executeUpdate();
            select3.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
