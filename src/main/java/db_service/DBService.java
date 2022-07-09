package db_service;

import java.io.IOException;
import java.sql.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBService {
    private Connection con;
    private static final Logger LOGGER = Logger.getLogger(DBService.class.getCanonicalName());

    private void initialization(String name) {
        try {
            LOGGER.addHandler(new FileHandler(name + "dbLogs.log"));
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Did not manage to add file handle to the logger");
        }
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + name);
            try (PreparedStatement createGoodGroupsTable =
                         con.prepareStatement(
                                 "create table if not exists 'good_groups' ('name' TEXT PRIMARY KEY, 'description' TEXT);"
                         );
                 PreparedStatement createGoodsTable =
                         con.prepareStatement(
                                 """ 
                                         create table if not exists 'goods' (
                                           'name' TEXT UNIQUE,
                                           'group_name' TEXT,
                                           'description' TEXT,
                                           'producer' TEXT,
                                           'amount' INTEGER CHECK ('amount' > 0),
                                           'price' REAL CHECK ('price' > 0),
                                           PRIMARY KEY ('name', 'group_name'),
                                           FOREIGN KEY ('group_name') REFERENCES good_groups('name')
                                         );
                                         """
                         )) {
                var result1 = "Good_groups table creation result: % d".formatted(createGoodGroupsTable.executeUpdate());
                LOGGER.log(Level.INFO, result1);
                var result2 = "Good_groups table creation result: % d".formatted(createGoodsTable.executeUpdate());
                LOGGER.log(Level.INFO, result2);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    public DBService (String nameOfDb) {
        initialization(nameOfDb);
    }

    public static void main(String[] args) {
        DBService sqlTest = new DBService("MyDb");
    }
}
