package com.jiaruiblog.justforonce.service;

import java.sql.*;
import java.sql.Connection;


/**
 * @Author Jarrett Luo
 * @Date 2022/9/8 18:53
 * @Version 1.0
 */
public class SqliteDBService {

    private Connection connect()
    {
        //定义数据库连接对象
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:D:/S6.db";
            //定义连接数据库的url(url:访问数据库的URL路径),testconn为数据库名称
            //加载数据库驱动
            Class.forName("org.sqlite.JDBC");
            //获取数据库连接
            conn = DriverManager.getConnection(url);
            //数据库连接成功输出提示
            System.out.println("数据库连接成功！\n");
        }
        catch (ClassNotFoundException | SQLException e) {
            System.out.println("数据库连接失败！"+e.getMessage());
        }
        //返回一个连接
        return conn;
    }
    //选择文本区中的所有文本。在 null或空文档上不执行任何操作。
    public void selectAll() {
        //将从表中查询到的的所有信息存入sql
        String sql="Select * From log_data";
        // String sql="select * from sqlite_master where type = 'table'";//将从表中查询到的的所有信息存入sql
        try {
            Connection conn = this.connect();
            Statement stmt = conn.createStatement();//得到Statement实例
            ResultSet rs = stmt.executeQuery(sql);//执行SQL语句返回结果集
            //输出查询到的记录的内容（表头）
            System.out.println("姓名"+ "\t"+"性别"+ "\t"+"年龄"+ "\t");
            // 当返回的结果集不为空时，并且还有记录时，循环输出记录
            while (rs.next()) {
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));
                System.out.println(rs.getLong(2));
                System.out.println(rs.getLong(3));
                //输出获得记录中的"姓名","性别","年龄"字段的值
                // System.out.println(rs.getString("id") + "\t" + rs.getString("time_stamp")+ "\t" +rs.getInt("flags"));
            }
        }
        catch (SQLException e) {
            System.out.println("查询数据时出错！"+e.getMessage());
        }
    }
    //定义main主函数
    public static void main(String[] args) {

        Timestamp ts = new Timestamp(1656604805239L);
        System.out.println(ts);
        Date date = new Date(ts.getTime());
        System.out.println(date);
    }
}
