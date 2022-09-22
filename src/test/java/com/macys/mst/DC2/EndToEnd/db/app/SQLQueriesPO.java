package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLQueriesPO {
    public static final String GET_PO_TESTDATA = "select * from po_rcpt_dtls where USE_CASE_ID = '%s' and RCPT_NBR !=0" +
            " and CREATE_USER = '%s' and enabled=1";
    public static final String GET_ALL_PO_TESTDATA = "select PO_nbr from po_rcpt_dtls where enabled=1";
    public static final String GET_PO_DTL = "select RCPT.RCPT_NBR, RCPT.PO_NBR, RCPT.REPORT_ID, LNRCPT.SKU_UPC, LNRCPT.BARCODE, RCPT.LOCN_NBR " +
            " from pofourwalls.po_receipts RCPT " +
            " JOIN pofourwalls.po_line_receipts LNRCPT ON RCPT.ID = LNRCPT.PO_RECEIPT_ID " +
            " Where RCPT.PO_NBR = '%s' and RCPT.RCPT_NBR = '%s' and LNRCPT.sku_upc not in (1001,1002)";

    public static final String GET_ORDER_PO_RECEIPT = "SELECT  po_rcpt_xref.PO_RCPT_ID,po_rcpt_xref.PO_NBR,po_rcpt_xref.RCPT_NBR,po_line_itm.PID_DESC,po_line_itm.SKU_UPC_NBR, " +
            " PO_HDR.DEPT_NBR,po_rcpt_xref.DIV_LOC_NBR, PO_HDR.LOCN_DIV_NBR, order_rcpt.DIV_LOC_NBR, order_rcpt.RECV_LOC_NBR, po_line_itm.TOT_SKU_OPEN_QTY " +
            " FROM  po_rcpt_xref " +
            " inner join PO_HDR on po_rcpt_xref.PO_NBR = PO_HDR.PO_NBR " +
            " inner join order_rcpt on po_rcpt_xref.PO_NBR = order_rcpt.PO_NBR " +
            " inner join po_line_itm on po_line_itm.PO_NBR = PO_HDR.PO_NBR " +
            " where po_rcpt_xref.PO_NBR in ('%s');";

    public static final String GET_PO_RECEPT = "select id from pofourwalls.po_receipts where PO_NBR = '%s'";

}
