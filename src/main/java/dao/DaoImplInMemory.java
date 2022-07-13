package dao;

import entities.Good;
import entities.GoodGroup;
import entities.SomethingLikeInMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class DaoImplInMemory {
    private final SomethingLikeInMemoryDatabase dbInstance = SomethingLikeInMemoryDatabase.getInstance();

    public boolean createGroup(GoodGroup group) {
        if (dbInstance.groups.containsKey(group.getName()))
            return false;
        dbInstance.groups.put(group.getName(), group);
        return true;
    }


    public boolean createGood(String group, Good good) {
        var groupFromDb = dbInstance.groups.get(group);
        if (groupFromDb == null || groupFromDb.getGoods().containsKey(good.getName()))
            return false;
        groupFromDb.getGoods().put(good.getName(), good);
        return true;
    }


    public List<GoodGroup> getGroups(int limit, CriteriaGoodGroup criteria) {
        if (limit < 0)
            throw new IllegalArgumentException("Limit less than 0");
        var list = new ArrayList<>(dbInstance.groups.values());
        return list.subList(0, limit);
    }


    public List<Good> getGoods(String groupName, int limit, CriteriaGood criteria) {
        if (limit < 0)
            throw new IllegalArgumentException("Limit less than 0");
        var list = new ArrayList<>(dbInstance.groups.get(groupName).getGoods().values());
        return list.subList(0, limit);
    }


    public Good getGood(String groupName, String goodName) {
        return dbInstance.groups.get(groupName).getGoods().get(goodName);
    }


    public GoodGroup getGroup(String groupName) {
        return dbInstance.groups.get(groupName);
    }


    public boolean updateGroup(GoodGroup group) {
        if (dbInstance.groups.containsKey(group.getName())) {
            dbInstance.groups.put(group.getName(), group);
            return true;
        }
        return false;
    }


    public boolean updateGood(String groupName, Good good) {
        return createGood(groupName, good);
    }


    public boolean deleteGood(String groupName, String goodName) {
        var groupFromDb = dbInstance.groups.get(groupName);
        if (groupFromDb == null || !groupFromDb.getGoods().containsKey(goodName))
            return false;
        groupFromDb.getGoods().remove(goodName);
        return true;
    }


    public boolean deleteGoodGroup(String name) {
        var groupFromDb = dbInstance.groups.get(name);
        if (groupFromDb == null)
            return false;
        dbInstance.groups.remove(name);
        return true;
    }
}
