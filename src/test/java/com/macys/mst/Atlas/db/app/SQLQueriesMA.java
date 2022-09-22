package com.macys.mst.Atlas.db.app;

/**
 *         This interface is used to store all the SQL queries
 */
public interface SQLQueriesMA {


	/** Select statements */

	public final String LOCN_BRCD_PICK_SEQ = "SELECT LOCN_BRCD FROM LOCN_HDR WHERE LOCN_PICK_SEQ IS NOT NULL "
			+ " AND LOCN_BRCD IN(%s) ORDER BY LOCN_PICK_SEQ";

	public final String GET_LOCN_BRCD_BY_PICK_SEQ = "SELECT  LOCN_BRCD FROM ( SELECT  * FROM LOCN_HDR sample(.1) WHERE LOCN_PICK_SEQ "
			+ " IS NOT NULL ORDER BY dbms_random.value) WHERE rownum <= %s ORDER BY LOCN_PICK_SEQ";

	public final String LPN_LCN_ITEM_PICK_SEQ =  "SELECT  tc_lpn_id, LOCN_BRCD, WORK_GRP, WORK_AREA, LOCN_CLASS, item_name, LOCN_PICK_SEQ FROM  ( select l.tc_lpn_id, lh.LOCN_BRCD, lh.WORK_GRP, lh.WORK_AREA, lh.LOCN_CLASS, l.item_name, lh.LOCN_PICK_SEQ "
			+ " from lpn  l inner join locn_hdr lh on l.DEST_SUB_LOCN_ID = lh.LOCN_ID where inbound_outbound_indicator='I' "
			+ " and  l.item_name is NOT NULL order by dbms_random.value) WHERE rownum <= %s order by LOCN_PICK_SEQ";

	public final String LPN_LCN_ITEM_PICK_SEQ_BY_PARENT = "select l.tc_lpn_id, lh.LOCN_BRCD, lh.WORK_GRP, lh.WORK_AREA, lh.LOCN_CLASS, l.item_name, "
			+ " lh.LOCN_PICK_SEQ from lpn  l inner join locn_hdr lh on l.DEST_SUB_LOCN_ID = lh.LOCN_ID where inbound_outbound_indicator='I' "
			+ " and  l.item_name is NOT NULL "
			+ " and l.TC_LPN_ID in (%s) order by LOCN_PICK_SEQ";

	public final String LPN_Case_lCN = "select lh.LOCN_BRCD, lpn.Last_UPDATED_DTTM, lh.WORK_AREA,lh.WORK_GRP, lpn.TC_LPN_ID,lpn.item_name\n" +
			"\t\t\tfrom locn_hdr lh join lpn lpn on lpn.CURR_SUB_LOCN_ID = lh.locn_id \n" +
			"      join wm_inventory wm on wm.tc_lpn_id = lpn.tc_lpn_id\n" +
			"\t\t\twhere lh.locn_class = 'R' and lh.work_GRP like 'BTRA' and lpn.lpn_type = '1' and lpn.lpn_facility_status < '90'\n" +
			"      and lpn.lpn_facility_status not in ('65','10') and lpn.tc_parent_lpn_id is not null and rownum <= '2' order by dbms_random.value";

	public final String ALT_LCN = "select LOCN_HDR.LOCN_BRCD, LOCN_HDR.LOCN_ID ,LOCN_HDR.LOCN_PICK_SEQ,"
			+"PICK_LOCN_HDR.MAX_VOL from LOCN_HDR inner join PICK_LOCN_HDR on PICK_LOCN_HDR.LOCN_ID = LOCN_HDR.LOCN_ID "
			+"where LOCN_HDR.SKU_DEDCTN_TYPE = 'T' and LOCN_HDR.AREA = '%s' and LOCN_HDR.ZONE ='%s' and LOCN_HDR.LOCN_ID not in "
			+"(select PICK_LOCN_DTL.LOCN_ID from PICK_LOCN_DTL) And PICK_LOCN_HDR.MAX_VOL  > "
			+"(select item_cbo.unit_volume from item_cbo where item_name = '%s')";

