package com.macys.mst.DC2.EndToEnd.db.app;

import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;

public class SQLOrders {
	CommonUtils commonUtils = new CommonUtils();
	public static final String PODetails = "SELECT DISTINCT\n" +
			"    PO_LINE_ITM.SKU_UPC_NBR AS 'In-House UPC',\n" +
			"    PO_LINE_ITM.VENDOR_UPC_NBR AS 'Vendor UPC',\n" +
			"    PO_LINE_ITM.MARKSTYLE AS 'MKSTYL',\n" +
			"    PO_LINE_ITM.V_STYLE AS 'VNDSTYL',\n" +
			"    TRIM(po_line_itm.PID) AS PID,\n" +
			"    TRIM(PO_LINE_ITM.PID_DESC) AS 'PID Desc',\n" +
			"    TRIM(PO_LINE_ITM.NRF_COLOR_DESC) AS 'Col Desc',\n" +
			"    TRIM(PO_LINE_ITM.NRF_SIZE_DESC) AS 'Size Desc',\n" +
			"    PO_LINE_ITM.COMPARE_AT_PRC AS 'Compare @ Retail',\n" +
			"    PO_LINE_ITM.TOT_SKU_ORDR_QTY AS 'Exp Units',\n" +
			"    (Select IFNULL(SUM(INVENTORY.QUANTITY),0)\n" +
			"                                FROM INVENTORY_NG.INV_CONTAINER_ATTRIBUTES as CEA\n" +
			"                                INNER JOIN INVENTORY_NG.INV_CONTAINER as ENTITY ON ENTITY.ID=CEA.INV_CONTAINER_ID\n" +
			"                                AND ENTITY.CONTAINER_TYPE in ('CRT','BINBOX')\n" +
			"                                AND ENTITY.enabled = 1\n" +
			"                                AND ENTITY.LOCN_NBR ="+ CommonUtils.getWarehouseLocNbr()+"\n" +
			"                                INNER JOIN INVENTORY_NG.INV_CONTAINER_ATTRIBUTES AS CEA1 ON CEA1.INV_CONTAINER_ID=CEA.INV_CONTAINER_ID\n" +
			"                                INNER JOIN INVENTORY_NG.INVENTORY_SNAPSHOT AS INVENTORY ON ENTITY.ID=INVENTORY.CONTAINER\n" +
			"                                 AND INVENTORY.ENABLED = 1\n" +
			"                                 where CEA.ATTRIBUTE_NAME = 'POReceipt'\n" +
			"                                 and CEA.ENABLED = 1\n" +
			"                                 AND CEA.ATTRIBUTE_VALUE = po_receipts.RCPT_NBR\n" +
			"                                 AND CEA1.ATTRIBUTE_NAME = 'PO'\n" +
			"                                 AND CEA1.ENABLED = 1\n" +
			"                                 AND CEA1.ATTRIBUTE_VALUE = po_receipts.PO_NBR\n" +
			"                                 AND INVENTORY.ITEM = PO_LINE_ITM.SKU_UPC_NBR) AS 'Act Units',\n" +
			"    (Select        \n" +
			"\n" +
			"typ.ATTR_TYP_DESC as attTypeDesc\n" +
			"\n" +
			"from \n" +
			"\n" +
			"orders.PO_ATTR as pa \n" +
			"inner join orders.ATTR_TYP as typ on typ.ATTR_TYP_ID = pa.ATTR_TYP_ID \n" +
			"inner join orders.ATTR_CAT as cat on cat.ATTR_CAT_ID = typ.ATTR_CAT_ID  \n" +
			"where pa.DEPT = po_line_itm.DEPT_NBR\n" +
			"and cat.ATTR_CAT_DESC = 'Process Area Confirmation') AS 'Process Area'\n" +
			"FROM\n" +
			"    orders.po_line_itm\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_receipts ON PO_LINE_ITM.PO_NBR = po_receipts.PO_NBR\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_line_receipts ON po_line_itm.SKU_UPC_NBR = po_line_receipts.SKU_UPC\n" +
			"WHERE\n" +
			"    po_receipts.PO_NBR = '%s'\n" +
			"                AND po_receipts.RCPT_NBR = '%s'\n";
/*
	"SELECT DISTINCT\n" +
			"    PO_LINE_ITM.SKU_UPC_NBR AS 'In-House UPC',\n" +
			"    PO_LINE_ITM.VENDOR_UPC_NBR AS 'Vendor UPC',\n" +
			"    PO_LINE_ITM.MARKSTYLE AS 'MKSTYL',\n" +
			"    PO_LINE_ITM.V_STYLE AS 'VNDSTYL',\n" +
			"    TRIM(po_line_itm.PID) AS PID,\n" +
			"    TRIM(PO_LINE_ITM.PID_DESC) AS 'PID Desc',\n" +
			"    TRIM(PO_LINE_ITM.NRF_COLOR_DESC) AS 'Col Desc',\n" +
			"    TRIM(PO_LINE_ITM.NRF_SIZE_DESC) AS 'Size Desc',\n" +
			"    PO_LINE_ITM.COMPARE_AT_PRC AS 'Compare @ Retail',\n" +
			"    PO_LINE_ITM.TOT_SKU_ORDR_QTY AS 'Exp Units',\n" +
			"    (Select IFNULL(SUM(INVENTORY.QUANTITY),0)\n" +
			"                                 from INVENTORY.COMMON_ENTITY_ATTRIBUTES as CEA\n" +
			"                                INNER JOIN INVENTORY.ENTITY as ENTITY ON ENTITY.ID=CEA.ENTITY_ID\n" +
			"                                AND ENTITY.entity_type in ('CRT','BINBOX')\n" +
			"                                AND ENTITY.enabled = 1\n" +
			"                                AND ENTITY.LOCN_NBR ="+ CommonUtils.getWarehouseLocNbr()+"\n" +
			"                                   INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES AS CEA1 ON CEA1.ENTITY_ID=CEA.ENTITY_ID\n" +
			"                                INNER JOIN INVENTORY.INVENTORY_SNAPSHOT AS INVENTORY ON ENTITY.ENTITY_ID=INVENTORY.CONTAINER\n" +
			"                                 and INVENTORY.ENABLED = 1\n" +
			"                                 where CEA.ATTRIBUTE_NAME = 'POReceipt'\n" +
			"                                 and CEA.ENABLED = 1\n" +
			"                                 AND CEA.ATTRIBUTE_VALUE = po_receipts.RCPT_NBR\n" +
			"                                 AND CEA1.ATTRIBUTE_NAME = 'PO'\n" +
			"                                 AND CEA1.ENABLED = 1\n" +
			"                                 AND CEA1.ATTRIBUTE_VALUE = po_receipts.PO_NBR\n" +
			"                                 AND INVENTORY.ITEM = PO_LINE_ITM.SKU_UPC_NBR) AS 'Act Units',\n" +
			"    (Select        \n" +
			"\n" +
			"typ.ATTR_TYP_DESC as attTypeDesc\n" +
			"\n" +
			"from \n" +
			"\n" +
			"orders.PO_ATTR as pa \n" +
			"inner join orders.ATTR_TYP as typ on typ.ATTR_TYP_ID = pa.ATTR_TYP_ID \n" +
			"inner join orders.ATTR_CAT as cat on cat.ATTR_CAT_ID = typ.ATTR_CAT_ID  \n" +
			"where pa.DEPT = po_line_itm.DEPT_NBR\n" +
			"and cat.ATTR_CAT_DESC = 'Process Area Confirmation') AS 'Process Area'\n" +
			"FROM\n" +
			"    orders.po_line_itm\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_receipts ON PO_LINE_ITM.PO_NBR = po_receipts.PO_NBR\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_line_receipts ON po_line_itm.SKU_UPC_NBR = po_line_receipts.SKU_UPC\n" +
			"WHERE\n" +
			"    po_receipts.PO_NBR = '%s'\n" +
			"                AND po_receipts.RCPT_NBR = '%s'\n";*/

