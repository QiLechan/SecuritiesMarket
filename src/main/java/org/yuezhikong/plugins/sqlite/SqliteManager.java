package org.yuezhikong.plugins.sqlite;

import java.io.File;
import java.sql.*;

import static org.bukkit.Bukkit.getLogger;
import static org.yuezhikong.plugins.SecuritiesMarket.getFolder;

public class SqliteManager {
    private static String Folder = getFolder().getPath();
    public static void connect() {
        Connection conn = null;
        Folder = getFolder().getPath();
        try {
            String url = "jdbc:sqlite:" + Folder + "/sqlite.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void createNewTable() {
        String url = "jdbc:sqlite:" + Folder + "/sqlite.db";
        String sql = "CREATE TABLE IF NOT EXISTS ticker (\n" + " name text NOT NULL,\n" +" UUID text NOT NULL,\n"+ " ticker text NOT NULL,\n" +" amount integer PRIMARY KEY\n" + ");";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            getLogger().severe("创建表成功");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    public static void Buy(String name, String UUID, String ticker, int amount) {
        String url = "jdbc:sqlite:" + Folder + "/sqlite.db";
        String sql = "INSERT INTO ticker(name,UUID,ticker,amount) VALUES(?,?,?,?)";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, UUID);
            pstmt.setString(3, ticker);
            pstmt.setInt(4, amount);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