	public final String LPN_DTLS_PREP = "select l.TC_LPN_ID,l.LPN_ID, ic.ITEM_NAME,l.TC_ASN_ID,l.MANIFEST_NBR, l.SINGLE_LINE_LPN, wmi.item_ID,iw.PROD_LINE " +
			" from lpn l inner join lpn_detail ld on ld.LPN_ID = l.lpn_id inner join WM_INVENTORY wmi on wmi.tc_lpn_id = l.tc_lpn_id and wmi.item_id = ld.item_id " +
			" inner join ITEM_WMS iw on iw.item_id = ld.item_id and iw.item_Id = wmi.item_id " +
			"  inner join ITEM_CBO ic on ic.item_id = ld.item_id and ic.item_Id = wmi.item_id and iw.PROD_LINE = '%s' and  l.SINGLE_LINE_LPN = 'N' and " +
			"  l.TC_LPN_ID  like '%s' and rownum < 50";

	public final String PARENT_LPN_DTLS = null;
	public final String getPackageDetailInfo = "Select $colname from m_package_dtl where m_package_id in(select m_package_id from m_package where PACKAGE_NUMBER='#PACKAGENUMBER#')";
	public final String getPackageInfo = "select $colname from m_package where Package_number='#PACKAGENUMBER#'";
	public final String getPackedQty="select $colname from m_shipment_dtl where M_SHIPMENT_DTL_ID IN (select M_SHIPMENT_DTL_ID from m_package_dtl where m_package_id in(select m_package_id from m_package where package_number='#PACKAGENUMBER#'))";

	
	String UPDATEMPACKAGEACTUALWEIGHT="update m_package set actual_weight='1.2' where package_number='#PACKAGE_NUMBER#'";
    String UPDATEMPACKAGESHIPDTLACTUALWEIGHT="update M_PACKAGE_SHIP_DTL set actual_weight='1.2' where package_number='#PACKAGE_NUMBER#' and CREATED_SOURCE='Routing:PACKING'";
    
    
		    interface Talos{
		    	String GET_SHIPMENT_BY_STATUS="Select SHIPMENT_NUMBER from m_shipment where status = '#STATUS#' and rownum=1";
		    	String GET_PACKAGE_BY_STATUS="Select * from (Select PACKAGE_NUMBER from m_package where status = '#STATUS#' ORDER By DATE_UPDATED DESC) where rownum=1";
		    	String GET_PACKAGE_BY_STATUS_SHIP_VIA_NOT_NULL="Select * from (select p.package_number as PACKAGE_NUMBER from m_package p, m_shipment s where p.shipment_number = s.shipment_number and p.SHIP_VIA IS NOT NULL and p.SHIP_VIA NOT IN ('CALL','INTL') and p.status = #STATUS# and s.status = 150  ORDER By p.DATE_UPDATED DESC) where rownum=1";
		    	String GET_UPC_EVENT_BY_USERNAME = "SELECT * FROM (SELECT * FROM m_wo_opportunity_upc_event WHERE user_id = '#USER_ID#' ORDER BY updated_dttm DESC ) WHERE ROWNUM = 1";
		    	String GET_UPC_DETAILS_BY_OPPORTUNITY_UPC_EVENT_ID = "SELECT UPC_NAME FROM m_wo_opportunity_upc_detail WHERE m_wo_opportunity_upc_event_id = '#M_WO_OPPORTUNITY_UPC_EVENT_ID#' ORDER BY m_wo_opportunity_upc_detail_id DESC"; 
		    	String GET_WORK_BY_STATUS="Select M_WORK_HDR_ID from m_work_hdr where status = '#STATUS#' and rownum=1";
		    	String GETRANDOMPACKAGE="SELECT * from(select p.WORK_BATCH_NBR as WORK_BATCH_NBR, p.PACKAGE_NUMBER as PACKAGE_NUMBER "+
		    							"from m_package p JOIN m_package_dtl pd ON p.m_package_id = pd.m_package_id WHERE pd.status IS NOT NULL "+
		    							"GROUP BY p.WORK_BATCH_NBR,p.PACKAGE_NUMBER,p.DATE_CREATED,pd.m_package_id HAVING COUNT(pd.m_package_id) <2 )  where ROWNUM=1";
		    	String GETPACKAGESTATUS="SELECT DESCRIPTION from m_status where object_type='PACKAGE' AND STATUS IN (SELECT STATUS FROM M_PACKAGE where WORK_BATCH_NBR='#workbatchnumber#')";
		    	String GETTOTEBASEDONSTATUS="select * from (select * from m_tote where status='#STATUS#') where rownum=1";
		    	String GETTOTEBASEDONWORKBATCHNO="select * from(select * from m_tote where work_batch_number='#WORK_BATCH_NUMBER#' order by date_created desc) where rownum=1";
		    	String GETMTOTEDTLBYTOTEID="select * from(select ic.ITEM_NAME,md.QUANTITY from m_tote_dtl md inner join item_cbo ic on md.ITEM_ID= ic.item_id where m_tote_id='#M_TOTE_ID#') where rownum=1";
		    	String GETPACKAGEFORFINDCHUTE="select * from(select mp.package_number,mp.chute,mp.work_batch_nbr,mp.total_quantity,mpd.initial_quantity,mpd.item_barcode from m_package mp inner join m_package_dtl mpd"
		    			+ " on mp.m_package_id=mpd.m_package_id where mp.PATROLLED IS NULL and mp.status='12' and mp.chute IS NOT NULL and mp.chute_type='BEU' and mp.chute like 'CH%' and mpd.item_id in(select item_id from item_wms where prod_line <> 'SEC')"
		    			+ " order by mp.date_created desc) where rownum=1";
		    	String GETMPACKAGEDETAILS = "SELECT mp.PACKAGE_NUMBER,mp.SHIPMENT_NUMBER,mp.WORK_BATCH_NBR,mp.LOCATION_BARCODE,mpd.ITEM_BARCODE,mpd.INITIAL_QUANTITY,mpd.SORTED_QUANTITY,mpd.QUANTITY FROM M_PACKAGE_DTL mpd, M_PACKAGE mp WHERE mpd.M_PACKAGE_ID in ( SELECT M_PACKAGE_ID FROM M_PACKAGE WHERE PACKAGE_NUMBER = '#PACKAGENUMBER#') AND mpd.M_PACKAGE_ID = mp.M_PACKAGE_ID";
		    	String GETTOTEDETAILSBASEDONSTATUS = "SELECT * from(select td.M_TOTE_ID as M_TOTE_ID, t.TOTE_NUMBER as TOTE_NUMBER, t.WORK_BATCH_NUMBER as WORK_BATCH_NUMBER "+
						"from m_tote t JOIN m_tote_dtl td "+
						"ON t.M_TOTE_ID = td.M_TOTE_ID "+
						"WHERE t.status = '#STATUS#' "+
						"GROUP BY td.M_TOTE_ID, t.TOTE_NUMBER, t.WORK_BATCH_NUMBER ,t.DATE_CREATED "+
						"HAVING COUNT(td.M_TOTE_ID) <2 ORDER BY t.DATE_CREATED DESC) where ROWNUM=1";
		    	String GETTOTEBASEDONWORKBATCHNOANDTOTE="select * from(select * from m_tote where work_batch_number='#WORK_BATCH_NUMBER#' and tote_number='#TOTENUMBER#' order by date_created desc) where rownum=1";
		    	String GETTOTEBASEDONSTATUSANDWORKBATCHSTATUS = " select * from(select * from M_TOTE where status = '#TOTESTATUS#' and WORK_BATCH_NUMBER IN(select WORK_BATCH_NUMBER from M_WORK_BATCH where status ='#WORKBATCHSTATUS#') order by DATE_UPDATED desc) where ROWNUM = 1";
		    	String GET_TOTE_NUMBER = "select TOTE_NUMBER from m_tote where WORK_BATCH_NUMBER = '#WAVE_NUMBER#'";
		    	String GET_LOCATION = "SELECT * FROM (  SELECT LOCN_BRCD  FROM LOCN_HDR WHERE WORK_GRP='#WORK_GRP#') WHERE ROWNUM=1";
		    }
		    
