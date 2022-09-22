package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLPODashboard {
	public static final String PODashboard_4walls = "\n"
			+ "Select\n"
			+ "\n"
			+ "group_concat(results.processArea order by results.processArea SEPARATOR ', ') as 'Area Flow',\n"
			+ "results.poNbr as 'PO NBR',\n"
			+ "results.rcptNbr as 'PO Receipt NBR',\n"
			+ "CASE WHEN results.status = 'OPEN' THEN 'OPEN' WHEN results.status = 'REL' THEN 'Released' WHEN results.status = 'PREL' THEN 'Partial Released' WHEN results.status = 'CIP' THEN 'Close In Progress' WHEN results.status = 'CAF' THEN 'Close Attempt Failed' WHEN results.status = 'CLOSE' THEN 'Closed' WHEN results.status = 'RIP' THEN 'Release In Progress' WHEN results.status = 'TRB' THEN 'Release In Trouble' WHEN results.status = 'RLF' THEN 'Release Attempt Failed' END as 'Status',\n"
			+ "results.orderQty as 'Expected Units',\n"
			+ "DATE_FORMAT(results.openDate, \"%%d-%%b-%%y\") as 'Date',\n"
			+ "results.age as 'Age'\n"
			+ "\n"
			+ "from\n"
			+ "(select distinct(POL.PROCESS_AREA) as processArea,RC.PO_NBR as poNbr, RC.RCPT_NBR as rcptNbr,RC.STATUS as status,po.dept_nbr,po.TOT_PO_ORDR_QTY as orderQty,\n"
			+ "            orderrcpt.DELIV_TS as openDate, DATEDIFF(CURDATE(),orderrcpt.DELIV_TS) as age\n"
			+ "            from pofourwalls.PO_RECEIPTS RC\n"
			+ "            inner join pofourwalls.PO_LINE_RECEIPTS pol on RC.ID = POL.PO_RECEIPT_ID\n"
			+ "            inner join  orders.PO_HDR po on RC.PO_NBR = PO.PO_NBR\n"
			+ "            inner join orders.ORDER_RCPT orderrcpt on orderrcpt.PO_NBR = po.PO_NBR\n"
			+ "            and RC.RCPT_NBR =  orderrcpt.RCPT_NBR and orderrcpt.PO_NBR = rc.PO_NBR\n"
			+ "            where\n"
			+ "            RC.ENABLED  =  1 and\n"
			+ "            RC.LOCN_NBR = 7221\n"
			+ "                                           AND POL.SKU_UPC NOT IN (1001,1002)\n"
			+ "                                           %s ) as results\n"
			+ "                                           group by results.rcptNbr,results.poNbr,results.status,results.dept_nbr,results.orderQty,results.openDate\n"
			+ "										   LIMIT 100\n";

	public static final String PODashboard_inventory = "Select (CEA.ATTRIBUTE_VALUE) as RCPT_NBR\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'TOTE' AND (ENTITY_ng.CONTAINER_STATUS IN ('CRE','LCT','DST','RLS','PRT','PREP','VSC')), INVENTORY_ng.QUANTITY, 0)) as tote_summary\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'LCN' AND (ENTITY_ng.CONTAINER_STATUS IN ('VSC','PTCRIP','PTCRLS')), INVENTORY_ng.QUANTITY, 0)) as lcn_summary\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'TOTE' AND (ENTITY_ng.CONTAINER_STATUS IN ('PRT','PREP','VSC')), INVENTORY_ng.QUANTITY, 0)) as tote_printed\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'TOTE' AND ENTITY_ng.CONTAINER_STATUS IN ('VSC'), INVENTORY_ng.QUANTITY, 0)) as tote_vased\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'BINBOX' AND (ENTITY_ng.CONTAINER_STATUS IN ('VSC','STG','SRT','SPW')), INVENTORY_ng.QUANTITY, 0)) as binbox_summary\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'PLT' AND (ENTITY_ng.CONTAINER_STATUS IN ('CRE','SIP','SRT','SPW')), INVENTORY_ng.QUANTITY, 0)) as pallet_summary\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'CRT' AND substr(INVENTORY_ng.reference_container, 22) IN (CEA.ATTRIBUTE_VALUE) AND (ENTITY_ng.CONTAINER_STATUS IN ('IPK','WGH','PCK')), INVENTORY_ng.QUANTITY, 0)) as carton_put_summary\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'BINBOX' AND (ENTITY_ng.CONTAINER_STATUS IN ('PTW')),INVENTORY_ng.QUANTITY, 0)) as binbox_putaway_summary\n" +
			"\t\t\t, SUM( IF(ENTITY_ng.CONTAINER_TYPE = 'PLT' AND ( ENTITY_ng.CONTAINER_STATUS IN ('PTW')),INVENTORY_ng.QUANTITY, 0)) as pallet_putaway_summary\n" +
			"\t\t\t, SUM(IF(ENTITY_ng.CONTAINER_TYPE = 'CRT' AND substr(INVENTORY_ng.reference_container, 22) IN (CEA.ATTRIBUTE_VALUE) AND  (ENTITY_ng.CONTAINER_STATUS IN ('MFT','SHR','SHP')), INVENTORY_ng.QUANTITY, 0)) AS carton_ship_summary\n" +
			"\t\t\tfrom INVENTORY_ng.inv_container_attributes as CEA\n" +
			"\t\t\tLEFT JOIN INVENTORY_ng.inv_container as ENTITY_ng ON ENTITY_ng.ID=CEA.INV_CONTAINER_ID\n" +
			"\t\t\tAND ENTITY_ng.CONTAINER_TYPE in ('TOTE','BINBOX','PLT','CRT','LCN')\n" +
			"\t\t\tAND ENTITY_ng.enabled = 1\n" +
			"\t\t\tAND ENTITY_ng.LOCN_NBR = 7221\n" +
			"\t\t\tAND exists (select 1 from INVENTORY.COMMON_ENTITY_ATTRIBUTES where ATTRIBUTE_NAME = 'PO' and ATTRIBUTE_VALUE in (4873879) and ENTITY_ID=ENTITY_ng.ID)\n" +
			"\t\t\tLEFT JOIN INVENTORY_ng.INVENTORY_SNAPSHOT AS INVENTORY_ng ON ENTITY_ng.barcode=INVENTORY_ng.CONTAINER\n" +
			"\t\t\t and INVENTORY_ng.ENABLED = 1\n" +
			"\t\t\t where CEA.ATTRIBUTE_NAME = 'POReceipt'\n" +
			"\t\t\t and CEA.ENABLED = 1\n" +
			"\t\t\t AND CEA.ATTRIBUTE_VALUE in (%s)\n" +
			"\t\t\t GROUP BY CEA.ATTRIBUTE_VALUE";




			/*""
    		+ "Select (CEA.ATTRIBUTE_VALUE) as RCPT_NBR\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'TOTE' AND (ENTITY.ENTITY_STATUS IN ('CRE','LCT','DST','RLS','PRT','PREP','VSC')), INVENTORY.QUANTITY, 0)) as tote_summary\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'LCN' AND (ENTITY.ENTITY_STATUS IN ('VSC','PTCRIP','PTCRLS')), INVENTORY.QUANTITY, 0)) as lcn_summary\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'TOTE' AND (ENTITY.ENTITY_STATUS IN ('PRT','PREP','VSC')), INVENTORY.QUANTITY, 0)) as tote_printed\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'TOTE' AND ENTITY.ENTITY_STATUS IN ('VSC'), INVENTORY.QUANTITY, 0)) as tote_vased\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'BINBOX' AND (ENTITY.ENTITY_STATUS IN ('VSC','STG','SRT','SPW')), INVENTORY.QUANTITY, 0)) as binbox_summary\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'PLT' AND (ENTITY.ENTITY_STATUS IN ('CRE','SIP','SRT','SPW')), INVENTORY.QUANTITY, 0)) as pallet_summary\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'CRT' AND substr(INVENTORY.reference_container, 22) IN (CEA.ATTRIBUTE_VALUE) AND (ENTITY.ENTITY_STATUS IN ('IPK','WGH','PCK')), INVENTORY.QUANTITY, 0)) as carton_put_summary\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'BINBOX' AND ( ENTITY.ENTITY_STATUS IN ('PTW')),INVENTORY.QUANTITY, 0)) as binbox_putaway_summary\n"
    		+ "			, SUM( IF(ENTITY.ENTITY_TYPE = 'PLT' AND ( ENTITY.ENTITY_STATUS  IN ('PTW')),INVENTORY.QUANTITY, 0)) as pallet_putaway_summary\n"
    		+ "			, SUM(IF(ENTITY.ENTITY_TYPE = 'CRT' AND substr(INVENTORY.reference_container, 22) IN (CEA.ATTRIBUTE_VALUE) AND  (ENTITY.ENTITY_STATUS IN ('MFT','SHR','SHP')), INVENTORY.QUANTITY, 0)) AS carton_ship_summary\n"
    		+ "			from INVENTORY.COMMON_ENTITY_ATTRIBUTES as CEA\n"
    		+ "			LEFT JOIN INVENTORY.ENTITY as ENTITY ON ENTITY.ID=CEA.ENTITY_ID\n"
    		+ "			AND ENTITY.entity_type in ('TOTE','BINBOX','PLT','CRT','LCN')\n"
    		+ "			AND ENTITY.enabled = 1\n"
    		+ "			AND ENTITY.LOCN_NBR = 7221\n"
    		+ "			AND exists (select 1 from INVENTORY.COMMON_ENTITY_ATTRIBUTES where ATTRIBUTE_NAME = 'PO' and ATTRIBUTE_VALUE in (%s) and ENTITY_ID=ENTITY.ID)\n"
    		+ "			LEFT JOIN INVENTORY.INVENTORY_SNAPSHOT AS INVENTORY ON ENTITY.ENTITY_ID=INVENTORY.CONTAINER\n"
    		+ "			 and INVENTORY.ENABLED = 1\n"
    		+ "			 where CEA.ATTRIBUTE_NAME = 'POReceipt'\n"
    		+ "			 and CEA.ENABLED = 1\n"
    		+ "			 AND CEA.ATTRIBUTE_VALUE in (%s)\n"
    		+ "			 GROUP BY CEA.ATTRIBUTE_VALUE";*/

}
