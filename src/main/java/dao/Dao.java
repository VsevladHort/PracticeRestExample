package dao;

import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;

import java.util.List;

public interface Dao {
    /**
     * Create group
     */
    boolean createGroup(GoodGroup group) throws DaoWrapperException;

    /**
     * Create good
     */
    boolean createGood(String group, Good good) throws DaoWrapperException;

    /**
     * Create user
     */
    boolean createUser(String username, String password) throws DaoWrapperException;

    /**
     * delete user
     */
    boolean deleteUser(String username) throws DaoWrapperException;

    /**
     * update password
     */
    boolean updateUser(String username, String password) throws DaoWrapperException;

    /**
     * get password
     */
    String getUserPass(String username) throws DaoWrapperException;

    /**
     * list usernames
     */
    List<String> getUsernames() throws DaoWrapperException;

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
     * List by criteria
     *
     * @param limit    - limit for the sql query
     * @param criteria - what to sort by
     * @return list of Goods from the database
     */
    List<Good> getGoods(int limit, CriteriaGood criteria) throws DaoWrapperException;

    /**
     * Read
     */
    Good getGood(String goodName) throws DaoWrapperException;

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
    boolean updateGood(Good good) throws DaoWrapperException;

    /**
     * Delete
     */
    boolean deleteGood(String goodName) throws DaoWrapperException;

    /**
     * Delete
     */
    boolean deleteGoodGroup(String name) throws DaoWrapperException;
}
