package dao;

import dao.exceptions.DaoWrapperException;
import entities.Good;
import entities.GoodGroup;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Practice 4
 */
public class DBService implements Dao {
    private static Connection con;
    private static final Logger LOGGER = Logger.getLogger(DBService.class.getCanonicalName());
    private static final String BAD_SQL_WARNING = "BAD SQL";

    public DBService(String nameOfDb) throws DaoWrapperException {
        initialization(nameOfDb);
    }

    /**
     * Has to be called before creating an instance of dao of this class
     */
    public static void initializeConnection(String name) throws DaoWrapperException {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + name);
            con.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Не знайшли драйвер JDBC");
            e.printStackTrace();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
    }

    public static Connection getConnection() {
        return con;
    }

    public void close() throws SQLException {
        con.close();
    }

    private void initialization(String name) throws DaoWrapperException {
        if (con == null)
            throw new DaoWrapperException("Uninitialized , call initializeConnection first", new IllegalStateException());
        try {
            File file = new File("logs/");
            if (!file.exists())
                file.mkdirs();
            File logFile = new File(file, name + "dbLogs.log");
            LOGGER.addHandler(new FileHandler(logFile.getAbsolutePath()));
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Did not manage to add file handle to the logger");
        }
        try {
            try (PreparedStatement createGoodGroupsTable =
                         con.prepareStatement(
                                 "CREATE TABLE IF NOT EXISTS good_groups (name TEXT PRIMARY KEY, description TEXT);"
                         );
                 PreparedStatement createGoodsTable =
                         con.prepareStatement(
                                 """ 
                                         CREATE TABLE IF NOT EXISTS goods (
                                           name TEXT UNIQUE,
                                           group_name TEXT,
                                           description TEXT,
                                           producer TEXT,
                                           amount INTEGER CHECK (amount >= 0),
                                           price REAL CHECK (price >= 0),
                                           PRIMARY KEY (name),
                                           FOREIGN KEY (group_name) REFERENCES good_groups(name)
                                           ON DELETE CASCADE
                                         );
                                         """
                         ); PreparedStatement createUserTable =
                         con.prepareStatement(""" 
                                 CREATE TABLE IF NOT EXISTS users (
                                   username TEXT UNIQUE,
                                   password TEXT,
                                   PRIMARY KEY (username)
                                 );
                                 """)
            ) {
                var result1 = "Good_groups table creation result: % d".formatted(createGoodGroupsTable.executeUpdate());
                LOGGER.log(Level.INFO, result1);
                var result2 = "Good_groups table creation result: % d".formatted(createGoodsTable.executeUpdate());
                LOGGER.log(Level.INFO, result2);
                createUserTable.executeUpdate();
                con.commit();
                try (PreparedStatement insertRootUser =
                             con.prepareStatement(""" 
                                     INSERT INTO users (username, password) VALUES(ee11cbb19052e40b07aac0ca060c23ee, 1a1dc91c907325c69271ddf0c944bc72);
                                     """)) {
                    insertRootUser.executeUpdate();
                }
                con.commit();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
    }

    @Override
    public boolean createGroup(GoodGroup group) throws DaoWrapperException {
        int result = 0;
        try (PreparedStatement lookIfContains =
                     con.prepareStatement(
                             "SELECT * FROM good_groups WHERE name = ?"
                     );
             PreparedStatement insert =
                     con.prepareStatement(
                             """ 
                                     INSERT INTO good_groups (name, description) VALUES (?, ?)
                                     """
                     )) {
            lookIfContains.setString(1, group.getName());
            var result1 = lookIfContains.executeQuery();
            if (result1.next())
                return false;
            insert.setString(1, group.getName());
            insert.setString(2, group.getDescription());
            result = insert.executeUpdate();
            result1.close();
            con.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return result != 0;
    }

    @Override
    public boolean createGood(String group, Good good) throws DaoWrapperException {
        int result = 0;
        try (PreparedStatement lookIfContains =
                     con.prepareStatement(
                             "SELECT * FROM goods WHERE group_name = ? AND name = ?"
                     );
             PreparedStatement insert =
                     con.prepareStatement(
                             """ 
                                     INSERT INTO goods (name, group_name, description,
                                      producer, amount, price) VALUES (?, ?, ?, ?, ?, ?)
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
            LOGGER.log(Level.INFO, "" + good.getAmount());
            insert.setDouble(6, good.getPrice());
            result = insert.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return result != 0;
    }

    @Override
    public boolean createUser(String username, String password) throws DaoWrapperException {
        int result = 0;
        try (PreparedStatement lookIfContains =
                     con.prepareStatement(
                             "SELECT * FROM users WHERE username = ?"
                     );
             PreparedStatement insert =
                     con.prepareStatement(
                             """ 
                                     INSERT INTO users (username, password) VALUES (?, ?)
                                     """
                     )) {
            lookIfContains.setString(1, username);
            var result1 = lookIfContains.executeQuery();
            if (result1.next())
                return false;
            insert.setString(1, username);
            insert.setString(2, password);
            result = insert.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return result != 0;
    }

    @Override
    public boolean deleteUser(String username) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "DELETE FROM users WHERE username = ?"
                     )) {
            select.setString(1, username);
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return false;
    }

    @Override
    public boolean updateUser(String username, String password) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "UPDATE users SET password = ? WHERE username = ?"
                     )) {
            select.setString(1, password);
            select.setString(2, username);
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return false;
    }

    @Override
    public String getUserPass(String username) throws DaoWrapperException {
        try (PreparedStatement lookIfContains =
                     con.prepareStatement(
                             "SELECT * FROM users WHERE username = ?"
                     )) {
            lookIfContains.setString(1, username);
            var result1 = lookIfContains.executeQuery();
            if (result1.next())
                return result1.getString(2);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return null;
    }

    @Override
    public List<String> getUsernames() throws DaoWrapperException {
        List<String> result = new ArrayList<>();
        try (PreparedStatement lookIfContains =
                     con.prepareStatement(
                             "SELECT * FROM users"
                     )) {
            var result1 = lookIfContains.executeQuery();
            while (result1.next())
                result.add(result1.getString(1));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return result;
    }

    @Override
    public List<GoodGroup> getGroups(int limit, CriteriaGoodGroup criteria) throws DaoWrapperException {
        if (limit < 0)
            throw new IllegalArgumentException("Limit cannot be less than 0");
        if (criteria == CriteriaGoodGroup.NAME) {
            try (PreparedStatement select =
                         con.prepareStatement(
                                 "SELECT * FROM good_groups ORDER BY name DESC LIMIT ?"
                         )) {
                return getGoodGroups(limit, select);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                e.printStackTrace();
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new DaoWrapperException(BAD_SQL_WARNING, e);
                }
            }
        } else if (criteria == CriteriaGoodGroup.DESCRIPTION) {
            try (PreparedStatement select =
                         con.prepareStatement(
                                 "SELECT * FROM good_groups ORDER BY description DESC LIMIT ?"
                         )) {
                return getGoodGroups(limit, select);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                e.printStackTrace();
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    throw new DaoWrapperException(BAD_SQL_WARNING, e);
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
                                     "SELECT * FROM goods WHERE group_name = ? ORDER BY name DESC LIMIT ?"
                             )) {
                    return getGoods(limit, groupName, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case DESCRIPTION -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods WHERE group_name = ? ORDER BY description DESC LIMIT ?"
                             )) {
                    return getGoods(limit, groupName, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case PRICE -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods WHERE group_name = ? ORDER BY price DESC LIMIT ?"
                             )) {
                    return getGoods(limit, groupName, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case AMOUNT -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods WHERE group_name = ? ORDER BY amount DESC LIMIT ?"
                             )) {
                    return getGoods(limit, groupName, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case PRODUCER -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods WHERE group_name = ? ORDER BY producer DESC LIMIT ?"
                             )) {
                    return getGoods(limit, groupName, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Good> getGoods(int limit, CriteriaGood criteria) throws DaoWrapperException {
        if (limit < 0)
            throw new IllegalArgumentException("Limit cannot be less than 0");
        switch (criteria) {
            case NAME -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY name DESC LIMIT ?"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case DESCRIPTION -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY description DESC LIMIT ?"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case PRICE -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY price DESC LIMIT ?"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case AMOUNT -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY amount DESC LIMIT ?"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
            case PRODUCER -> {
                try (PreparedStatement select =
                             con.prepareStatement(
                                     "SELECT * FROM goods ORDER BY producer DESC LIMIT ?"
                             )) {
                    return getGoods(limit, select);
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
                    e.printStackTrace();
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        throw new DaoWrapperException(BAD_SQL_WARNING, e);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Good> getGoods(int limit, PreparedStatement select) throws SQLException {
        select.setInt(1, limit);
        return getGoods(select);
    }

    private List<Good> getGoods(int limit, String groupName, PreparedStatement select) throws SQLException {
        select.setInt(2, limit);
        select.setString(1, groupName);
        return getGoods(select);
    }

    private List<Good> getGoods(PreparedStatement select) throws SQLException {
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
    public Good getGood(String goodName) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "SELECT * FROM goods WHERE name = ?"
                     )) {
            select.setString(1, goodName);
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
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return null;
    }

    @Override
    public GoodGroup getGroup(String groupName) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "SELECT * FROM good_groups WHERE name = ?"
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
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return null;
    }

    @Override
    public boolean updateGroup(GoodGroup group) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "UPDATE good_groups SET description = ? WHERE name = ?"
                     )) {
            select.setString(1, group.getDescription());
            select.setString(2, group.getName());
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return false;
    }

    @Override
    public boolean updateGood(Good good) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "UPDATE goods SET description = ?, producer = ?, amount = ?, price = ? WHERE name = ?"
                     )) {
            select.setString(1, good.getDescription());
            select.setString(2, good.getProducer());
            select.setInt(3, good.getAmount());
            select.setDouble(4, good.getPrice());
            select.setString(5, good.getName());
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return false;
    }

    @Override
    public boolean deleteGood(String goodName) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "DELETE FROM goods WHERE name = ?"
                     )) {
            select.setString(1, goodName);
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return false;
    }

    @Override
    public boolean deleteGoodGroup(String name) throws DaoWrapperException {
        try (PreparedStatement select =
                     con.prepareStatement(
                             "DELETE FROM good_groups WHERE name = ?"
                     )) {
            select.setString(1, name);
            var result = select.executeUpdate();
            con.commit();
            return result != 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, BAD_SQL_WARNING);
            e.printStackTrace();
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new DaoWrapperException(BAD_SQL_WARNING, e);
            }
        }
        return false;
    }
}
