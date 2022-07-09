package dao;

import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBService implements Dao {
    private Connection con;
    private static final Logger LOGGER = Logger.getLogger(DBService.class.getCanonicalName());

    public DBService(String nameOfDb) throws DaoWrapperException {
        initialization(nameOfDb);
    }

    private void initialization(String name) throws DaoWrapperException {
        try {
            LOGGER.addHandler(new FileHandler(name + "dbLogs.log"));
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Did not manage to add file handle to the logger");
        }
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + name);
            con.setAutoCommit(false);
            try (PreparedStatement createGoodGroupsTable =
                         con.prepareStatement(
                                 "CREATE TABLE IF NOT EXISTS 'good_groups' ('name' TEXT PRIMARY KEY, 'description' TEXT);"
                         );
                 PreparedStatement createGoodsTable =
                         con.prepareStatement(
                                 """ 
                                         CREATE TABLE IF NOT EXISTS 'goods' (
                                           'name' TEXT UNIQUE,
                                           'group_name' TEXT,
                                           'description' TEXT,
                                           'producer' TEXT,
                                           'amount' INTEGER CHECK ('amount' > 0),
                                           'price' REAL CHECK ('price' > 0),
                                           PRIMARY KEY ('name', 'group_name'),
                                           FOREIGN KEY ('group_name') REFERENCES good_groups('name')
                                           ON DELETE CASCADE 
                                         );
                                         """
                         )) {
                var result1 = "Good_groups table creation result: % d".formatted(createGoodGroupsTable.executeUpdate());
                LOGGER.log(Level.INFO, result1);
                var result2 = "Good_groups table creation result: % d".formatted(createGoodsTable.executeUpdate());
                LOGGER.log(Level.INFO, result2);
                con.commit();
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Не знайшли драйвер JDBC");
            e.printStackTrace();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
    }

    @Override
    public boolean createGroup(GoodGroup group) throws DaoWrapperException {
        int result = 0;
        try (PreparedStatement lookIfContains =
                     con.prepareStatement(
                             "SELECT * FROM good_groups WHERE 'name' = ?"
                     );
             PreparedStatement insert =
                     con.prepareStatement(
                             """ 
                                     INSERT INTO good_groups COLUMN('name', 'description') VALUES (?, ?)
                                     """
                     )) {
            lookIfContains.setString(1, group.getName());
            var result1 = lookIfContains.executeQuery();
            if (result1.next())
                return false;
            insert.setString(1, group.getName());
            insert.setString(2, group.getDescription());
            result = insert.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return result != 0;
    }

    @Override
    public boolean createGood(String group, Good good) throws DaoWrapperException {
        int result = 0;
        try (PreparedStatement lookIfContains =
                     con.prepareStatement(
                             "SELECT * FROM goods WHERE 'group_name' = ? AND 'name' = ?"
                     );
             PreparedStatement insert =
                     con.prepareStatement(
                             """ 
                                     INSERT INTO goods COLUMN('name', 'group_name', 'description',
                                      'producer', 'amount', 'price') VALUES (?, ?, ?, ?, ?, ?)
                                     """
                     )) {
            lookIfContains.setString(1, group);
            lookIfContains.setString(2, good.getName());
            var result1 = lookIfContains.executeQuery();
            if (result1.next())
                return false;
            insert.setString(1, good.getName());
            insert.setString(2, group);
            insert.setString(3, good.getDescription());
            insert.setString(4, good.getProducer());
            insert.setInt(5, good.getAmount());
            insert.setDouble(6, good.getPrice());
            result = insert.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return result != 0;
    }

    @Override
    public List<GoodGroup> getGroups(int limit, CriteriaGoodGroup criteria) throws DaoWrapperException {
        if (limit < 0)
            throw new IllegalArgumentException("Limit cannot be less than 0");
        if (criteria == CriteriaGoodGroup.NAME) {
            try (PreparedStatement select =
                         con.prepareStatement(
                                 "SELECT * FROM good_groups ORDER BY 'name' LIMIT ? DESC"
                         )) {
                return getGoodGroups(limit, select);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
                e.printStackTrace();
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new DaoWrapperException("Bad sql", e);
                }
            }
        } else if (criteria == CriteriaGoodGroup.DESCRIPTION) {
            try (PreparedStatement select =
                         con.prepareStatement(
                                 "SELECT * FROM good_groups ORDER BY 'description' LIMIT ? DESC"
                         )) {
                return getGoodGroups(limit, select);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
                e.printStackTrace();
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new DaoWrapperException("Bad sql", e);
                }
            }
        }
        return new ArrayList<>();
    }

    private List<GoodGroup> getGoodGroups(int limit, PreparedStatement select) throws SQLException {
        select.setInt(1, limit);
        var result = select.executeQuery();
        var returnableList = new ArrayList<GoodGroup>();
        while (result.next())
            returnableList.add(new GoodGroup(result.getString(1), result.getString(2)));
        return returnableList;
    }

    @Override
    public List<Good> getGoods(String groupName, int limit, CriteriaGood criteria) throws DaoWrapperException {
        if (limit < 0)
            throw new IllegalArgumentException("Limit cannot be less than 0");
        switch (criteria) {
            case NAME -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY 'name' LIMIT ? DESC"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException("Bad sql", e);
                    }
                }
            }
            case DESCRIPTION -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY 'description' LIMIT ? DESC"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException("Bad sql", e);
                    }
                }
            }
            case PRICE -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY 'price' LIMIT ? DESC"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException("Bad sql", e);
                    }
                }
            }
            case AMOUNT -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY 'amount' LIMIT ? DESC"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException("Bad sql", e);
                    }
                }
            }
            case PRODUCER -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY 'producer' LIMIT ? DESC"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException("Bad sql", e);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Good> getGoods(int limit, PreparedStatement select) throws SQLException {
        select.setInt(1, limit);
        var result = select.executeQuery();
        var returnableList = new ArrayList<Good>();
        while (result.next())
            returnableList.add(new Good(result.getString(1),
                    result.getString(3),
                    result.getDouble(6),
                    result.getString(4),
                    result.getInt(5)));
        return returnableList;
    }

    @Override
    public Good getGood(String groupName, String goodName) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "SELECT * FROM 'goods' WHERE 'name' = ? AND 'group_name' = ?"
                     )) {
            select.setString(1, goodName);
            select.setString(2, groupName);
            var result = select.executeQuery();
            var returnableList = new ArrayList<Good>();
            while (result.next())
                returnableList.add(new Good(result.getString(1),
                        result.getString(3),
                        result.getDouble(6),
                        result.getString(4),
                        result.getInt(5)));
            if (returnableList.size() != 1)
                return null;
            return returnableList.get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return null;
    }

    @Override
    public GoodGroup getGroup(String groupName) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "SELECT * FROM 'good_groups' WHERE 'name' = ?"
                     )) {
            select.setString(1, groupName);
            var result = select.executeQuery();
            var returnableList = new ArrayList<GoodGroup>();
            while (result.next())
                returnableList.add(new GoodGroup(result.getString(1), result.getString(2)));
            if (returnableList.size() != 1)
                return null;
            return returnableList.get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return null;
    }

    @Override
    public boolean updateGroup(GoodGroup group) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "UPDATE 'good_groups' SET 'description' = ? WHERE 'name' = ?"
                     )) {
            select.setString(1, group.getDescription());
            select.setString(2, group.getName());
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return false;
    }

    @Override
    public boolean updateGood(String groupName, Good good) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "UPDATE 'goods' SET 'description' = ?, 'producer' = ?, 'amount' = ?, 'price' = ? WHERE 'name' = ? AND 'group_name' = ?"
                     )) {
            select.setString(1, good.getDescription());
            select.setString(2, good.getProducer());
            select.setInt(3, good.getAmount());
            select.setDouble(4, good.getPrice());
            select.setString(5, good.getName());
            select.setString(6, groupName);
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return false;
    }

    @Override
    public boolean deleteGood(String groupName, String goodName) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "DELETE FROM 'goods' WHERE 'group_name' = ? AND 'name' = ?"
                     )) {
            select.setString(1, groupName);
            select.setString(2, goodName);
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return false;
    }

    @Override
    public boolean deleteGoodGroup(String name) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "DELETE FROM 'good_groups' WHERE 'name' = ?"
                     )) {
            select.setString(1, name);
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException("Bad sql", e);
            }
        }
        return false;
    }
}
