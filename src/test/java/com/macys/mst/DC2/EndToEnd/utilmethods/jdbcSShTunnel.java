package com.macys.mst.DC2.EndToEnd.utilmethods;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.artemis.config.ConfigProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class jdbcSShTunnel {



        private static void doSshTunnel( String strSshUser, String strSshPassword, String strSshHost,
                                         int nSshPort, String strRemoteHost, int nLocalPort, int nRemotePort )
                throws JSchException  {
            final JSch jsch = new JSch();
            Session session = jsch.getSession( strSshUser, strSshHost, 22 );
            session.setPassword( strSshPassword );

            final Properties config = new Properties();
            config.put("StrictHostKeyChecking","no" );
            session.setConfig(config );
            session.connect();
            String boundaddress ="0.0.0.0";
            session.setPortForwardingL(boundaddress,nLocalPort, strRemoteHost, nRemotePort);
        }


    public static Connection getUatConnection() {
        Connection con = null ;
        try {
            String strSshUser = ConfigProperties.getInstance("config.properties").getProperty("sshUser");  // SSH loging username
            String strSshPassword = ConfigProperties.getInstance("config.properties").getProperty("sshPwd"); // SSH login password
            String strSshHost = "172.22.141.200"; // hostname or ip or SSH server
            int nSshPort = 22; // remote SSH host port number
            String strRemoteHost = "10.255.241.7"; // hostname or ip of your database server
            int nLocalPort = 3366; // local port number use to bind SSH tunnel
            int nRemotePort = 3306; // remote port number of your database
            String strDbUser = "scmuatuser"; // database loging username
            String strDbPassword = ExecutionConfig.getPassword();

            jdbcSShTunnel.doSshTunnel(strSshUser, strSshPassword, strSshHost, nSshPort, strRemoteHost, nLocalPort, nRemotePort);

            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:" + nLocalPort, strDbUser, strDbPassword);
            System.out.println(con.getMetaData());
            System.out.println("Database connection established");
            System.out.println("DONE");
                /*
                String query = "SELECT * FROM orders.po_location_dtl where Po_NBR = 4726897";
                PreparedStatement sqlStatement = con.prepareStatement(query);
                ResultSet resultSet = sqlStatement.executeQuery();
                Map<String, List<Object>> map = CommonUtils.resultSetToMap(resultSet);
                System.out.println("map: "+map);*/
            // con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}