	interface Proteus {
		String GET_WAVE_PARM_STATUS_BY_WORK_PLAN = "SELECT Wave_Stat_Code FROM wave_parm WHERE Wave_Nbr = '#WAVE_NBR#'";
		String GET_C_MHE_STATUS_FROM_PACK_WAVE_PARM_DTL = "select C_MHE_STATUS from pack_wave_parm_dtl where PACK_WAVE_NBR = #PACK_WAVE_NBR#";
		String GET_M_PACKAGE_DETAILS = "select MP.PACKAGE_NUMBER, MP.TOTAL_QUANTITY, MP.CHUTE, MPDTL.ITEM_ID from M_PACKAGE MP, M_PACKAGE_DTL MPDTL where MP.M_PACKAGE_ID = MPDTL.M_PACKAGE_ID and WORK_BATCH_NBR = #PACK_WAVE_NBR#";
		String GET_BATCH_NUMBER_FROM_WAVE_RECOMMEND_BATCH = "select MAX(BATCH_NUMBER) as BATCH_NUMBER from M_WAVE_RECOMMEND_BATCH";
		String GET_STAT_CODE_FROM_WAVE_RECOMMEND_BATCH = "select STAT_CODE from M_WAVE_RECOMMEND_BATCH where BATCH_NUMBER = '#BATCH_NUMBER#'";