	public static final String PODistro = "SELECT DISTINCT\n" +
			"    PO_LINE_ITM.SKU_UPC_NBR AS 'In-House UPC',\n" +
			"    po_location_dtl.LOCN_STORE_NBR AS 'Store #',\n" +
			"    TRIM(po_location_dtl.STORE_NAME) AS 'Store Name',\n" +
			"    po_location_dtl.STORE_ORDR_QTY AS 'Expected',\n" +
			"    (Select IF(SUM(PO_ID_NBR = 14 AND PO_ID_VALUE = 'Y') < 1, 'N', 'Y')  from orders.po_identifiers pi where pi.po_nbr =  '%s') as 'New Store?',\n" +
			"                (Select IFNULL(SUM(INVENTORY.QUANTITY),0)\n" +
			"                                from INVENTORY_NG.INV_CONTAINER_ATTRIBUTES as CEA\n" +
			"                                INNER JOIN INVENTORY_NG.INV_CONTAINER as ENTITY ON ENTITY.ID=CEA.INV_CONTAINER_ID\n" +
			"                                AND ENTITY.CONTAINER_TYPE in ('CRT','BINBOX')\n" +
			"                                AND ENTITY.enabled = 1\n" +
			"                                AND ENTITY.LOCN_NBR ="+CommonUtils.getWarehouseLocNbr()+"\n" +
			"                                INNER JOIN INVENTORY_NG.INV_CONTAINER_ATTRIBUTES AS CEA1 ON CEA1.INV_CONTAINER_ID=CEA.INV_CONTAINER_ID\n" +
			"                                INNER JOIN INVENTORY_NG.INV_CONTAINER_ATTRIBUTES AS CEA2 ON CEA2.INV_CONTAINER_ID=CEA.INV_CONTAINER_ID\n" +
			"                                INNER JOIN INVENTORY_NG.INVENTORY_SNAPSHOT AS INVENTORY ON ENTITY.ID=INVENTORY.CONTAINER\n" +
			"                                 and INVENTORY.ENABLED = 1\n" +
			"                                 where CEA.ATTRIBUTE_NAME = 'POReceipt'\n" +
			"                                 and CEA.ENABLED = 1\n" +
			"                                 AND CEA.ATTRIBUTE_VALUE = po_receipts.RCPT_NBR\n" +
			"                                 AND CEA1.ATTRIBUTE_NAME = 'PO'\n" +
			"                                 AND CEA1.ENABLED = 1\n" +
			"                                 AND CEA1.ATTRIBUTE_VALUE = po_receipts.PO_NBR\n" +
			"                                 AND CEA2.ATTRIBUTE_NAME = 'Store'\n" +
			"                                 AND CEA2.ENABLED = 1\n" +
			"                                 AND CEA2.ATTRIBUTE_VALUE = po_location_dtl.LOCN_STORE_NBR\n" +
			"                                 AND INVENTORY.ITEM = PO_LINE_ITM.SKU_UPC_NBR) AS 'Actual'\n" +
			"    FROM\n" +
			"    orders.po_location_dtl\n" +
			"        INNER JOIN\n" +
			"    ORDERS.PO_LINE_ITM ON po_line_itm.PO_LINE_ITM_ID = po_location_dtl.PO_LINE_ITM_ID\n" +
			"        AND po_line_itm.ENABLED = po_location_dtl.ENABLED\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_receipts ON po_receipts.PO_NBR = po_location_dtl.PO_NBR\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_line_receipts ON po_line_receipts.PO_RECEIPT_ID = po_receipts.ID\n" +
			"WHERE\n" +
			"    po_receipts.PO_NBR = '%s'\n" +
			"                AND po_receipts.RCPT_NBR = '%s'\n" +
			"        AND po_line_itm.SKU_UPC_NBR = '%s';\n";
 /* PO Distro from Inventory
	"SELECT DISTINCT\n" +
			"    PO_LINE_ITM.SKU_UPC_NBR AS 'In-House UPC',\n" +
			"    po_location_dtl.LOCN_STORE_NBR AS 'Store #',\n" +
			"    TRIM(po_location_dtl.STORE_NAME) AS 'Store Name',\n" +
			"    po_location_dtl.STORE_ORDR_QTY AS 'Expected',\n" +
			"    (Select IF(SUM(PO_ID_NBR = 14 AND PO_ID_VALUE = 'Y') < 1, 'N', 'Y')  from orders.po_identifiers pi where pi.po_nbr =  '%s') as 'New Store?',\n" +
			"                (Select IFNULL(SUM(INVENTORY.QUANTITY),0)\n" +
			"                                from INVENTORY.COMMON_ENTITY_ATTRIBUTES as CEA\n" +
			"                                INNER JOIN INVENTORY.ENTITY as ENTITY ON ENTITY.ID=CEA.ENTITY_ID\n" +
			"                                AND ENTITY.entity_type in ('CRT','BINBOX')\n" +
			"                                AND ENTITY.enabled = 1\n" +
			"                                AND ENTITY.LOCN_NBR ="+CommonUtils.getWarehouseLocNbr()+"\n" +
			"                                INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES AS CEA1 ON CEA1.ENTITY_ID=CEA.ENTITY_ID\n" +
			"                                INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES AS CEA2 ON CEA2.ENTITY_ID=CEA.ENTITY_ID\n" +
			"                                INNER JOIN INVENTORY.INVENTORY_SNAPSHOT AS INVENTORY ON ENTITY.ENTITY_ID=INVENTORY.CONTAINER\n" +
			"                                 and INVENTORY.ENABLED = 1\n" +
			"                                 where CEA.ATTRIBUTE_NAME = 'POReceipt'\n" +
			"                                 and CEA.ENABLED = 1\n" +
			"                                 AND CEA.ATTRIBUTE_VALUE = po_receipts.RCPT_NBR\n" +
			"                                 AND CEA1.ATTRIBUTE_NAME = 'PO'\n" +
			"                                 AND CEA1.ENABLED = 1\n" +
			"                                 AND CEA1.ATTRIBUTE_VALUE = po_receipts.PO_NBR\n" +
			"                                 AND CEA2.ATTRIBUTE_NAME = 'Store'\n" +
			"                                 AND CEA2.ENABLED = 1\n" +
			"                                 AND CEA2.ATTRIBUTE_VALUE = po_location_dtl.LOCN_STORE_NBR\n" +
			"                                 AND INVENTORY.ITEM = PO_LINE_ITM.SKU_UPC_NBR) AS 'Actual'\n" +
			"    FROM\n" +
			"    orders.po_location_dtl\n" +
			"        INNER JOIN\n" +
			"    ORDERS.PO_LINE_ITM ON po_line_itm.PO_LINE_ITM_ID = po_location_dtl.PO_LINE_ITM_ID\n" +
			"        AND po_line_itm.ENABLED = po_location_dtl.ENABLED\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_receipts ON po_receipts.PO_NBR = po_location_dtl.PO_NBR\n" +
			"        INNER JOIN\n" +
			"    pofourwalls.po_line_receipts ON po_line_receipts.PO_RECEIPT_ID = po_receipts.ID\n" +
			"WHERE\n" +
			"    po_receipts.PO_NBR = '%s'\n" +
			"                AND po_receipts.RCPT_NBR = '%s'\n" +
			"        AND po_line_itm.SKU_UPC_NBR = '%s';\n";*/

