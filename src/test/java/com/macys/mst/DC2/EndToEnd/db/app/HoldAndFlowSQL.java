package com.macys.mst.DC2.EndToEnd.db.app;

public class HoldAndFlowSQL {
       public static final String UpdateCompletedPresortSplitAsDeleted = "UPDATE wsm.activity_lifecycle lc set lc.status_id=3 where lc.status_id=4 and lc.activity_id in (SELECT ID FROM wsm.activity act where act.type_id = 25 and act.container in (#containers))";

       //To clear presort_tote+alloc table aisles
       public static final String selectPresortToteAllocIDs = "SELECT ID FROM sorting.presort_tote_alloc where aisle_nbr=#aisle";
       public static final String updateGivenIDsToAisleZero = "UPDATE sorting.presort_tote_alloc set aisle_nbr=0 where id in (#IDs)";

       //To clear presort_qd table aisles
       public static final String selectPresortQDAisleIDs = "SELECT ID FROM sorting.presort_qd where aisle_nbr=#aisle";
       public static final String updateGivenIDsToAisleZeroInQD = "UPDATE sorting.presort_qd set aisle_nbr=0 where id in (#IDs)";
       public static final String cycle_count_ui_data = " SELECT * FROM receiving.cycle_count_ui_data where CHILD in (#CHILD)";

       public static final String updateRcptBeforeFirstScan = "UPDATE orders.order_rcpt SET RCPT_STAT_NBR = '10', WHM_STATUS = 'OPEN', FIRST_SCAN_TS = NULL, LAST_SCAN_TS = NULL AND PROCESSED_TIME=NULL WHERE RCPT_NBR = #RCPTNBR";
       public static final String updateRcptAfterFirstScan = "UPDATE orders.order_rcpt SET RCPT_STAT_NBR = '15', WHM_STATUS = 'ACK' WHERE RCPT_NBR = #RCPTNBR";
}