		String GET_WAVE_DASHBOARD_DRILLDOWNS = "select oli.ship_wave_nbr, pwd.c_mhe_status, sum (oli.order_qty-nvl(oli.user_canceled_qty,0)) as order_qty " + 
				"from order_line_item oli, pack_wave_parm_dtl pwd, pack_wave_parm_hdr pwh, orders o " + 
				"where oli.order_id = o.order_id " + 
				"and pwd.pack_wave_parm_id = pwh.pack_wave_parm_id " + 
				"and oli.ship_wave_nbr = substr (pwd.pack_wave_nbr,1,12) " + 
				"and pwh.chute_assign_type ='#CHUTE_ASSIGN_TYPE#' " + 
				"and oli.do_dtl_status >=130 and oli.do_dtl_status <=170 " + 
				"and o.pickup_start_dttm < = sysdate " + 
				"and c_mhe_status < 90 " + 
				"group by oli.ship_wave_nbr, pwd.c_mhe_status " + 
				"order by oli.ship_wave_nbr";
		
	}
		    
	public final String getWorkDtlInfo = "select FROM_LOCATION_ID as LOCN_ID , ITEM_ID from M_WORK_DTL where work_batch_number = '#WORKBATCHNUMBER#'";
	String UPDATETOBEFILLEDQTY = "update wm_inventory set TO_BE_FILLED_QTY='$qty' where location_id='#LOCATIONID#'  and item_id='#ITEMID#'";
	
