package com.macys.mst.Atlas.db.app;

import com.macys.mst.Atlas.execdrivers.ExecutionConfig;
import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.config.GetPasswordCyberArk;
import com.macys.mst.artemis.db.DBConnections;
import org.apache.log4j.Logger;

import java.sql.*;

public class DBInitilizer {

    public static String cyberarksafe = FileConfig.getInstance().getStringConfigValue("cyberark.safe");
    public static String cyberarkappid = FileConfig.getInstance().getStringConfigValue("cyberark.appid");
    public static Connection connection = null;
    static PreparedStatement sqlStatement = null;
    static ResultSet resultSet = null;

    private static Logger logger = Logger.getLogger(DBInitilizer.class.getName());

    public static Connection getConnection(String dbType, String schema) {
        Connection conn = DBConnections.getinstance(dbType, schema).dbConnection();
        return conn;
    }

    public static Connection oracleDbConnection(String schema) {
        try {
            if (connection == null || connection.isClosed()) {
                String driver = FileConfig.getInstance().getStringConfigValue("oracleDB.driver.classname");
                String url = FileConfig.getInstance().getStringConfigValue("oracleDB.testData.connecturl");
                String username = FileConfig.getInstance().getStringConfigValue("oracleDB.testData.name");
                // String
                // password=FileConfig.getInstance().getStringConfigValue("db."+schema+".password");
                String passwordobj = FileConfig.getInstance().getStringConfigValue("oracleDB." + schema + ".pwdobjectid");
                //String password = GetPasswordCyberArk.getpassword(cyberarksafe, cyberarkappid, passwordobj);
                String password = "MSTPD9R1";
                Class.forName(driver);
                connection = DriverManager.getConnection(url, username, password);
            } else {
                logger.info("Connection is NOT NULL");
                return connection;
            }

        } catch (SQLException | ClassNotFoundException e) {
            logger.info(e);
        }
        return connection;
    }

    public static Connection mySQLDbConnection(String schema) {
//        try {
//            if (connection != null && !connection.isClosed()) {
//                logger.info("Connection is NOT NULL");
//                return connection;
//            }
//
//            String driver = FileConfig.getInstance().getStringConfigValue("mySQLDB.driver.classname");
//            String url = FileConfig.getInstance().getStringConfigValue("mySQLDB." + schema + ".connecturl");
//            String username = FileConfig.getInstance().getStringConfigValue("mySQLDB." + schema + ".name");
//            String password = FileConfig.getInstance().getStringConfigValue("mySQLDB." + schema + ".password");
//            System.out.println(driver);
//            System.out.println(url);
//            System.out.println(username);
//            System.out.println(password);
//            Class.forName(driver);
//            connection = DriverManager.getConnection(url, username, password);
//        } catch (ClassNotFoundException | SQLException var5) {
//            logger.info(var5);
//        }
//
//        return connection;
        try {
            if (connection == null || connection.isClosed()) {
                String driver = FileConfig.getInstance().getStringConfigValue("mySQLDB.driver.classname");
                String url = FileConfig.getInstance().getStringConfigValue("mySQLDB." + schema + ".connecturl");
                String username = FileConfig.getInstance().getStringConfigValue("mySQLDB." + schema + ".name");
                // String
                // password=FileConfig.getInstance().getStringConfigValue("db."+schema+".password");
                //String passwordobj = FileConfig.getInstance().getStringConfigValue("mySQLDB." + schema + ".pwdobjectid");
                //String password = GetPasswordCyberArk.getpassword(cyberarksafe, cyberarkappid, passwordobj);
                String password = "1cr*QJX?";
                Class.forName(driver);
                connection = DriverManager.getConnection(url, username, password);
            } else {
                logger.info("Connection is NOT NULL");
                return connection;
            }

        } catch (SQLException | ClassNotFoundException e) {
            logger.info(e);
        }
        return connection;
    }

    public static Connection getDbConnections(String dbSchema) {
        String driverClass = FileConfig.getInstance().getStringConfigValue("db.driver.classname");
        String connUrl = String.format(FileConfig.getInstance().getStringConfigValue("db.connecturi"), dbSchema);
        String userName = FileConfig.getInstance().getStringConfigValue("db.userName");
        String passWord = ExecutionConfig.getPassword();
        Connection conn = DBConnections.getinstance(dbSchema, driverClass, connUrl, userName, passWord).dbConnection();
        return conn;
    }

    public static Connection getUATDbConnections(String dbSchema) {
        String driverClass = FileConfig.getInstance().getStringConfigValue("db.driver.classname");
        String connUrl = String.format(FileConfig.getInstance().getStringConfigValue("db.connecturi"), dbSchema);
        String userName = FileConfig.getInstance().getStringConfigValue("db.userName");
        String passWord = ExecutionConfig.getPassword();
        Connection conn = DBConnections.getinstance(dbSchema, driverClass, connUrl, userName, passWord).dbConnection();
        return conn;
    }

    public static Connection getDbConnectionSSH(String dbSchema) throws Exception {
        Connection conn = null;
        String connUrl = String.format(FileConfig.getInstance().getStringConfigValue("db." + dbSchema + ".connecturi"), dbSchema);
        String userName = FileConfig.getInstance().getStringConfigValue("db.userName");
        String passWord = ExecutionConfig.getPassword();
        conn = DriverManager.getConnection(connUrl, userName, passWord);
        return conn;
    }



    public static Connection getDbConnections(String dbType, String dbSchema) {
        Connection conn = null;

        try {
            String driverClass = FileConfig.getInstance().getStringConfigValue(String.format("%s%s", dbType, ".driver.classname"));
            String dbTypeSchema = String.format("%s%s%s", dbType, ".", dbSchema);
            String connUrl = FileConfig.getInstance().getStringConfigValue(String.format("%s%s", dbTypeSchema, ".connecturi"));
            String userName = FileConfig.getInstance().getStringConfigValue(String.format("%s%s", dbTypeSchema, ".name"));
            String cybArkSafe = FileConfig.getInstance().getStringConfigValue(String.format("%s%s", dbTypeSchema, ".safe"));
            String cybArkAppId= FileConfig.getInstance().getStringConfigValue(String.format("%s%s", dbTypeSchema, ".appid"));
            conn = DBConnections.getinstance(dbSchema, driverClass, connUrl, userName, GetPasswordCyberArk.getpassword(cybArkSafe, cybArkAppId, FileConfig.getInstance().getStringConfigValue(String.format("%s%s", dbTypeSchema, ".pwdobjectid")))).dbConnection();
        } catch (Exception var7) {
            logger.error("Error while create DbConnections", var7);
        }

        return conn;
    }

    public static void closeDBResources() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (sqlStatement != null) {
                sqlStatement.close();
            }
            if (connection != null) {
                connection.close();
                logger.info("##############################--- Connection Closed -- ######################");
            }

        } catch (SQLException se) {
            logger.error(se);
        }
    }

}
