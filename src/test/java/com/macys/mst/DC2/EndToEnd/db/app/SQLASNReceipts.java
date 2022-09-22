package com.macys.mst.DC2.EndToEnd.db.app;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;

public class SQLASNReceipts {
	public static final String GET_ASN_RECEIPTNBR_DETAILS = "SELECT \r\n"
			+"    r.receipt AS 'Receipt NBR',\r\n" 
			+"    r.purchase_order AS 'PO NBR',\r\n" 
			+"    r.whm_status AS 'Status',\r\n" 
			+"    IFNULL(s.RCPT_STAT_DESC, 'N/A') AS 'ERS Status',\r\n" 
			+"    r.expected_count AS 'Expected Cases Count',\r\n" 
			+"    r.scanned_count AS 'Scanned Cases Count',\r\n" 
			+"    r.putaway_count AS 'Putaway Cases Count',\r\n" 
			+"    insert(concat(r.first_scan,'.000Z'),11,1,'T') AS 'First Case Scan',\r\n" 
			+"    insert(concat(r.last_scan,'.000Z'),11,1,'T') AS 'Last Case Scan',\r\n" 
			+"    DATEDIFF(CURDATE(), r.first_scan) AS 'Age'\r\n" 
			+"FROM\r\n" 
			+"    (SELECT \r\n" 
			+"        PO_HDR.PO_NBR AS purchase_order,\r\n" 
			+"            PO_HDR.OM_IND_CD AS HAF_FLAG,\r\n" 
			+"            RCPTS.RCPT_NBR AS receipt,\r\n" 
			+"            IFNULL(RCPTS.WHM_STATUS, 'N/A') AS whm_status,\r\n" 
			+"            RCPTS.RCPT_STAT_NBR AS ERS_STATUS_NBR,\r\n" 
			+"            COUNT(RCPT_CNTR.ORDER_RCPT_CNTR_ID) AS expected_count,\r\n" 
			+"            IFNULL(COUNT(RCPT_CNTR.CASE_SCAN_TS), 0) AS scanned_count,\r\n" 
			+"            IFNULL(COUNT(RCPT_CNTR.PUT_AWAY_TS), 0) AS putaway_count,\r\n" 
			+"            CONVERT_TZ(MIN(RCPT_CNTR.CASE_SCAN_TS), 'UTC', 'America/New_York') AS first_scan,\r\n" 
			+"            CONVERT_TZ(MAX(RCPT_CNTR.CASE_SCAN_TS), 'UTC', 'America/New_York') AS last_scan\r\n" 
			+"    FROM\r\n" 
			+"        orders.order_rcpt RCPTS\r\n" 
			+"    INNER JOIN orders.po_hdr PO_HDR ON PO_HDR.PO_NBR = RCPTS.PO_NBR\r\n" 
			+"    LEFT OUTER JOIN orders.order_rcpt_cntr RCPT_CNTR ON RCPTS.RCPT_NBR = RCPT_CNTR.RCPT_NBR\r\n" 
			+"    WHERE\r\n" 
			+"        PO_HDR.OM_IND_CD = 'HF'\r\n" 
			+"            AND RCPTS.RCPT_STAT_NBR IN (15 , 20)\r\n" 
			+"            AND RCPTS.RECV_LOC_NBR = 7221\r\n" 
			+"            AND RCPTS.RCPT_NBR = '%s' \r\n" 
			+"    GROUP BY purchase_order , receipt , whm_status , ERS_STATUS_NBR , HAF_FLAG UNION ALL SELECT \r\n" 
			+"        PO_HDR.PO_NBR AS purchase_order,\r\n" 
			+"            PO_HDR.OM_IND_CD AS HAF_FLAG,\r\n" 
			+"            RCPTS.RCPT_NBR AS receipt,\r\n" 
			+"            IFNULL(RCPTS.WHM_STATUS, 'N/A') AS whm_status,\r\n" 
			+"            RCPTS.RCPT_STAT_NBR AS ERS_STATUS_NBR,\r\n" 
			+"            MAX(RCPTS.LOGICAL_XPCTD_CNTR_QTY) AS expected_count,\r\n" 
			+"            IFNULL(COUNT(RCPT_CNTR.CASE_SCAN_TS), 0) AS scanned_count,\r\n" 
			+"            IFNULL(COUNT(RCPT_CNTR.PUT_AWAY_TS), 0) AS putaway_count,\r\n" 
			+"            CONVERT_TZ(MIN(RCPT_CNTR.CASE_SCAN_TS), 'UTC', 'America/New_York') AS first_scan,\r\n" 
			+"            CONVERT_TZ(MAX(RCPT_CNTR.CASE_SCAN_TS), 'UTC', 'America/New_York') AS last_scan\r\n" 
			+"    FROM\r\n" 
			+"        orders.order_rcpt RCPTS\r\n" 
			+"    INNER JOIN orders.po_hdr PO_HDR ON PO_HDR.PO_NBR = RCPTS.PO_NBR\r\n" 
			+"    INNER JOIN orders.order_rcpt_cntr RCPT_CNTR ON RCPTS.RCPT_NBR = RCPT_CNTR.RCPT_NBR\r\n" 
			+"    WHERE\r\n" 
			+"        PO_HDR.OM_IND_CD = 'HF'\r\n" 
			+"            AND RCPTS.RCPT_STAT_NBR = 10\r\n" 
			+"            AND RCPT_CNTR.CASE_SCAN_TS IS NOT NULL\r\n" 
			+"            AND RCPTS.RECV_LOC_NBR =" + CommonUtils.getWarehouseLocNbr()+"\r\n"
			+"            AND RCPTS.RCPT_NBR = '%s' \r\n" 
			+"    GROUP BY purchase_order , receipt , whm_status , ERS_STATUS_NBR , HAF_FLAG) AS r\r\n" 
			+"        LEFT OUTER JOIN\r\n" 
			+"    orders.order_rcpt_stat s ON s.RCPT_STAT_NBR = r.ERS_STATUS_NBR";
    
   
   }