	public final String getConfigForLogicalGrp = "select dtl.CONFIG_ROW_ID from m_generic_config hdr,m_generic_config_dtl dtl "
			+"where hdr.config_code = '${genConfig}' and hdr.config_id = dtl.config_id " 
			+"and hdr.column_name= '${colname1}' and dtl.config_value = '${overrideValue}' and dtl.CONFIG_ROW_ID IN"
			+ "(select dtl.CONFIG_ROW_ID from m_generic_config hdr,m_generic_config_dtl dtl "
			+"where hdr.config_code = '${genConfig}' and hdr.config_id = dtl.config_id "
			+"and hdr.column_name= '${colname2}' and dtl.config_value = '${ruleValue}')";
	public final String getPackWavesForLogicalGroup = "select $colname from M_RELEASE_PACK_WAVES where LOGICAL_GROUP='#LOGICALGROUP#' and PACK_WAVE_NBR='#PACKWAVENBR#' order by RELEASE_WAVE_DTTM desc";
	String GET_M_GENERIC_CONFIG_DETAILS = "select COLUMN_NAME, CONFIG_ID from M_GENERIC_CONFIG where CONFIG_CODE = '#CONFIG_CODE#' and (COLUMN_NAME = '#COLUMN_NAME#' OR COLUMN_NAME = 'CHUTE_TYPE_GRP')";
	
	String GET_ROW_ID_FROM_GENERIC_CONFIG_DETAILS = "select CONFIG_ROW_ID from M_Generic_Config_Dtl where CONFIG_ID = #CHUTE_TYPE_GRP_CONFIG_ID# AND CONFIG_VALUE = '#ROW_NAME#'";
	
	String UPDATE_M_GENERIC_CONFIG_DETAILS = "UPDATE M_Generic_Config_Dtl SET CONFIG_VALUE = '#CONFIG_VALUE#' where CONFIG_ID = '#COLUMN_ID#' AND CONFIG_ROW_ID = '#ROW_NUMBER#'";
	
	String GET_SCAN_TOTE_FROM_EVENT_DATA = "select BUSINESS_ID from M_EVENT_DATA where CORRELATION_ID='#WAVE_NUMBER#' and EVENT_NAME = 'Staged'";
	
	String GET_WP_ACTIVATE_CLOSE_HSTRY = "select * from m_wp_activate_close_hstry where action_type='ACTIVATE' and user_id='PROTEUS' and wave_number = '#WAVE_NUMBER#'";
	
	public String update_Package_Status_11 = "update m_package ab set ab.status='0' where ab.package_Number in (select package_number packageNumber from m_package pkg  inner join Locn_hdr lh on lh.locn_id = pkg.location_id "
    		+ "inner join m_work_batch mwb on mwb.work_batch_number = pkg.work_batch_nbr inner join m_status ms on ms.status = pkg.status "   
    		+ "where lh.work_grp = '#WORKGRP#' and pkg.status in('11') and mwb.status <= 90 "  
    		+ "and ms.object_type='PACKAGE' and pkg.package_number <>'#PACKAGE_NUMBER#')";
	public String update_Package_Status_12 = "update m_package ab set ab.status='10' where ab.package_Number in (select package_number packageNumber from m_package pkg  inner join Locn_hdr lh on lh.locn_id = pkg.location_id "
    		    		+ "inner join m_work_batch mwb on mwb.work_batch_number = pkg.work_batch_nbr inner join m_status ms on ms.status = pkg.status "   
    		    		+ "where lh.work_grp = '#WORKGRP#' and pkg.status in('12') and mwb.status <= 90 "  
    		    		+ "and ms.object_type='PACKAGE' and pkg.package_number <>'#PACKAGE_NUMBER#')";
	public String restore_Package_Status_11 = "update m_package ab set ab.status='11' where ab.package_Number in (select package_number packageNumber from m_package pkg  inner join Locn_hdr lh on lh.locn_id = pkg.location_id "
    		+ "inner join m_work_batch mwb on mwb.work_batch_number = pkg.work_batch_nbr inner join m_status ms on ms.status = pkg.status "   
    		+ "where lh.work_grp = '#WORKGRP#' and pkg.status in('0') and mwb.status <= 90 "  
    		+ "and ms.object_type='PACKAGE' and pkg.package_number <>'#PACKAGE_NUMBER#')";
	public String restore_Package_Status_12 = "update m_package ab set ab.status='12' where ab.package_Number in (select package_number packageNumber from m_package pkg  inner join Locn_hdr lh on lh.locn_id = pkg.location_id "
    		   		+ "inner join m_work_batch mwb on mwb.work_batch_number = pkg.work_batch_nbr inner join m_status ms on ms.status = pkg.status "   
    		   		+ "where lh.work_grp = '#WORKGRP#' and pkg.status in('10') and mwb.status <= 90 "  
    		   		+ "and ms.object_type='PACKAGE' and pkg.package_number <>'#PACKAGE_NUMBER#')";

