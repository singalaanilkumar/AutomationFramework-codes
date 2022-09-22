package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLQueriesDRR {

    public static final String OVERRIDE_ATTRIBS_PO_SQL = ""
    		+ "Select "
    		+ "pplr.SKU_UPC, "
    		+ "pprlo.PROCESS_AREA_ID, "
    		+ "pprlo.CASE_ASSORTMENT_ID, "
    		+ "pprlo.CASE_TYPE_ID, "
    		+ "pprlo.INNER_PACK, "
    		+ "pprlo.PACK_QTY, "
    		+ "pprlo.PRODUCT_CUBE_ID, "
    		+ "pprlo.FRAGILE_ID, "
    		+ "pprlo.TICKET_TYPE, "
    		+ "pprlo.NO_OF_TICKETS, "
    		+ "pprlo.PACKAWAY_STRG_ATTR_ID, "
    		+ "pprlo.HAZMAT "
    		+ " "
    		+ "from "
    		+ " "
    		+ "pofourwalls.po_receipts ppr, "
    		+ "pofourwalls.po_line_receipts pplr, "
    		+ "pofourwalls.po_rcpts_line_ovrd pprlo "
    		+ " "
    		+ "where ppr.report_id = %s "
    		+ "and pplr.po_receipt_id = ppr.id "
    		+ "and pprlo.po_line_receipt_id = pplr.id";
    
    
    public static final String OVERRIDE_ATTRIBS_SKU_SQL = ""
    		+ "Select "
    		+ "pprlo.PROCESS_AREA_ID, "
    		+ "pprlo.CASE_ASSORTMENT_ID, "
    		+ "pprlo.CASE_TYPE_ID, "
    		+ "pprlo.INNER_PACK, "
    		+ "pprlo.PACK_QTY, "
    		+ "pprlo.PRODUCT_CUBE_ID, "
    		+ "pprlo.FRAGILE_ID, "
    		+ "pprlo.TICKET_TYPE, "
    		+ "pprlo.NO_OF_TICKETS, "
    		+ "pprlo.PACKAWAY_STRG_ATTR_ID, "
    		+ "pprlo.HAZMAT"
    		+ " "
    		+ "from "
    		+ " "
    		+ "pofourwalls.po_receipts ppr, "
    		+ "pofourwalls.po_line_receipts pplr, "
    		+ "pofourwalls.po_rcpts_line_ovrd pprlo "
    		+ " "
    		+ "where ppr.report_id = %s "
    		+ "and pplr.po_receipt_id = ppr.id "
    		+ "and pprlo.po_line_receipt_id = pplr.id "
    		+ "and pplr.sku_upc = %s ";
    
    
    public static final String OVERRIDE_PREP_SQL = ""
    		+ "SELECT pprlpo.prep_id "
    		+ "FROM   pofourwalls.po_rcpts_line_prep_ovrd pprlpo "
    		+ "WHERE  pprlpo.po_rcpts_line_ovrd_id IN (SELECT pprlo.id "
    		+ "                                        FROM "
    		+ "       pofourwalls.po_receipts ppr, "
    		+ "       pofourwalls.po_line_receipts pplr, "
    		+ "       pofourwalls.po_rcpts_line_ovrd "
    		+ "       pprlo "
    		+ "         WHERE  ppr.report_id = %s "
    		+ "                AND pplr.po_receipt_id = ppr.id "
    		+ "                AND pprlo.po_line_receipt_id = pplr.id"
    		+ "				   AND pplr.SKU_UPC = %s) ";
   
    
    public static final String OVERRIDE_PREPVALUE_SQL = ""
    		+ "SELECT pprlpo.prep_value "
    		+ "FROM   pofourwalls.po_rcpts_line_prep_ovrd pprlpo "
    		+ "WHERE  pprlpo.po_rcpts_line_ovrd_id IN (SELECT pprlo.id "
    		+ "                                        FROM "
    		+ "       pofourwalls.po_receipts ppr, "
    		+ "       pofourwalls.po_line_receipts pplr, "
    		+ "       pofourwalls.po_rcpts_line_ovrd "
    		+ "       pprlo "
    		+ "         WHERE  ppr.report_id = %s "
    		+ "                AND pplr.po_receipt_id = ppr.id "
    		+ "                AND pprlo.po_line_receipt_id = pplr.id"
    		+ "				   AND pplr.SKU_UPC = %s) ";


   public static final String SKU_OVERRIDE_SQL = ""
    		+ "SELECT pprlo.sku_ovrd_flg "
    		+ "FROM   pofourwalls.po_receipts ppr, "
    		+ "       pofourwalls.po_line_receipts pplr, "
    		+ "       pofourwalls.po_rcpts_line_ovrd pprlo "
    		+ "WHERE  ppr.report_id = %s "
    		+ "       AND pplr.po_receipt_id = ppr.id "
    		+ "       AND pprlo.po_line_receipt_id = pplr.id"
    		+ "		  AND pplr.SKU_UPC = %s";
   
   public static final String IS_SKU_OVERRIDDEN_SQL = ""
   		+ "SELECT count(*) "
   		+ "FROM   pofourwalls.po_receipts ppr, "
   		+ "       pofourwalls.po_line_receipts pplr, "
   		+ "       pofourwalls.po_rcpts_line_ovrd pprlo "
   		+ "WHERE  ppr.report_id = %s "
   		+ "       AND pplr.po_receipt_id = ppr.id "
   		+ "       AND pprlo.po_line_receipt_id = pplr.id"
   		+ "		  AND pplr.SKU_UPC = %s";
    
    public static final String PREPOVERRIDE_IDS_BY_REPORTID = ""
    		+ "SELECT id "
    		+ "FROM   pofourwalls.po_rcpts_line_prep_ovrd "
    		+ "WHERE  po_rcpts_line_ovrd_id IN (SELECT id "
    		+ "                                 FROM   pofourwalls.po_rcpts_line_ovrd "
    		+ "                                 WHERE  po_line_receipt_id IN (SELECT id "
    		+ "                                                               FROM "
    		+ "                                        pofourwalls.po_line_receipts "
    		+ "                                                               WHERE "
    		+ "                                        po_receipt_id IN (SELECT id "
    		+ "                                                          FROM "
    		+ "                                        pofourwalls.po_receipts "
    		+ "                                                          WHERE  report_id = %s "
    		+ "                                                         )) "
    		+ "                                );";


    
    public static final String OVERRIDE_IDS_BY_REPORTID =  ""
    		+ "SELECT id "
    		+ "FROM   pofourwalls.po_rcpts_line_ovrd "
    		+ "WHERE  po_line_receipt_id IN (SELECT id "
    		+ "                              FROM   pofourwalls.po_line_receipts "
    		+ "                              WHERE  po_receipt_id IN (SELECT id "
    		+ "                                                       FROM "
    		+ "                                     po_receipts "
    		+ "                                                       WHERE  report_id = %s ))";


    
    
	public static final String OVERRIDE_PREP_DELETE_REPORTID_SQL = "delete from pofourwalls.po_rcpts_line_prep_ovrd where ID in (%s)";
	public static final String OVERRIDE_DELETE_REPORTID_SQL = "delete from pofourwalls.po_rcpts_line_ovrd where ID in (%s)";
}
