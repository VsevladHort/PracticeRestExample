package dao;

import entities.Good;
import entities.GoodGroup;
import entities.SomethingLikeInMemoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class DaoImplInMemory implements Dao {
    private final SomethingLikeInMemoryDatabase dbInstance = SomethingLikeInMemoryDatabase.getInstance();

    @Override
    public boolean addGroup(GoodGroup group) {
        if (dbInstance.groups.containsKey(group.getName()))
            return false;
        dbInstance.groups.put(group.getName(), group);
        return true;
    }

    @Override
    public boolean addGood(String group, Good good) {
        var groupFromDb = dbInstance.groups.get(group);
        if (groupFromDb == null || groupFromDb.getGoods().containsKey(good.getName()))
            return false;
        groupFromDb.getGoods().put(good.getName(), good);
        return true;
    }

    @Override
    public List<GoodGroup> getGroups() {
        return new ArrayList<>(dbInstance.groups.values());
    }

    @Override
    public List<Good> getGoods(String groupName) {
        return new ArrayList<>(dbInstance.groups.get(groupName).getGoods().values());
    }

    @Override
    public Good getGood(String groupName, String goodName) {
        return dbInstance.groups.get(groupName).getGoods().get(goodName);
    }

    @Override
    public GoodGroup getGroup(String groupName) {
        return dbInstance.groups.get(groupName);
    }

    @Override
    public boolean updateGroup(GoodGroup group) {
        if (dbInstance.groups.containsKey(group.getName())) {
            dbInstance.groups.put(group.getName(), group);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateGood(String groupName, Good good) {
        return addGood(groupName, good);
    }

    @Override
    public boolean deleteGood(String groupName, String goodName) {
        var groupFromDb = dbInstance.groups.get(groupName);
        if (groupFromDb == null || !groupFromDb.getGoods().containsKey(goodName))
            return false;
        groupFromDb.getGoods().remove(goodName);
        return true;
    }

    @Override
    public boolean deleteGoodGroup(String name) {
        var groupFromDb = dbInstance.groups.get(name);
        if (groupFromDb == null)
            return false;
        dbInstance.groups.remove(name);
        return true;
    }
}