	public String getOsmOrder="SELECT * FROM SHP_HDR WHERE RES_NBR='#RES_NBR#'";
	public String getMstOrder="SELECT * FROM ORDERS WHERE TC_ORDER_ID='#TC_ORDER_ID#'";
	
	//Added For HAF
	public String UPDATE_TRAN_TYPE="UPDATE SHP_HDR SET TRAN_TYPE='#TRAN_TYPE#' Where FULLFILLMENT_NBR='#FULLFILLMENT_NBR#'";
	public String UPDATE_RTE_ATTR="update orders set RTE_ATTR='#RTE_ATTR#' where tc_order_id='#tc_order_id#'";
	public String UPDATE_ROUTE_ATTRIBUTE_1="update m_shipment set ROUTE_ATTRIBUTE_1='#routeAttribute#' where shipment_number='#SHIPMENT_NUMBER#'";
	public String UPDATE_ROUTE_ATTRIBUTE_2="update m_shipment set ROUTE_ATTRIBUTE_2='#routeAttribute#' where shipment_number='#SHIPMENT_NUMBER#'";
	public String GET_SHIPMENT_VALUES="select * from m_shipment where shipment_number like '#FULLFILLMENT_NBR#%'";
	public String UPDATE_BUYER_CODE="update orders set BUYER_CODE='#BUYER_CODE#' where tc_order_id='#tc_order_id#'";
	public String GET_OSM_ORDER_BY_FULLFILLMENT_NBR="SELECT * FROM SHP_HDR WHERE FULLFILLMENT_NBR='#FULLFILLMENT_NBR#'";
	public String GET_SHP_LI_DETAILS="SELECT * FROM SHP_LI WHERE FULLFILLMENT_NBR='#FULLFILLMENT_NBR#'";
	
	public String UPDATE_TABLES_COLUMN="UPDATE #table_name SET $columnName='#columnValue#' WHERE TC_ORDER_ID='#OrderNumber'";
	
	public String HAF_ActiveLocationsCSR="select distinct SUBSTR(lcn_brcd,1,4) from(  SELECT distinct locn_brcd lcn_brcd from locn_hdr lhr LEFT OUTER JOIN pick_locn_dtl pdtl ON lhr.LOCN_ID = pdtl.LOCN_ID  WHERE lhr.WORK_GRP in('AML1','AML2','KAM2') AND lhr.SKU_DEDCTN_TYPE='P' AND NOT EXISTS (select 1 from pick_locn_dtl pdtl where lhr.LOCN_ID = pdtl.LOCN_ID and ltst_sku_assign = 'Y' and trig_repl_for_sku = 'Y')  AND EXISTS (select 1 from pick_locn_hdr plh where lhr.locn_id=plh.locn_id and repl_flag = 'Y') ) where SUBSTR(lcn_brcd,1,4)<>'EA33' group by SUBSTR(lcn_brcd,1,4) having count(lcn_brcd) > 15";
	
	public String UPDATE_ORDER_STATUS="update orders set do_status='200' where tc_order_id in (select tc_order_id from orders where tc_order_id in (select tc_order_id from c_order_spl_instr where SPL_INSTR_CODE_9 in (#orderCategory)) and do_status='110' and TC_ORDER_ID like '#orderPrefix%' and PICKUP_END_DTTM='#pickUpDate')";
	
	public String UPDATE_ORDER_CATEGORY="update c_order_spl_instr set SPL_INSTR_CODE_9='#category' where TC_ORDER_ID='#orderID'";
}