package com.macys.mst.Atlas.db.app;

import com.macys.mst.Atlas.execdrivers.ExecutionConfig;
import com.macys.mst.Atlas.utilmethods.CommonUtils;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

import static com.macys.mst.Atlas.db.app.DBInitilizer.getDbConnections;

@Slf4j
public class DBMethods {

    static ResultSet resultSet = null;
    static PreparedStatement sqlStatement = null;
    static String sql = "";
    /**
     * The logger.
     */
    private static Logger logger = Logger.getLogger(DBMethods.class.getName());
    private static Map<String, Connection> connectionMap = new HashMap();
    private static Connection dbConnect = null;
    private static String input = "";

    public static PreparedStatement getSqlStatement() {
        return sqlStatement;
    }

    public static void setSqlStatement(PreparedStatement sqlStatement2) {
        sqlStatement = sqlStatement2;
    }

    public static Connection establishDBConnections(String schema) {
        String env = ExecutionConfig.getExecEnv();
        try {
            if (connectionMap.containsKey(schema) && !((Connection) connectionMap.get(schema)).isClosed()) {
                logger.info("Connection is already established ... ");
            } else {
                Connection conn;
                if ("UAT".equalsIgnoreCase(env)) {
                    // conn = jdbcSShTunnel.getUatConnection();
                    conn = DBInitilizer.getUATDbConnections(schema);
                } else {
                    conn = getDbConnections(schema);
                }
                conn.setSchema(schema);
                connectionMap.put(schema, conn);
            }
        } catch (Exception e) {
            logger.info("Retry Connection ... ");
            try {
                if (connectionMap.containsKey(schema) && !((Connection) connectionMap.get(schema)).isClosed()) {
                    logger.info("Retry Connection is already established ... ");
                } else {
                    Connection conn;
                    if ("UAT".equalsIgnoreCase(env)) {
                        // conn = jdbcSShTunnel.getUatConnection();
                        conn = DBInitilizer.getUATDbConnections(schema);
                    } else {
                        conn = getDbConnections(schema);
                    }
                    conn.setSchema(schema);
                    connectionMap.put(schema, conn);
                }

            } catch (Exception e1) {
                logger.error("Exception on establish connection:", e);
            }
        }
        return connectionMap.get(schema);
    }

    public static Map<String, Connection> establishDBConnections(String dbType, String dbSchema) throws Exception {
        if (connectionMap.containsKey(dbSchema) && !((Connection) connectionMap.get(dbSchema)).isClosed()) {
            logger.info("Connection is already established ... ");
        } else {
            Connection conn = getDbConnections(dbType, dbSchema);
            connectionMap.put(dbSchema, conn);
        }

        return connectionMap;
    }

	public static void clearDBConnection() {
		connectionMap.forEach((k, v) -> {
			try {
				if (v != null) {
					v.close();
				}
			} catch (SQLException e) {
				logger.error("Close DbConnections", e);
			}
		});
	}

    public static void closeDBConnections() throws Exception {
        logger.info("Closing DB Connections ... ");
        dbConnect.close();
    }

    public static ResultSet dbResultSet(String query, String schema) throws SQLException, Exception {
        resultSet = null;
        try {

            Connection connection = establishDBConnections(schema);
            logger.info("Query: " + query);
            PreparedStatement sqlStatement = connection.prepareStatement(query);
            resultSet = sqlStatement.executeQuery();
            setSqlStatement(sqlStatement);
        } catch (SQLException exception) {
            logger.info("Exception in result set :" + exception.getMessage());
            {
                logger.error("Exception came for " + exception.getMessage());
                try {
                    Connection connection = establishDBConnections(schema);
                    PreparedStatement sqlStatement = connection.prepareStatement(query);
                    resultSet = sqlStatement.executeQuery();
                    setSqlStatement(sqlStatement);
                } catch (Exception ex) {
                    logger.info("Additional Ex: " + ex.getMessage());
                }

            }
        }
        return resultSet;
    }

    public static void deleteOrUpdateDataBase(String query, Connection connection) throws SQLException {
            logger.info(query);
            PreparedStatement sqlStatement = connection.prepareStatement(query);
            sqlStatement.executeUpdate();
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
            sqlStatement.close();
    }

    /*
     * Get a Single String Value
     */
    public static String getDBValueInString(String query, String schema) {
        logger.info("SQL during 1st time execution: " + query);
        String dbValue = null;
        ResultSet rs = null;
        try {
            rs = dbResultSet(query, schema);
            while (rs.next()) {
                dbValue = rs.getString(1);
                break;
            }
            rs.close();
            getSqlStatement().close();
        } catch (Exception exception) {
            logger.info("SQL during 2nd time execution: " + query);
            logger.info("the Db values exception is " + exception.getMessage());
        }
        return dbValue;
    }

