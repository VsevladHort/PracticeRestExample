package dao;

import entities.Good;
import entities.GoodGroup;

import java.util.List;
import java.util.Set;

public interface Dao {
    boolean addGroup(GoodGroup group);

    boolean addGood(String group, Good good);

    List<GoodGroup> getGroups();

    List<Good> getGoods(String groupName);

    Good getGood(String groupName, String goodName);

    GoodGroup getGroup(String groupName);

    boolean updateGroup(GoodGroup group);

    boolean updateGood(String groupName, Good good);

    boolean deleteGood(String groupName, String goodName);

    boolean deleteGoodGroup(String name);
}
