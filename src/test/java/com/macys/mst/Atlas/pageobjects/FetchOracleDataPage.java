package com.macys.mst.Atlas.pageobjects;

import com.macys.mst.Atlas.db.app.DBInitilizer;
import com.macys.mst.Atlas.db.app.DBMethods;
import com.macys.mst.Atlas.db.app.SQLQueries;
import com.macys.mst.Atlas.utilmethods.StepsDataStore;
import com.macys.mst.artemis.reports.StepDetail;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class FetchOracleDataPage extends BasePage{
    Connection con = null;
    ResultSet dbResults = null;
    private StepsDataStore dataStorage = StepsDataStore.getInstance();
    public FetchOracleDataPage(WebDriver driver) {
        super(driver);
    }

    public void getTestData(){
        getPackageNoShipmentNoFromDB();
    }

    public void updateTestData() throws ParseException {
        updateDateForShipmentNo(dataStorage.getStoredData().get("shipment_number").toString());
    }

    public void deleteRecordsFromMPackage(String packageNo){
        dataStorage.getStoredData().put("package_number",packageNo);
        try{
        con = DBInitilizer.mySQLDbConnection("mySQLTestData");
        String DELETE_RECORDS_routing_m_package_ship_dtl = "Delete from routing.m_package_ship_dtl where package_number = '"+packageNo+"'";
        DBMethods.deleteOrUpdateDataBase(DELETE_RECORDS_routing_m_package_ship_dtl,con);
        StepDetail.addDetail("Records deleted from routing.m_package_ship_dtl for package_number : "+packageNo, true);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            StepDetail.addDetail("Exception in DB : " + exception, false);
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

    public void getPackageNoShipmentNoFromDB(){
        String packageNo = dataStorage.getStoredData().get("package_number").toString();
        String shipmentNo = null, tracking_nbr=null, shipVia=null, status=null,weight=null;
        try {
            con = DBInitilizer.oracleDbConnection("testData");
            String UPDATE_STATUS_SHIPVIA = "update m_package set status = '20',tracking_nbr = '',ship_via = '' where package_number='"+packageNo+"'";
            DBMethods.deleteOrUpdateDataBase(UPDATE_STATUS_SHIPVIA,con);
            con = DBInitilizer.oracleDbConnection("testData");
            String GET_DATA_MPACKAGE = "select * from m_package where package_number in ('"+packageNo+"')";
            dbResults = DBMethods.getFirstRowValues(con, GET_DATA_MPACKAGE);
//            dbResults = DBMethods.getFirstRowValues(con, SQLQueries.GET_M_PACKAGE_TESTDATA);
            while (dbResults.next()) {
                shipmentNo = dbResults.getString("shipment_number");
                shipVia = dbResults.getString("ship_via");
                status = dbResults.getString("status");
                tracking_nbr = dbResults.getString("tracking_nbr");
                weight = dbResults.getString("estimated_weight");
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
        StepDetail.addDetail("Shipment_Number : " + shipmentNo, true);
        StepDetail.addDetail("Package_Number : " + packageNo, true);
        StepDetail.addDetail("Status : " + status, true);
        StepDetail.addDetail("Ship_Via : " + shipVia, true);
        StepDetail.addDetail("Tracking_Nbr : " + tracking_nbr, true);
        StepDetail.addDetail("Estimated_Weight : " + weight, true);
        dataStorage.getStoredData().put("shipment_number", shipmentNo);
        dataStorage.getStoredData().put("estimated_weight", weight);
        log.info("shipment_number : "+shipmentNo);
        log.info("package_number : "+packageNo);
    }

    public void updateDateForShipmentNo(String param) throws ParseException {
        String extendedDate = addDaysSkippingWeekends(7);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date d = fmt.parse(extendedDate);
        fmt = new SimpleDateFormat("dd-MMM-yy");
        extendedDate = fmt.format(d);
        try{
        con = DBInitilizer.oracleDbConnection("testData");
        String UPDATE_DEL_END_DATE = "update orders set delivery_end_dttm='"+extendedDate+"' where tc_order_id in ('"+param+"','','','')";
        DBMethods.deleteOrUpdateDataBase(UPDATE_DEL_END_DATE,con);
        StepDetail.addDetail("delivery_end_dttm updated in orders to : " + extendedDate, true);
        String UPDATE_EXP_DEL_DATE =  "update shp_hdr set exp_del_dt ='"+extendedDate+"' where FULLFILLMENT_NBR in ('"+param+"','','','')";
        DBMethods.deleteOrUpdateDataBase(UPDATE_EXP_DEL_DATE,con);
        StepDetail.addDetail("exp_del_dt updated in shp_hdr to : " + extendedDate, true);
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

    public void validateShipViaForPackage(){
        String shipVia = null;
        String shipment_number = dataStorage.getStoredData().get("shipment_number").toString();
        String package_number = dataStorage.getStoredData().get("package_number").toString();
        String query = "Select * from m_package where shipment_number in('"+shipment_number+"') and package_number in('"+package_number+"')";
        try{
            con = DBInitilizer.oracleDbConnection("testData");
            dbResults = DBMethods.getFirstRowValues(con, query);
            while (dbResults.next()) {
                shipVia = dbResults.getString("ship_via");
            }
            log.info("ship_via for package_number "+package_number+" : "+shipVia);
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
        Assert.assertNotNull(shipVia);
        StepDetail.addDetail("ShipVia for package_number "+package_number+" : "+shipVia,true);
    }

    public void updateData(){
        try{
        con = DBInitilizer.oracleDbConnection("testData");
        String UPDATE_STATUS_SHIPVIA = "update m_package set status=20 where package_number in ('10000000000043239375')";
        DBMethods.deleteOrUpdateDataBase(UPDATE_STATUS_SHIPVIA,con);
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

    public void validateStatusForPackage(String statusNo){
        String status= null;
        String shipment_number = dataStorage.getStoredData().get("shipment_number").toString();
        String package_number = dataStorage.getStoredData().get("package_number").toString();
//        String shipment_number = "F766641604";
//        String package_number = "10000000000043241994";
        String query = "Select * from m_package where shipment_number in('"+shipment_number+"') and package_number in('"+package_number+"')";
        try{
            con = DBInitilizer.oracleDbConnection("testData");
            dbResults = DBMethods.getFirstRowValues(con, query);
            while (dbResults.next()) {
                status = dbResults.getString("status");
            }
            log.info("status for package_number "+package_number+" : "+status);
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
        Assert.assertEquals(status,statusNo);
        StepDetail.addDetail("Status validated successfully for package_number "+package_number+" : "+status,true);
    }


}
