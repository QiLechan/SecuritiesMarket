package org.yuezhikong.plugins.sqlite;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.bukkit.Bukkit.getLogger;
import static org.yuezhikong.plugins.SecuritiesMarket.getFolder;

public class SqliteManager {
    private static String Folder = getFolder().getPath();
    private static String url = "jdbc:sqlite:" + Folder + "/sqlite.db";
    public static void connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void createNewTable() {
        String sql = "CREATE TABLE IF NOT EXISTS ticker (\n" + " name text NOT NULL,\n" +" UUID text NOT NULL,\n"+ " ticker text NOT NULL,\n" +" amount integer,\n" +"time text NOT NULL" + ");";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            getLogger().severe("创建表成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * 买入股票
     * @param name 玩家名称
     * @param UUID 玩家UUID
     * @param ticker 股票代码
     * @param amount 购买数量
     */
    public static void Buy(String name, String UUID, String ticker, int amount) {
        String sql = "INSERT INTO ticker(name,UUID,ticker,amount,time) VALUES(?,?,?,?,?)";
        String select = "SELECT amount FROM ticker WHERE ticker = ? AND UUID = ?";
        String newAmount = "UPDATE ticker SET amount = amount+?  WHERE ticker = ? AND UUID = ?";
        Connection conn = null;
        LocalDateTime now = LocalDateTime.now(); // 获取当前日期和时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedNow = now.format(formatter); // 格式化输出
        try {
            conn = DriverManager.getConnection(url);
            PreparedStatement SelectAmount = conn.prepareStatement(select);
            SelectAmount.setString(1, ticker);
            SelectAmount.setString(2, UUID);
            ResultSet rs = SelectAmount.executeQuery();
            if (!rs.next()){
                //插入数据
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, UUID);
                pstmt.setString(3, ticker);
                pstmt.setInt(4, amount);
                pstmt.setString(5, formattedNow);
                pstmt.executeUpdate();
            }
            else {
                //更新数量
                PreparedStatement pstmt = conn.prepareStatement(newAmount);
                pstmt.setInt(1, amount);
                pstmt.setString(2, ticker);
                pstmt.setString(3, UUID);
                pstmt.executeUpdate();
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 出售股票
     * @param UUID 玩家UUID
     * @param ticker 股票代码
     * @param amount 出售数量
     * @return 返回出售结果，0表示股票不存在，1表示股票数量不足，2表示数据库修改成功
     */
    public static int Sell(String UUID, String ticker, int amount) {
        String select = "SELECT amount FROM ticker WHERE ticker = ? AND UUID = ?";
        String newAmount = "UPDATE ticker SET amount = amount-?  WHERE ticker = ? AND UUID = ?";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            PreparedStatement SelectAmount = conn.prepareStatement(select);
            SelectAmount.setString(1, ticker);
            SelectAmount.setString(2, UUID);
            ResultSet rs = SelectAmount.executeQuery();
            if (!rs.next()){
                conn.close();
                return 0;
            }
            else {
                //更新数量
                if (rs.getInt("amount") < amount) {
                    conn.close();
                    return 1;
                }
                else {
                    PreparedStatement pstmt = conn.prepareStatement(newAmount);
                    pstmt.setInt(1, amount);
                    pstmt.setString(2, ticker);
                    pstmt.setString(3, UUID);
                    pstmt.executeUpdate();
                    conn.close();
                    return 2;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
