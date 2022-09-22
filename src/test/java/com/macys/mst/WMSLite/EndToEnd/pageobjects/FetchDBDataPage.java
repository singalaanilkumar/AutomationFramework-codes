package com.macys.mst.WMSLite.EndToEnd.pageobjects;

import com.macys.mst.WMSLite.EndToEnd.db.app.DBInitilizer;
import com.macys.mst.WMSLite.EndToEnd.db.app.DBMethods;
import com.macys.mst.WMSLite.EndToEnd.db.app.SQLQueries;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.WMSLite.EndToEnd.utilmethods.StepsDataStore;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
public class FetchDBDataPage extends BasePage {
    Connection con = null;
    ResultSet dbResults = null;
    private StepsDataStore dataStorage = StepsDataStore.getInstance();

    public FetchDBDataPage(WebDriver driver) {
        super(driver);
    }

    public void getOSMDetailsUsingFullfillmentNbr(String fullfillmentNbr) {
        ResultSet rs = null;
        try {
            DBInitilizer.cyberarkappid = "WMS_OPS";
            DBInitilizer.cyberarksafe = "PSV-FS-WMOPS-P";
            con = DBInitilizer.osmDbConnection("testData");
            String query = String.format(SQLQueries.GET_OSM_SHP_HDR_DATA, fullfillmentNbr);
            rs = DBMethods.getresultset(con, query);
            while (rs.next()) {
                String dbValue = rs.getString(1);
            }

        } catch (Exception e) {
            log.error("Error in loadAllTestData", e);
            org.testng.Assert.fail("Error in loadAllTestData", e);
        } finally {
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void getJoppaDbDetailsUsingFullfillmentNbr(String fullfillmentNbr) {
        ResultSet rs = null;
        try {
            DBInitilizer.cyberarkappid = "WMS_OPS";
            DBInitilizer.cyberarksafe = "PSV-FS-WMOPS-P";
            con = DBInitilizer.joppaDbConnection("testData");
            String query = String.format(SQLQueries.GET_JOPPA_INPT_PKT_HDR, fullfillmentNbr);
            rs = DBMethods.getresultset(con, query);
            while (rs.next()) {
                String dbValue = rs.getString(1);
            }
        } catch (Exception e) {
            log.error("Error in loadAllTestData", e);
            org.testng.Assert.fail("Error in loadAllTestData", e);
        } finally {
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public  List<Map<Object, Object>> getDbDetailsUsingResNumber(String query, String dbType) {
        ResultSet rs=null;
        List<Map<Object, Object>> list=null;
        try {
            if(dbType.contains("joppa")){
                DBInitilizer.cyberarkappid = "WMS_OPS";
                DBInitilizer.cyberarksafe = "PSV-FS-WMOPS-P";
                con = DBInitilizer.joppaDbConnection("testData");
            }else{
                con = DBInitilizer.osmDbConnection("testData");
            }

            rs = DBMethods.getresultset(con,query);
            list = CommonUtils.resultSetToStringList(rs);
        } catch (Exception e) {
            log.error("Error in loadAllTestData", e);
            org.testng.Assert.fail("Error in loadAllTestData", e);
        } finally {
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
     return list;
    }

    public  List<Map<Object, Object>> getOsmDbDetailsUsingResNumber(String query,String dbType) {
        ResultSet rs=null;
        List<Map<Object, Object>> list=null;
        try {
            DBInitilizer.cyberarkappid = "WMS_OPS";
            DBInitilizer.cyberarksafe = "PSV-FS-WMOPS-P";
            con = DBInitilizer.joppaDbConnection("testData");
            rs = DBMethods.getresultset(con,query);
            list = CommonUtils.resultSetToStringList(rs);
        } catch (Exception e) {
            log.error("Error in loadAllTestData", e);
            org.testng.Assert.fail("Error in loadAllTestData", e);
        } finally {
            if (null != con) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

}

