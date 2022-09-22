package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLPo4Walls {


    public static final String DISTRO_ALLOCATED_QTY_SUM = "select sum(allocated_qty) from pofourwalls.distribution dist inner join pofourwalls.po_line_receipts plr on dist.po_line_receipt_id=plr.id " +
            " inner join pofourwalls.po_receipts pr on plr.po_receipt_id=pr.id where plr.barcode = '%s'";
    public static final String Distribution = "select barcode_id, allocated_qty, locn_nbr, store_nbr from pofourwalls.distribution where barcode_ID in ('%s')";
    public static final String PO_Receipt_Report_Id = "select * from pofourwalls.po_rcpt_xref where po_nbr = %s order by created_ts asc";

    public static final String TOTE_ALLOC_SQL = "SELECT TOTE_ID,SKU_UPC,DIST.LOCN_NBR,DEPT_NBR,QTY, STORE_TYPE,BARCODE FROM pofourwalls.TOTE_ALLOC TOTALLOC "+
                        " JOIN pofourwalls.DISTRIBUTION DIST ON DIST.ID = TOTALLOC.DISTRIBUTION_ID  JOIN pofourwalls.PO_LINE_RECEIPTS LNRCPT ON LNRCPT.ID =  DIST.PO_LINE_RECEIPT_ID  "+
                        " JOIN pofourwalls.PO_RECEIPTS RCPT ON RCPT.ID = LNRCPT.PO_RECEIPT_ID  WHERE   RCPT.po_nbr='%s' and RCPT.RCPT_NBR='%s'  ORDER BY TOTE_ID";

    public static final String GET_SKU_BARCODE = "select LNRCPT.SKU_UPC, LNRCPT.BARCODE from pofourwalls.po_receipts RCPT JOIN pofourwalls.po_line_receipts LNRCPT ON RCPT.ID = LNRCPT.PO_RECEIPT_ID Where RCPT.PO_NBR = '%s' and RCPT.RCPT_NBR = '%s' and sku_upc not in (1001,1002)";

    public static final String DISTRO_ALLOCATED_NEW_STORE = "select LOCN_NBR,allocated_qty from pofourwalls.distribution where po_line_receipt_id in (select id from pofourwalls.po_line_receipts where po_receipt_id in (select id from pofourwalls.po_receipts where po_nbr = '%s' and rcpt_nbr = '%s') and SKU_UPC ='%s') and STORE_TYPE='N' order by allocated_qty desc";
    public static final String DISTRO_ALLOCATED_REG_STORE = "select LOCN_NBR,allocated_qty from pofourwalls.distribution where po_line_receipt_id in (select id from pofourwalls.po_line_receipts where po_receipt_id in (select id from pofourwalls.po_receipts where po_nbr = '%s' and rcpt_nbr = '%s') and SKU_UPC ='%s') and STORE_TYPE='R' order by allocated_qty desc";
    public static final String DISTRO_ALLOCATED_PACKAWAY = "select LOCN_NBR,allocated_qty from pofourwalls.distribution where po_line_receipt_id in (select id from pofourwalls.po_line_receipts where po_receipt_id in (select id from pofourwalls.po_receipts where po_nbr = '%s' and rcpt_nbr = '%s') and SKU_UPC ='%s') and STORE_TYPE='P' order by allocated_qty desc";

    public static final String DISTRO_STATUS = "select REQ_TYPE,STATUS from pofourwalls.distro_request where po_nbr = '%s' and rcpt_nbr = '%s' order by updated_date desc";
    public static final String PO_STATUS = "select STATUS from pofourwalls.po_receipts where po_nbr = '%s' and rcpt_nbr = '%s' order by updated_date desc";

    public static final String GET_BARCODE = "select LNRCPT.BARCODE from pofourwalls.po_receipts RCPT JOIN "
    		+ "pofourwalls.po_line_receipts LNRCPT ON RCPT.ID = LNRCPT.PO_RECEIPT_ID Where " + 
    		"RCPT.PO_NBR = '%s' and RCPT.RCPT_NBR = '%s' and LNRCPT.sku_upc='%s'";
    
    public static final String GET_SKU = "select LNRCPT.SKU_UPC from pofourwalls.po_receipts RCPT JOIN "
    		+ "pofourwalls.po_line_receipts LNRCPT ON RCPT.ID = LNRCPT.PO_RECEIPT_ID Where " + 
    		"RCPT.PO_NBR = '%s' and RCPT.RCPT_NBR = '%s'";

    public static final String UPDATE_PORECEIPT_STATUS = "set sql_safe_updates = 0;\n"
			+"update po_receipts set status = 'OPEN' where PO_NBR = '%s' and RCPT_NBR = '%s';\n"
    		+"set sql_safe_updates = 1;";

    public static final String UPDATE_PORECEIPT_STATUS_TO_CLOSE = "set sql_safe_updates = 0;\n"
            +"update po_receipts set status = 'CLOSE' where PO_NBR = '%s' and RCPT_NBR = '%s';\n"
            +"set sql_safe_updates = 1;";
}