	public static final String POInquiry = ""
			+ "SELECT "
			+ " \n"
			+ "po_rcpt_xref.PO_RCPT_ID as 'Report ID', \n"
			+ "order_rcpt.PO_NBR as 'PO', \n"
			+ "order_rcpt.RCPT_NBR as 'Receipt #', \n"
			+ "order_rcpt_stat.RCPT_STAT_DESC as 'ERS Receipt Status', \n"
			+ "po_hdr.PO_STAT_DESC as 'PO Status', \n"
			+ "IF(DISTRO_CMPLT_TS IS NOT NULL,'Y','N') AS 'Distro', \n"
			+ "TRIM(po_hdr.PO_GEN_DESC) AS 'Gen Type', \n"
			+ "IFNULL(APPTDET.APPT_NBR,'0') as 'Appt #', \n"
			+ "APPTDET.APPT_SCHD_ARVL_TS as 'Appt Date', \n"
			+ "APPTDET.DESCRPTN as 'Appt Status', \n"
			+ "(SELECT IF(COUNT(prx1.po_nbr)<2,'N','Y') FROM orders.po_rcpt_xref prx1  WHERE prx1.po_nbr = po_rcpt_xref.po_nbr group by prx1.po_nbr) as 'Multiple Appts', \n"
			+ "(SELECT IF(SUM(PO_ID_NBR = 14 AND PO_ID_VALUE = 'Y') < 1, 'N', 'Y') FROM orders.po_identifiers pi, orders.po_hdr ph WHERE pi.loc_nbr = ph.locn_div_nbr AND pi.enabled = 1 AND ph.enabled = 1 AND pi.po_nbr =  po_rcpt_xref.po_nbr) AS 'New Store', \n"
			+ "(SELECT COUNT(SKU_UPC_NBR) FROM orders.po_line_itm where po_line_itm.po_nbr = po_rcpt_xref.PO_NBR) as 'UPC Count', \n"
			+ "(SELECT SUM(TOT_SKU_ORDR_QTY) FROM orders.po_line_itm where po_line_itm.po_nbr = po_rcpt_xref.PO_NBR) as 'Exp. Units', \n"
			+ "(Select IFNULL(SUM(INVENTORY.QUANTITY),0) \n"
			+ "                                from inventory_ng.inv_container_attributes as CEA \n"
			+ "                                INNER JOIN inventory_ng.inv_container as ENTITY ON ENTITY.ID=CEA.INV_CONTAINER_ID \n"
			+ "                                AND ENTITY.CONTAINER_TYPE in ('TOTE','BINBOX','CRT') \n"
			+ "                                AND ENTITY.enabled = 1 \n"
			+ "                                AND ENTITY.LOCN_NBR ="+CommonUtils.getWarehouseLocNbr()+"\n"
			+ "                                INNER JOIN inventory_ng.inv_container_attributes AS CEA1 ON CEA1.INV_CONTAINER_ID=CEA.INV_CONTAINER_ID \n" +
			"                                INNER JOIN inventory_ng.inventory_snapshot AS INVENTORY ON ENTITY.ID=INVENTORY.CONTAINER  \n"
			+ "                                 and INVENTORY.ENABLED = 1 \n"
			+ "                                 where CEA.ATTRIBUTE_NAME = 'POReceipt' \n"
			+ "                                 and CEA.ENABLED = 1 \n"
			+ "                                 AND CEA.ATTRIBUTE_VALUE = po_rcpt_xref.RCPT_NBR \n"
			+ "								 AND CEA1.ATTRIBUTE_NAME = 'PO' \n"
			+ "                                 AND CEA1.ENABLED = 1 \n"
			+ "                                 AND CEA1.ATTRIBUTE_VALUE = po_rcpt_xref.PO_NBR) as 'Act. Units', \n"
			+ "(Select concat('[',group_concat(DISTINCT(PROCESS_AREA) order by PROCESS_AREA SEPARATOR ', '),']') from \n"
			+ "			pofourwalls.po_line_receipts \n"
			+ "			inner join pofourwalls.po_receipts \n"
			+ "				on \n"
			+ "			po_line_receipts.po_receipt_id = po_receipts.id \n"
			+ "		where (po_receipts.po_nbr = po_rcpt_xref.PO_NBR and po_receipts.rcpt_nbr = po_rcpt_xref.RCPT_NBR)) as 'Process Area' \n"
			+ "      \n"
			+ "FROM \n"
			+ "    orders.order_rcpt \n"
			+ "        INNER JOIN \n"
			+ "    orders.po_rcpt_xref ON (order_rcpt.PO_NBR = po_rcpt_xref.PO_NBR and order_rcpt.RCPT_NBR = po_rcpt_xref.RCPT_NBR) \n"
			+ "        INNER JOIN \n"
			+ "    orders.po_hdr ON po_hdr.PO_NBR = po_rcpt_xref.PO_NBR \n"
			+ "        INNER JOIN \n"
			+ "    orders.order_rcpt_stat ON order_rcpt_stat.RCPT_STAT_NBR = order_rcpt.RCPT_STAT_NBR \n"
			+ "                                LEFT OUTER JOIN \n"
			+ "     (Select \n"
			+ "  \n"
			+ "po_appt_xref.po_nbr, \n"
			+ "po_appt_xref.rcpt_nbr, \n"
			+ "load_appt.APPT_NBR, \n"
			+ "load_appt.APPT_SCHD_ARVL_TS , \n"
			+ "load_appt_stat.DESCRPTN \n"
			+ "from \n"
			+ "  \n"
			+ "shipment.po_appt_xref \n"
			+ "                                INNER JOIN \n"
			+ "shipment.load_appt on load_appt.APPT_NBR = po_appt_xref.APPT_NBR \n"
			+ "                                INNER JOIN \n"
			+ "shipment.load_appt_stat on load_appt.APPT_STAT_NBR = load_appt_stat.APPT_STAT_NBR) as APPTDET on (po_rcpt_xref.PO_NBR = APPTDET.PO_NBR and po_rcpt_xref.RCPT_NBR = APPTDET.RCPT_NBR) \n"
			+ "                                                WHERE \n"
			+ "    po_rcpt_xref.ENABLED = 1"
			+ "	%s \n";

/* Query from Inventory
	+ "SELECT "
			+ " \n"
			+ "po_rcpt_xref.PO_RCPT_ID as 'Report ID', \n"
			+ "order_rcpt.PO_NBR as 'PO', \n"
			+ "order_rcpt.RCPT_NBR as 'Receipt #', \n"
			+ "order_rcpt_stat.RCPT_STAT_DESC as 'ERS Receipt Status', \n"
			+ "po_hdr.PO_STAT_DESC as 'PO Status', \n"
			+ "IF(DISTRO_CMPLT_TS IS NOT NULL,'Y','N') AS 'Distro', \n"
			+ "TRIM(po_hdr.PO_GEN_DESC) AS 'Gen Type', \n"
			+ "IFNULL(APPTDET.APPT_NBR,'0') as 'Appt #', \n"
			+ "APPTDET.APPT_SCHD_ARVL_TS as 'Appt Date', \n"
			+ "APPTDET.DESCRPTN as 'Appt Status', \n"
			+ "(SELECT IF(COUNT(prx1.po_nbr)<2,'N','Y') FROM orders.po_rcpt_xref prx1  WHERE prx1.po_nbr = po_rcpt_xref.po_nbr group by prx1.po_nbr) as 'Multiple Appts', \n"
			+ "(SELECT IF(SUM(PO_ID_NBR = 14 AND PO_ID_VALUE = 'Y') < 1, 'N', 'Y') FROM orders.po_identifiers pi, orders.po_hdr ph WHERE pi.loc_nbr = ph.locn_div_nbr AND pi.enabled = 1 AND ph.enabled = 1 AND pi.po_nbr =  po_rcpt_xref.po_nbr) AS 'New Store', \n"
			+ "(SELECT COUNT(SKU_UPC_NBR) FROM orders.po_line_itm where po_line_itm.po_nbr = po_rcpt_xref.PO_NBR) as 'UPC Count', \n"
			+ "(SELECT SUM(TOT_SKU_ORDR_QTY) FROM orders.po_line_itm where po_line_itm.po_nbr = po_rcpt_xref.PO_NBR) as 'Exp. Units', \n"
			+ "(Select IFNULL(SUM(INVENTORY.QUANTITY),0) \n"
			+ "                                from INVENTORY.COMMON_ENTITY_ATTRIBUTES as CEA \n"
			+ "                                INNER JOIN INVENTORY.ENTITY as ENTITY ON ENTITY.ID=CEA.ENTITY_ID \n"
			+ "                                AND ENTITY.entity_type in ('TOTE','BINBOX','CRT') \n"
			+ "                                AND ENTITY.enabled = 1 \n"
			+ "                                AND ENTITY.LOCN_NBR ="+CommonUtils.getWarehouseLocNbr()+"\n"
			+ "                                INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES AS CEA1 ON CEA1.ENTITY_ID=CEA.ENTITY_ID \n"
			+ "                                INNER JOIN INVENTORY.INVENTORY_SNAPSHOT AS INVENTORY ON ENTITY.ENTITY_ID=INVENTORY.CONTAINER \n"
			+ "                                 and INVENTORY.ENABLED = 1 \n"
			+ "                                 where CEA.ATTRIBUTE_NAME = 'POReceipt' \n"
			+ "                                 and CEA.ENABLED = 1 \n"
			+ "                                 AND CEA.ATTRIBUTE_VALUE = po_rcpt_xref.RCPT_NBR \n"
			+ "								 AND CEA1.ATTRIBUTE_NAME = 'PO' \n"
			+ "                                 AND CEA1.ENABLED = 1 \n"
			+ "                                 AND CEA1.ATTRIBUTE_VALUE = po_rcpt_xref.PO_NBR) as 'Act. Units', \n"
			+ "(Select concat('[',group_concat(DISTINCT(PROCESS_AREA) order by PROCESS_AREA SEPARATOR ', '),']') from \n"
			+ "			pofourwalls.po_line_receipts \n"
			+ "			inner join pofourwalls.po_receipts \n"
			+ "				on \n"
			+ "			po_line_receipts.po_receipt_id = po_receipts.id \n"
			+ "		where (po_receipts.po_nbr = po_rcpt_xref.PO_NBR and po_receipts.rcpt_nbr = po_rcpt_xref.RCPT_NBR)) as 'Process Area' \n"
			+ "      \n"
			+ "FROM \n"
			+ "    orders.order_rcpt \n"
			+ "        INNER JOIN \n"
			+ "    orders.po_rcpt_xref ON (order_rcpt.PO_NBR = po_rcpt_xref.PO_NBR and order_rcpt.RCPT_NBR = po_rcpt_xref.RCPT_NBR) \n"
			+ "        INNER JOIN \n"
			+ "    orders.po_hdr ON po_hdr.PO_NBR = po_rcpt_xref.PO_NBR \n"
			+ "        INNER JOIN \n"
			+ "    orders.order_rcpt_stat ON order_rcpt_stat.RCPT_STAT_NBR = order_rcpt.RCPT_STAT_NBR \n"
			+ "                                LEFT OUTER JOIN \n"
			+ "     (Select \n"
			+ "  \n"
			+ "po_appt_xref.po_nbr, \n"
			+ "po_appt_xref.rcpt_nbr, \n"
			+ "load_appt.APPT_NBR, \n"
			+ "load_appt.APPT_SCHD_ARVL_TS , \n"
			+ "load_appt_stat.DESCRPTN \n"
			+ "from \n"
			+ "  \n"
			+ "shipment.po_appt_xref \n"
			+ "                                INNER JOIN \n"
			+ "shipment.load_appt on load_appt.APPT_NBR = po_appt_xref.APPT_NBR \n"
			+ "                                INNER JOIN \n"
			+ "shipment.load_appt_stat on load_appt.APPT_STAT_NBR = load_appt_stat.APPT_STAT_NBR) as APPTDET on (po_rcpt_xref.PO_NBR = APPTDET.PO_NBR and po_rcpt_xref.RCPT_NBR = APPTDET.RCPT_NBR) \n"
			+ "                                                WHERE \n"
			+ "    po_rcpt_xref.ENABLED = 1"
			+ "	%s \n";*/


	public static final String UPDATE_PORECEIPT_STATUS = "update order_rcpt set RCPT_STAT_NBR = '15' where PO_NBR = '%s' and RCPT_NBR = '%s'";


}