    public static ResultSet getresultset(Connection con, String SQL) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(SQL);
            return rs;
        } catch (SQLException var5) {
            logger.info("exeception in creating a results set using SQL :" + SQL + "" + var5);
            return rs;
        }
    }

    public static List<String> getDBValueInList(Connection con, String query) throws Exception, SQLException {
        logger.info("query: " + query);
        ArrayList dbValues = new ArrayList();
        ResultSet rs = null;
        rs = getresultset(con, query);

        while (rs.next()) {
            String dbValue = rs.getString(1);
            dbValues.add(dbValue);
        }

        return dbValues;
    }

    public static ResultSet getFirstRowValues(Connection con, String query) throws Exception, SQLException {
        logger.info("query: " + query);
        ResultSet rs = null;
        rs = getresultset(con, query);
        return rs;
    }

    public static Map<String, String> getDBValuesInMap(String query, String dbSchema) throws Exception {
        logger.info("Query Is : " + query);
        LinkedHashMap sub_Map = new LinkedHashMap();
        ResultSet result_Set = dbResultSet(query, dbSchema);

        while (result_Set.next()) {
            sub_Map.put(result_Set.getString(1), result_Set.getString(2));
        }

        return sub_Map;
    }

    public static Integer getDBValueInteger(String query, String schema) throws Exception {
        logger.info("SQL during 1st time execution: " + query);
        Integer dbValue = null;
        ResultSet rs = null;
        try {
            rs = dbResultSet(query, schema);
            while (rs.next()) {
                dbValue = rs.getInt(1);
                break;
            }
            rs.close();
            getSqlStatement().close();
        } catch (Exception exception) {
            logger.info("SQL during 2nd time execution: " + query);
            logger.info("the Db values exception is " + exception.getMessage());
        }
        return dbValue;
    }

    /*
     * Get a Single String Value
     */
    public static List<String> getDBValueInList(String query, String schema) throws Exception {
        List<String> dbValues = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = dbResultSet(query, schema);
            while (rs.next()) {
                String dbValue = rs.getString(1);
                dbValues.add(dbValue);
                // break;
            }
            rs.close();
            getSqlStatement().close();
        } catch (Exception exception) {
            logger.info("the Db values exception is " + exception.getMessage());
        }
        return dbValues;
    }

    /* Get list values in SQL query format */
    public static String getValuesForSQl(List<String> list) {
        List<String> sl = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            sl.add("'" + list.get(i) + "'");
        }
        String deptsSqlOrder = sl.toString().substring(1, sl.toString().length() - 1);
        return deptsSqlOrder;

    }

    public static Map<String, List<Object>> getValuesFromDB(String sql, String schema) throws Exception {
        ResultSet rs = dbResultSet(sql, schema);
        Map<String, List<Object>> map = CommonUtils.resultSetToMap(rs);
        rs.close();
        getSqlStatement().close();
        return map;
    }

    public static List<Map<?, ?>> getValuesFromDBAsList(String sql, String schema) throws Exception {
        ResultSet rs = dbResultSet(sql, schema);
        List<Map<?, ?>> list = CommonUtils.resultSetToList(rs);
        rs.close();
        getSqlStatement().close();
        return list;

    }

    public static List<Map<Object, Object>> getValuesFromDBAsStringList(String sql, String schema) throws Exception {
        ResultSet rs = dbResultSet(sql, schema);
        List<Map<Object, Object>> list = CommonUtils.resultSetToStringList(rs);
        rs.close();
        getSqlStatement().close();
        return list;
    }

    public static List<Map<String, String>> getValuesFromDBAsStringListMap(String sql, String schema) throws Exception {
        ResultSet rs = dbResultSet(sql, schema);
        List<Map<String, String>> list = CommonUtils.resultSetToStringListMap(rs);
        rs.close();
        getSqlStatement().close();
        return list;
    }

    public static String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public static Object getData(String schema) throws ClassNotFoundException {
        ResultSet rs = null;
        List<Map<String, String>> AssignmentDetails = new ArrayList<Map<String, String>>();

        try {
            long startTime = 0;
            long currentTime;
            long timeDiff;
            long waitTime = 1000000;
            Boolean flag = false;
            do {
                if (rs != null) {
                    rs.close();
                    getSqlStatement().close();
                }
                rs = dbResultSet(sql, schema);// added

                currentTime = System.currentTimeMillis();
                timeDiff = currentTime - startTime;
            } while (!rs.isBeforeFirst() && timeDiff <= waitTime);
            while (rs.next()) {
                Map<String, String> details = new HashMap<String, String>();
                details.clear();
                ResultSetMetaData rmd = rs.getMetaData();
                // The column count starts from 1
                for (int i = 1; i < rmd.getColumnCount() + 1; i++) {
                    String name = rmd.getColumnName(i);
                    // logger.info(name);
                    details.put(name, rs.getString(name));
                }
                AssignmentDetails.add(details);
            }
            rs.close();
            getSqlStatement().close();
        } catch (SQLException e) {
            logger.error(e);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error(e);
            // throw e;
        }
        return AssignmentDetails;
    }

    public String getSql() {
        return sql;
    }

    public static void setSql(String sql1) {
        sql = sql1.replace("#PARAM#", getInput());
        // Log.info(sql);
        sql = sql1;
    }

}
