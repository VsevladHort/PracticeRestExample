package dao;

import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;

import java.util.List;

public interface Dao {
    /**
     * Create
     */
    boolean createGroup(GoodGroup group) throws DaoWrapperException;

    /**
     * Create
     */
    boolean createGood(String group, Good good) throws DaoWrapperException;

    /**
     * List by criteria
     *
     * @param limit    - limit for the sql query
     * @param criteria - what to sort by
     * @return list of GoodGroups from the database
     */
    List<GoodGroup> getGroups(int limit, CriteriaGoodGroup criteria) throws DaoWrapperException;

    /**
     * List by criteria
     *
     * @param limit    - limit for the sql query
     * @param criteria - what to sort by
     * @return list of Goods from the database
     */
    List<Good> getGoods(String groupName, int limit, CriteriaGood criteria) throws DaoWrapperException;

    /**
     * Read
     */
    Good getGood(String groupName, String goodName) throws DaoWrapperException;

    /**
     * Read
     */
    GoodGroup getGroup(String groupName) throws DaoWrapperException;

    /**
     * Update
     */
    boolean updateGroup(GoodGroup group) throws DaoWrapperException;

    /**
     * Update
     */
    boolean updateGood(String groupName, Good good) throws DaoWrapperException;

    /**
     * Delete
     */
    boolean deleteGood(String groupName, String goodName) throws DaoWrapperException;

    /**
     * Delete
     */
    boolean deleteGoodGroup(String name) throws DaoWrapperException;
}
