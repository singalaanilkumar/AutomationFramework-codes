package com.macys.mst.WMSLite.EndToEnd.db.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface Constants                
{

	/**
	 * All Queries used in this project as constants
	 *
	 */

	interface Select 
	{
		
		
		/**
		 *  All Select Queries
		 */
		/** Wave Rule Queries */
		public static final String RULE_CMPAR_VALUE = "select RULE_CMPAR_VALUE from rule_sel_dtl where colm_name = 'TC_ORDER_ID' and "
				+ " rule_id in (select RULE_ID from wave_rule_parm where wave_rule_parm_id = ${waveRuleParmId})";
		public static final String RULE_CMPAR_VALUE1 = "select RULE_CMPAR_VALUE from rule_sel_dtl where colm_name = 'PROD_LINE' and "
				+ " rule_id in (select RULE_ID from wave_rule_parm where wave_rule_parm_id = ${waveRuleParmId})";
		public static final String MAX_RULE_ID = "select max(rule_id) from rule_hdr order by rule_id desc";
		public static final String MAX_RULE_SEL_DTL_ID = "select max(rule_sel_dtl_id) from rule_sel_dtl order by rule_sel_dtl_id desc";
		public static final String MAX_RULE_SORT_DTL_ID = "select max(rule_sort_dtl_id) from rule_sort_dtl order by rule_sort_dtl_id desc";
		public static final String WAVE_PARM_ID_WAVETYPE = "select ship_wave_parm_id from SHIP_WAVE_PARM where WAVE_DESC =  ${waveDesc} and rec_type = 'T'";
		public static final String MAX_WAVE_RULE_PARM_ID = "select max(WAVE_RULE_PARM_ID) from wave_rule_parm order by WAVE_RULE_PARM_ID desc";
		public static final String WAVE_RULE_PARM_ID = "select wave_rule_parm_id from wave_rule_parm  where ship_wave_parm_id in "
				+ " (select ship_wave_parm_id from SHIP_WAVE_PARM where WAVE_DESC = ${waveDesc} and rec_type ='T') and rule_name = ${ruleName}";
	
		/**Queries for story 1325*/
		
		public static final String Task_By_Area= "select count(*)from task_hdr where stat_code = 10 and invn_neeD_type = 50 and begin_area in (${A%}) and task_genrtn_ref_code = 44 group by begin_area order by begin_area";
		
		public static final String Staged_units = "SELECT  SUM(QTY_ALLOC-QTY_PULLD) AS STAGED_UNITS FROM  TASK_DTL TD INNER JOIN  PACK_WAVE_PARM_DTL PWPD "
				+ " ON  TD.TASK_GENRTN_REF_NBR=PWPD.PACK_WAVE_NBR INNER JOIN  PACK_WAVE_PARM_HDR PWPH ON PWPH.PACK_WAVE_PARM_HDR_ID=PWPD.PACK_WAVE_PARM_HDR_ID  "
				+ " WHERE  INVN_NEED_TYPE=52 AND  TASK_GENRTN_REF_CODE=44 AND  TD.STAT_CODE<90 AND CHUTE_ASSIGN_TYPE in (${chuteAssignType}) GROUP BY PWPH.CHUTE_ASSIGN_TYPE ORDER BY PWPH.CHUTE_ASSIGN_TYPE";
		
		public static final String stagedWaves = "select count(pack_wave_nbr) from pack_wave_parm_dtl where c_mhe_status >= 15 and c_mhe_status < = 20 "
				+ " and pack_wave_parm_id in (select pack_wave_parm_id from pack_wave_parm_hdr where chute_assign_type in ('BE1','BE2','NST'))";
		
		public static final String maxConfidRowID = "select max(config_row_id) from M_Generic_Config_Dtl where config_id in  "
				+ "(SELECT CONFIG_ID FROM M_GENERIC_CONFIG WHERE Config_Code= ${genConfigCode})";
		
		public static final String Confid_Row_ID = "select dtl.CONFIG_ROW_ID from m_generic_config hdr,m_generic_config_dtl dtl "
				+ "where hdr.config_code = ${genConfigCode} and hdr.config_id = dtl.config_id  and hdr.column_name= ${colName} and dtl.config_value= ${configValue} order by dtl.config_row_id asc";
				
		public static final String Config_id_CONSTRAINT_TYPE = "select config_id from m_generic_config  where CONFIG_CODE= ${genConfigCode} AND COLUMN_NAME = ${CONSTRAINT_TYPE}";
		
		public static final String Config_value_CONSTRAINT_TYPE = "select CONFIG_VALUE from m_generic_config_dtl  where config_id= ${Config_id_CONSTRAINT_TYPE} and config_row_id = ${Confid_Row_ID}";
		
		public static final String latestPickWaveNumber = "select * from (select wave_nbr from wave_rule_parm where rule_name = ${ruleName} "
				+ "  order by create_date_time desc) where  rownum=1";
		
		public static final String pickWaveStatus= "select wave_stat_code from wave_parm where  wave_nbr = ${waveNumber}  ";
		
		public static final String M_RELEASE_PACK_WAVES= "SELECT USER_ID from M_RELEASE_PACK_WAVES where pack_wave_nbr in (${packWaveNbr})";
		
		public static final String packWaveStatus = "select c_mhe_status from pack_wave_parm_dtl  where pack_wave_nbr in (${packWaveNbr})";
		
		public static final String Allocations = "SELECT STAT_CODE FROM ALLOC_INVN_DTL WHERE INVN_NEED_TYPE = 50  AND TASK_GENRTN_REF_NBR in (${pack_wave_nbr})";
		
		public static final String Tasks = "select STAT_CODE from task_hdr where  invn_need_type='50' and begin_area='A' and task_genrtn_ref_nbr in (${pack_wave_nbr})";

		public static final String releaseAllDttmQry = "select * from (SELECT RELEASE_WAVE_DTTM from M_RELEASE_PACK_WAVES where pack_wave_nbr in (${packWaveNbr}) "
				+ " AND LOGICAL_GROUP LIKE '%ALL' order by RELEASE_WAVE_DTTM desc) where rownum=1 ";
		
		public static final String releaseNotAllDttmQry =  "SELECT * from (SELECT RELEASE_WAVE_DTTM from M_RELEASE_PACK_WAVES where pack_wave_nbr in (${packWaveNbr}) "
				+ " AND LOGICAL_GROUP NOT LIKE '%ALL' order by RELEASE_WAVE_DTTM desc ) where rownum=1";
		
		
		/**Queries for story 1575*/
		public static final String Get_CONFIG_Value= "select config_value from (select config_value from m_generic_config_dtl where config_row_id = ${configRowID} and "
				+ " config_id = (select config_id from m_generic_config where config_code = ${configCode} and column_name = ${colName})) where rownum=1";
		
		public static final String Get_TASKS_AREA= "select count(*) begin_area from task_hdr where invn_need_type = '1' and task_genrtn_ref_code = '1' "
					+ "and stat_code = '10' and begin_area = ${area}";
		
		public static final String Get_TASKS_CONFIG_VALUE= "select config_value from m_generic_config_dtl where config_id = (select config_id from m_generic_config "
				+ " where config_code = 'PROTEUS/REPLENISH/REPLENTASK' and column_name = 'MIN_TASKS_RELEASED_PER_REPLENISHER')"; 
		
		public static final String Replen_Batch_number_old= "Select TASK_GENRTN_REF_NBR FROM ALLOC_INVN_DTL a, m_rplns_rcmnd_batch b "
				+ "WHERE a.TASK_GENRTN_REF_NBR=b.BATCH_NUMBER and b.ORDER_SELECTION_RULE= ${orderSelRule} "
				+ "and item_id= (select item_id from item_master a where sku_brcd=${itemName})order by CREATE_DATE_TIME desc";
		
		
		public static final String Replen_Batch_number= "Select TASK_GENRTN_REF_NBR FROM ALLOC_INVN_DTL a, m_rplns_rcmnd_batch b "
				+ "WHERE a.TASK_GENRTN_REF_NBR=b.BATCH_NUMBER  "
				+ "and item_id= (select item_id from item_master a where sku_brcd=${itemName})order by CREATE_DATE_TIME desc"; /* and b.ORDER_SELECTION_RULE= ${orderSelRule}*/
		
		public static final String Replen_Batch_number_create_date_time= "SELECT * from (Select  a.CREATE_DATE_TIME FROM ALLOC_INVN_DTL a, m_rplns_rcmnd_batch b "
	
				+ " WHERE a.TASK_GENRTN_REF_NBR=b.BATCH_NUMBER and b.ORDER_SELECTION_RULE= ${orderSelRule} order by CREATE_DATE_TIME desc) where rownum=1";
		
		public static final String statCode_Tasks = "select stat_code from alloc_invn_dtl where  TASK_GENRTN_REF_NBR= ${batchNumber} order by create_date_time desc";
		
		public static final String statCode_Allocations_old = "Select  a.stat_code FROM ALLOC_INVN_DTL a, m_rplns_rcmnd_batch b "
				+ "WHERE a.TASK_GENRTN_REF_NBR=b.BATCH_NUMBER and b.ORDER_SELECTION_RULE= ${orderSelRule} and item_id= (select item_id from item_master a where sku_brcd=${itemName})"
				+ " order by CREATE_DATE_TIME desc" ;	
		
		
		public static final String statCode_Allocations = "Select  a.stat_code FROM ALLOC_INVN_DTL a, m_rplns_rcmnd_batch b "
				+ "WHERE a.TASK_GENRTN_REF_NBR=b.BATCH_NUMBER  and item_id= (select item_id from item_master a where sku_brcd=${itemName})"
				+ " order by CREATE_DATE_TIME desc" ;	/*and b.ORDER_SELECTION_RULE= ${orderSelRule}*/
		
		
		public static final String order_Details="SELECT O.TC_ORDER_ID as TC_ORDER_ID,O.ORDER_ID as ORDER_ID,O.DO_STATUS as ORDER_STATUS,O.ORDER_TYPE AS ORDER_TYPE,O.RTE_TO as SHIPPING_METHOD,O.ORIG_BUDG_COST AS TOTAL_ORDER_QTY,O.PICKUP_START_DTTM AS PICK_UP_START_DTTM , O.CREATED_DTTM AS CREATED_DTTM,O.MERCH_CODE AS MECH_CODE,CSPL1.SPL_INSTR_CODE_7 AS CHUTE_TYPE, CSPL1.SPL_INSTR_CODE_9 AS ORDER_CATEGORY ,OIL.TC_ORDER_LINE_ID AS TC_ORDER_LINE_ID,OIL.LINE_ITEM_ID AS LINE_ITEM_ID, OIL.ORDER_QTY AS ORDER_QTY,OIL.DO_DTL_STATUS as ORDER_LINE_ITEM_STATUS,OIL.CHUTE_ASSIGN_TYPE AS CHUTE_ASSIGN_TYPE,OIL.LPN_BRK_ATTRIB AS LPN_BRK_ATTRIB,OIL.USER_CANCELED_QTY AS USER_CANCELED_QTY ,OIL.WAVE_NBR AS WAVE_NBR, IW.PROD_LINE AS STORAGE_TYPE,IW.ITEM_ID AS ITEM_ID,OWT.WORK_TYPE AS WORK_TYPE,OWT.SAM AS FRAGILE,OWT.NBR_OF_UNITS AS GIFT,OWT.NBR_OF_PIKS AS OVERSIZE,OWT.NBR_OF_LPNS AS SORTABLE, CSPL1.SPL_INSTR_CODE_5 AS GIFT_WRAP, CSPL1.SPL_INSTR_CODE_6 AS GIFT_CARD, CSPL1.SPL_INSTR_CODE_10 AS UPS_SHIP_ZONE, O.EST_PALLET_BRIDGED AS ORDER_VOLUME, O.LAST_UPDATED_DTTM AS SRC_UPDATE_TS, O.DC_CTR_NBR AS DC_CTR_NBR, O.CANCEL_DTTM AS CANCEL_DTTM, O.ORDER_DATE_DTTM AS ORDER_DATE_DTTM, O.DELIVERY_START_DTTM AS DELIVERY_START_DTTM, O.IS_SATURDAY_DELIVERY AS IS_SATURDAY_DELIVERY, O.IS_SATURDAY_PICKUP AS IS_SATURDAY_PICKUP, O.PRIORITY_TYPE AS PRIORITY_TYPE , O.PRIORITY AS PRIORITY, O.LINE_HAUL_SHIP_VIA, O.DSG_SHIP_VIA, O.SHPNG_CHRG, O.REF_FIELD_2, O.REF_FIELD_3, O.RTE_TYPE_1, O.RTE_TYPE_2, OIL.PROD_STAT, OIL.ORIG_ORDER_QTY,"+
				"CSPL1.SPL_INSTR_CODE_1, CSPL1.SPL_INSTR_CODE_2, CSPL1.SPL_INSTR_CODE_3, CSPL1.SPL_INSTR_CODE_4, CSPL1.SPL_INSTR_CODE_8, IFWMS.ALLOC_TYPE, IFWMS.MISC_ALPHA_1, ICBO.ITEM_BAR_CODE"+
				" FROM ORDERS O"+          
				" INNER JOIN C_ORDER_SPL_INSTR CSPL1 ON O.ORDER_ID = CSPL1.ORDER_ID"+
				" INNER JOIN ORDER_LINE_ITEM OIL ON O.ORDER_ID = OIL.ORDER_ID"+
				" INNER JOIN ITEM_WMS IW ON OIL.ITEM_ID = IW.ITEM_ID"+
				" INNER JOIN ITEM_CBO ICBO ON OIL.ITEM_ID = ICBO.ITEM_ID"+
				" INNER JOIN ITEM_FACILITY_MAPPING_WMS IFWMS ON OIL.ITEM_ID = IFWMS.ITEM_ID"+
				" LEFT OUTER JOIN ORDER_WORK_TYPE OWT ON OIL.ORDER_ID = OWT.ORDER_ID AND OIL.LINE_ITEM_ID = OWT.LINE_ITEM_ID"+
				" where o.tc_order_id='${tcOrderId}'";
				
		public static final String maxRecommendBatchId="select max(M_WAVE_RECOMMEND_BATCH_ID) from m_wave_recommend_batch";
		public static final String maxBatchNo="select max(BATCH_NUMBER) from m_wave_recommend_batch";
		public static final String sysCode="select code_id from whse_sys_code where rec_type='C' and code_type='PCK' and code_id=${chuteType}";
		public static final String maxSysCodeId="select max(whse_sys_code_id) from whse_sys_code";
		public static final String pickWaveNumber="select rte_wave_nbr from orders where tc_order_id=${order_id}";
		public static final String packWaveReleasedStatus="select stat_code from alloc_invn_dtl where task_genrtn_ref_nbr=${packWaveNumber}";
		public static final String maxOpenWaves="select count(*) as count from ship_wave_parm where wave_desc=${waveType} and c_mhe_status=0";
		public static final String taskQuery="select MW.M_WORK_ID AS TASK_ID,MW.FROM_AREA AS PICK_AREA,MW.ACTIVITY_TYPE AS INVN_NEED_TYPE,MAD.QUANTITY AS QTY_ALLOC,MAD.COMPLETED_QUANTITY AS QTY_PULLD,MW.STATUS AS STAT_CODE,MW.WORK_BATCH_NUMBER AS TASK_GENRTN_REF_NBR FROM mstmb.M_WORK MW,MSTMB.M_ACTIVITY_DTL MAD WHERE MAD.WORK_BATCH_NUMBER=MW.WORK_BATCH_NUMBER AND   MAD.WORK_BATCH_NUMBER=${packWaveNumber}";
		public static final String rpwFlag="Select trim(MISC_FLAGS) from sys_code where code_type ='MST' and code_id ='RPW'";
				
		/** Order Creation queries*/
		public static final String getOrderID = "select ORDER_ID from orders where tc_order_id = '${tcOrderId}' ";
		public static final String getAllOrdersWithPrefix = "select TC_ORDER_ID,ORDER_ID from orders where tc_order_id like '${orderPrefix}%' order by created_dttm desc ";
		
		public static final String singleVdrOrderItemQuery  = "select item_name from (select ic.item_name from item_cbo ic "
				+ " INNER JOIN ITEM_FACILITY_MAPPING_WMS ifc  on ic.ITEM_ID= ifc.ITEM_ID "
				+ "INNER JOIN item_wms iwms on ifc.ITEM_ID= iwms.ITEM_ID where ifc.MISC_ALPHA_3 = '${prodStat}' "
				+ " and ifc.MISC_ALPHA_1 like '${sngVdr}%' and not exists ( select 1 from wm_inventory wmi where iwms.ITEM_ID = wmi.ITEM_ID)) where rownum=1";
		 //iwms.prod_line = '${prodPrefix}' and
		public static final String orderItemQuery  = "select item_name from (select ic.item_name from item_cbo ic "
				+ " INNER JOIN ITEM_FACILITY_MAPPING_WMS ifc  on ic.ITEM_ID= ifc.ITEM_ID "
				+ "INNER JOIN item_wms iwms on ifc.ITEM_ID= iwms.ITEM_ID where iwms.prod_line = '${prodPrefix}' and ifc.MISC_ALPHA_3 = '${prodStat}' "
				+ "  and not exists ( select 1 from wm_inventory wmi where iwms.ITEM_ID = wmi.ITEM_ID)) where rownum=1";
	
		public static final String activeLocationCSR = "SELECT lhr.locn_id from locn_hdr lhr LEFT OUTER JOIN pick_locn_dtl pdtl ON lhr.LOCN_ID = pdtl.LOCN_ID "
				+ " WHERE lhr.WORK_GRP in('AML1','AML2') AND lhr.SKU_DEDCTN_TYPE='P' "
				+ " AND NOT EXISTS (select 1 from pick_locn_dtl pdtl where lhr.LOCN_ID = pdtl.LOCN_ID ) "
				+ " AND EXISTS (select 1 from pick_locn_hdr plh where lhr.locn_id=plh.locn_id)  and rownum=1";
		
		public static final String activeLocationGOH = "SELECT lhr.locn_id from locn_hdr lhr LEFT OUTER JOIN pick_locn_dtl pdtl ON lhr.LOCN_ID = pdtl.LOCN_ID "
				+ " WHERE lhr.LOCN_CLASS = 'A' AND lhr.AREA = 'F' AND lhr.work_grp like 'AML%' AND NOT EXISTS (select 1 from pick_locn_dtl pdtl where lhr.LOCN_ID = pdtl.LOCN_ID ) and rownum=1";
		
		public static final String activeLocationBTR = "SELECT lhr.locn_id from locn_hdr lhr LEFT OUTER JOIN pick_locn_dtl pdtl ON lhr.LOCN_ID = pdtl.LOCN_ID "
				+ " WHERE lhr.pick_detrm_zone not in ('1A','2A') AND lhr.PICK_DETRM_ZONE like 'B%' AND lhr.SKU_DEDCTN_TYPE='P' "
				+ " AND NOT EXISTS (select 1 from pick_locn_dtl pdtl where lhr.LOCN_ID = pdtl.LOCN_ID ) and rownum=1";
		
		public static final String activeLocationSEC = "SELECT lhr.locn_id from locn_hdr lhr LEFT OUTER JOIN pick_locn_dtl pdtl ON lhr.LOCN_ID = pdtl.LOCN_ID "
				+ " WHERE lhr.LOCN_CLASS = 'A' AND lhr.work_grp like 'AMLS%' "
				+ " AND NOT EXISTS (select 1 from pick_locn_dtl pdtl where lhr.LOCN_ID = pdtl.LOCN_ID ) and rownum=1";
		
		
		public static final String activeLocationHFG = "select locn_id from locn_hdr lhr where pick_detrm_zone in ('HOM') and locn_id not in (select locn_id from pick_locn_dtl) and rownum=1";
		public static final String activeLocationPLR = "select locn_id from locn_hdr lhr where locn_class='A' and locn_brcd like 'G%' and locn_id not in (select locn_id from pick_locn_dtl) and rownum=1";
		public static final String activeLocationSHO = "select locn_id from locn_hdr lhr where locn_class='A' and locn_brcd like 'C%' and locn_id not in (select locn_id from pick_locn_dtl) and rownum=1";
		public static final String activeLocationHOM = "select locn_id from locn_hdr lhr where locn_class='A' and locn_brcd like 'H%'  and locn_id not in (select locn_id from pick_locn_dtl) and rownum=1";
		public static final String reserveLocationCSR = "select locn_id from locn_hdr lhr where pull_zone in ('C01','C02') and  locn_id in (select locn_id from resv_locn_hdr where curr_uom_qty = '0') and rownum=1";
		public static final String reserveLocationBTR = "select locn_id from locn_hdr lhr where locn_class = 'R' and work_grp like 'BTR%' and rownum=1";
		public static final String reserveLocationPLR = "select locn_id from locn_hdr lhr where locn_class = 'R' and work_grp like 'BTR%' and rownum=1";
		public static final String activeLocationCHESIRE = "select locn_id from locn_hdr where LOCN_CLASS='A' and WORK_GRP like ${wrkGroup} and locn_brcd like ${area}  and locn_id not in (select locn_id from pick_locn_dtl) and rownum=1";
		public static final String reserveLocationCHESIRE = "select locn_id from locn_hdr lhr where locn_class = 'R' and work_grp like 'KBTR%' and rownum=1";
		//public static final String activeLocationCHESIRE = "select locn_id from locn_hdr where pick_detrm_zone in ('1A','2A') and locn_id not in (select locn_id from pick_locn_dtl)  and  SKU_DEDCTN_TYPE='P' and DSP_LOCN like ${area} and rownum=1";
		
		/** WA-2319 : Gen config updates*/
		public static final String getRuleName = "select config_value from( select config_value from m_generic_config_dtl where config_id = " 
					+" (select config_id from m_generic_config where config_code = 'PROTEUS/REPLENISH/ORDSELRULE' and column_name = 'ORD_SEL_RULE')) where rownum=1";

		public static final String getRowID = "select dtl.CONFIG_ROW_ID from m_generic_config hdr,m_generic_config_dtl dtl"
					+" where hdr.config_code = 'PROTEUS/REPLENISH/ORDSELRULE' and hdr.config_id = dtl.config_id "
					+" and hdr.column_name='ORD_SEL_RULE' and dtl.config_value= ${ruleName}";
		
		public static final String getRWFValue ="select config_value from m_generic_config_dtl where config_row_id=  ${rowID} "
					+" and config_id = (select config_id from m_generic_config where config_code = 'PROTEUS/REPLENISH/ORDSELRULE' and column_name = 'INCLUDE_RWF')";

		public static final String getmaxItems ="select config_value from m_generic_config_dtl where config_row_id=  ${rowID} "
				+" and config_id = (select config_id from m_generic_config where config_code = 'PROTEUS/REPLENISH/ORDSELRULE' and column_name = 'BATCH_SIZE')";

		/** Replin Batch Queries*/
		public static final String replinBatchListQry= "Select DISTINCT TASK_GENRTN_REF_NBR FROM ALLOC_INVN_DTL a, m_rplns_rcmnd_batch b "
				+ " WHERE a.TASK_GENRTN_REF_NBR=b.BATCH_NUMBER and b.ORDER_SELECTION_RULE= '${ruleName}' and b.TOTAL_NO_ITEM>0"
				+ " and b.CREATED_DTTM > (sysdate - (${timeInMins}/ 24/60))";
		
		/**Assigning Item to another Active location */
		public static final String secondActiveLocnCSRQry = "SELECT lhr.locn_id from locn_hdr lhr LEFT OUTER JOIN pick_locn_dtl pdtl ON lhr.LOCN_ID = pdtl.LOCN_ID"
				+ " WHERE lhr.pick_detrm_zone not in ('1A','2A') and lhr.locn_brcd like ${locnBrcd} "
				+ " AND NOT EXISTS (select 1 from pick_locn_dtl pdtl where lhr.LOCN_ID = pdtl.LOCN_ID ) and rownum=1";

		
		
		/**Picking dashboard quesries*/
		String toBereleasedUnitsQry = "select (pwph.chute_assign_type||'-'||lh.area||'-'||iw.prod_line) AS LOGICAL_GROUP , sum(aid.qty_alloc - aid.qty_pulld) "
				+ " from alloc_invn_dtl aid, locn_hdr lh, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd "
				+ " where aid.pull_locn_id = lh.locn_id and aid.invn_need_type = 50 "
				+ " and pwpd.pack_wave_nbr = aid.task_genrtn_ref_nbr "
				+ " and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_hdr_id "
				+ " and aid.item_id = iw.item_id and aid.stat_code <90 and aid.task_genrtn_ref_code = '44' "
				+ "and pwpd.c_mhe_status < 30 "
				+ " group by pwph.chute_assign_type, lh.area, iw.prod_line "
				+ " order by pwph.chute_assign_type, lh.area, iw.prod_line";
		
		String releasedTasksQryTalos = "select (pwph.chute_assign_type||'-'||mw.from_area||'-'||iw.prod_line) AS LOGICAL_GROUP,count(distinct mw.m_work_id) "
				+ " from MSTMBPRD.m_work mw, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd, MSTMBPRD.m_activity_dtl mad "
				+ " where mw.status in ('10') and pwpd.pack_wave_nbr = mw.work_batch_number and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and mw.m_work_id = mad.m_work_id and mad.item_id = iw.item_id and pwpd.c_mhe_status < = 30 and mw.work_description not like 'Exception%' "
				+ " group by pwph.chute_assign_type,mw.from_area, iw.prod_line order by pwph.chute_assign_type,mw.from_area, iw.prod_line";
		
		String releasedTasksQryBeumer = "select (pwph.chute_assign_type||'-'||th.begin_area||'-'||iw.prod_line)  AS LOGICAL_GROUP,count(distinct th.task_id) "
				+ "  from task_hdr th, item_wms iw, task_dtl td,	pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd "
				+ "  where th.invn_need_type = '50' and th.task_genrtn_ref_code = '44' and th.STAT_CODE IN ('10') and th.task_id = td.task_id "
				+ "  and pwpd.pack_wave_nbr = th.task_genrtn_ref_nbr	and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id  "
				+ "  and th.task_genrtn_ref_nbr in (select pack_wave_nbr from pack_wave_parm_dtl where c_mhe_status <= 30) and th.task_desc not like 'Exception%'"
				+ "  and td.item_id = iw.item_id 	group by pwph.chute_assign_type, th.begin_area,iw.prod_line order by pwph.chute_assign_type, th.begin_area,iw.prod_line";
		
		String assignedTasksQryBeumer = "select (pwph.chute_assign_type||'-'|| th.begin_area||'-'||iw.prod_line) AS LOGICAL_GROUP, count(distinct th.task_id) from task_hdr th, item_wms iw, task_dtl td, "
				+ " pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd where th.invn_need_type = '50' and th.task_genrtn_ref_code = '44' and th.STAT_CODE IN ('30','13','40') "
				+ " and th.task_id = td.task_id and pwpd.pack_wave_nbr = th.task_genrtn_ref_nbr and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and th.task_genrtn_ref_nbr in (select pack_wave_nbr from pack_wave_parm_dtl where c_mhe_status <= 30) and td.item_id = iw.item_id "
				+ " group by pwph.chute_assign_type, th.begin_area,iw.prod_line order by pwph.chute_assign_type, th.begin_area,iw.prod_line";
	
		String assignedTasksQryTalos = "select (pwph.chute_assign_type||'-'||mw.from_area||'-'|| iw.prod_line) as LOGICAL_GROUP, count(distinct mw.m_work_id) "
				+ " from MSTMBPRD.m_work mw, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd, MSTMBPRD.m_activity_dtl mad "
				+ " where mw.status in ('30') and pwpd.pack_wave_nbr = mw.work_batch_number and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and mw.m_work_id = mad.m_work_id and mad.item_id = iw.item_id and pwpd.c_mhe_status < = 30 "
				+ " group by pwph.chute_assign_type,mw.from_area, iw.prod_line order by pwph.chute_assign_type,mw.from_area, iw.prod_line";
		
		
		String lockedTasksQryBeumer = "select (pwph.chute_assign_type||'-'|| th.begin_area||'-'||iw.prod_line) AS LOGICAL_GROUP, count(distinct th.task_id) from task_hdr th, item_wms iw, task_dtl td, "
				+ " pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd where th.invn_need_type = '50' and th.task_genrtn_ref_code = '44' and th.STAT_CODE IN ('5') "
				+ " and th.task_id = td.task_id and pwpd.pack_wave_nbr = th.task_genrtn_ref_nbr and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and th.task_genrtn_ref_nbr in (select pack_wave_nbr from pack_wave_parm_dtl where c_mhe_status <= 30) and td.item_id = iw.item_id "
				+ " group by pwph.chute_assign_type, th.begin_area,iw.prod_line order by pwph.chute_assign_type, th.begin_area,iw.prod_line";
		
		String lockedTasksQryTalos = "select (pwph.chute_assign_type||'-'||mw.from_area||'-'|| iw.prod_line) as LOGICAL_GROUP, count(distinct mw.m_work_id) "
				+ " from MSTMBPRD.m_work mw, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd, MSTMBPRD.m_activity_dtl mad "
				+ " where mw.status in ('5') and pwpd.pack_wave_nbr = mw.work_batch_number and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and mw.m_work_id = mad.m_work_id and mad.item_id = iw.item_id and pwpd.c_mhe_status < = 30 "
				+ " group by pwph.chute_assign_type,mw.from_area, iw.prod_line order by pwph.chute_assign_type,mw.from_area, iw.prod_line";
		
		String assignedUnitsQryBeumer = "select (pwph.chute_assign_type||'-'|| th.begin_area ||'-'|| iw.prod_line) AS LOGICAL_GROUP, sum (td.qty_alloc-td.qty_pulld) "
				+ " from task_dtl td, task_hdr th, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd where td.task_id = th.task_id "
				+ " and th.invn_need_type = '50' and td.item_id = iw.item_id and pwpd.pack_wave_nbr = th.task_genrtn_ref_nbr "
				+ " and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and th.task_genrtn_ref_nbr in (select pack_wave_nbr from pack_wave_parm_dtl where c_mhe_status <= 30) "
				+ " and th.task_genrtn_ref_code = '44' and th.STAT_CODE IN ('30','13','40') and td.stat_code < 90 "
				+ " group by pwph.chute_assign_type, th.begin_area,iw.prod_line order by pwph.chute_assign_type, th.begin_area,iw.prod_line";
		
		String assignedUnitsQryTalos = "select (pwph.chute_assign_type ||'-'|| mw.from_area ||'-'||iw.prod_line) AS LOGICAL_GROUP,sum(mad.quantity-mad.completed_quantity) "
				+ " from MSTMBPRD.m_activity_dtl mad, MSTMBPRD.m_work mw, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd "
				+ " where mw.status in ('30') and pwpd.pack_wave_nbr = mw.work_batch_number and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and mw.m_work_id = mad.m_work_id and mad.item_id = iw.item_id and pwpd.c_mhe_status < = 30 and mw.work_description not like 'Exception%' "
				+ " group by pwph.chute_assign_type,mw.from_area, iw.prod_line order by pwph.chute_assign_type,mw.from_area, iw.prod_line"; 
		
		String lockedUnitsQryBeumer = "select (pwph.chute_assign_type||'-'|| th.begin_area ||'-'|| iw.prod_line) AS LOGICAL_GROUP, sum (td.qty_alloc-td.qty_pulld) "
				+ " from task_dtl td, task_hdr th, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd where td.task_id = th.task_id "
				+ " and th.invn_need_type = '50' and td.item_id = iw.item_id and pwpd.pack_wave_nbr = th.task_genrtn_ref_nbr "
				+ " and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and th.task_genrtn_ref_nbr in (select pack_wave_nbr from pack_wave_parm_dtl where c_mhe_status <= 30) and th.task_genrtn_ref_code = '44' "
				+ " and th.STAT_CODE IN ('5') and td.stat_code < 90 group by pwph.chute_assign_type, th.begin_area,iw.prod_line "
				+ " order by pwph.chute_assign_type, th.begin_area,iw.prod_line";
		
		String lockedUnitsQryTalos = "select (pwph.chute_assign_type ||'-'|| mw.from_area ||'-'||iw.prod_line) AS LOGICAL_GROUP,sum(mad.quantity-mad.completed_quantity) "
				+ " from MSTMBPRD.m_activity_dtl mad, MSTMBPRD.m_work mw, item_wms iw, pack_wave_parm_hdr pwph, pack_wave_parm_dtl pwpd "
				+ " where mw.status in ('5') and pwpd.pack_wave_nbr = mw.work_batch_number and pwph.pack_wave_parm_id = pwpd.pack_wave_parm_id "
				+ " and mw.m_work_id = mad.m_work_id and mad.item_id = iw.item_id and pwpd.c_mhe_status < = 30 "
				+ " group by pwph.chute_assign_type,mw.from_area, iw.prod_line order by pwph.chute_assign_type,mw.from_area, iw.prod_line"; 
		
		
		public final String GETWORKIDUSINGWAVENUMBER = "select m_work_hdr_id from m_work_hdr where batch_number = '#WAVENUMBER#' and status=10";
	
	}
	
	

	interface Update
	{
				
		/**
		 *  All Update Queries
		 */
				
		//this query updates the ABORTED waves which are existing in DB to an INVALID status(hence application code should not find aborted waves and turn 'Aborted Waves' link blue)
		public static final String updateAbortedWaveToInvalidStatus ="UPDATE WAVE_PARM SET WAVE_STAT_CODE=WAVE_STAT_CODE+1000 WHERE WAVE_NBR IN( "+
						" SELECT "+
						" WAVE_PARM.WAVE_NBR "+
						" FROM "+
						" WAVE_PARM "+
						" LEFT OUTER JOIN "+
						" M_WAVE_RECOMMEND_BATCH "+
						" ON Wave_Parm.Wave_Nbr=M_Wave_Recommend_Batch.Wave_Nbr "+
						" where  WAVE_STAT_CODE in (25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,49) "+
						" ) ";
				

		//this query REVERTS the waves which were updated to the INVALID status back to its initial ABORTED status(hence application code should find aborted waves and turn 'Aborted Waves' link red)
		public static final String revertBackUpdatedStatusAbortedWave="UPDATE WAVE_PARM SET WAVE_STAT_CODE=WAVE_STAT_CODE-1000 WHERE WAVE_STAT_CODE>1000 ";


		//this query fetches a wave as per the date created conditions below and update it to an ABORTED status(hence application code should find aborted waves and turn 'Aborted Waves' link red)
		public static final String makeWavesInAbortedStatus="UPDATE WAVE_PARM SET WAVE_STAT_CODE=(SELECT CASE NUM WHEN 47 THEN 49 WHEN 48 THEN 49 ELSE NUM END FROM (select ROUND(dbms_random.value(25,49)) num from dual)) WHERE WAVE_NBR IN( "+
			" SELECT Wave_Parm.WAVE_NBR "+
			" FROM  "+
			" WAVE_PARM "+ 
			" LEFT OUTER JOIN "+ 
			" M_WAVE_RECOMMEND_BATCH "+ 
			" ON Wave_Parm.Wave_Nbr=M_Wave_Recommend_Batch.Wave_Nbr "+ 
			" WHERE  "+
			" WAVE_PARM.CREATE_DATE_TIME > = (sysdate - 17) and WAVE_PARM.CREATE_DATE_TIME < (sysdate - 5) "+
			" and rownum<=1 "+
			" )";
		
		
		
		
		
		
		
		/**Update queries for wave rule*/
		public static final String UPDATE_RULE_COMP_VALUE ="update rule_sel_dtl set RULE_CMPAR_VALUE= ${ruleCompValue}  where colm_name = 'TC_ORDER_ID' and "
				+ " rule_id in (select RULE_ID from wave_rule_parm where wave_rule_parm_id =  ${waveRuleParmId})";
				
				public static final String UPDATE_RULE_COMP_VALUE1 ="update rule_sel_dtl set RULE_CMPAR_VALUE= ${ruleCompValue}  where colm_name = 'PROD_LINE' and "
						+ " rule_id in (select RULE_ID from wave_rule_parm where wave_rule_parm_id =  ${waveRuleParmId})";
				public static final String UPDATE_RULE_OPER_CODE="update rule_sel_dtl set OPER_CODE= 'Like'  where colm_name = 'PROD_LINE' and "
						+ " rule_id in (select RULE_ID from wave_rule_parm where wave_rule_parm_id =  ${waveRuleParmId})";
						
						public static final String UPDATE_RULE_OPER_CODE1="update rule_sel_dtl set OPER_CODE= 'Like'  where colm_name = 'TC_ORDER_ID' and "
								+ " rule_id in (select RULE_ID from wave_rule_parm where wave_rule_parm_id =  ${waveRuleParmId})";
				
		public static final String UPDATE_RULE_CREATED_DTTM ="update rule_sel_dtl set RULE_CMPAR_VALUE= '@Date' where colm_name = 'CREATED_DTTM' and "
				+ " rule_id in (select RULE_ID from wave_rule_parm where wave_rule_parm_id =  ${waveRuleParmId})";
		
		public static final String UPDATE_WAVE_RULES= "UPDATE wave_rule_parm set stat_code =90 where wave_rule_parm_id IN ( "
				+ "select wrp.wave_rule_parm_id as WAVE_RULE_PARM_ID from wave_rule_parm wrp, rule_hdr rh where wave_parm_id "
				+ "in (select ship_wave_parm_id from ship_wave_parm where wave_desc = ${wavedesc} "
				+ " and rec_type ='T' ) and rh.rule_id = wrp.rule_id and rule_type ='SS')";
		
		public static final String UPDATE_WAVE_SEL_RULE= "update wave_rule_parm set stat_code = 0 where wave_rule_parm_id =  ${waveRuleParmId}";
		
		
		/**Queries for 1325 story*/
		public static final String Update_Config_Values = "update m_generic_config_dtl set CONFIG_VALUE= ${configValue} where config_id= ${configId} and config_row_id=${confidRowID}";
		
		public static final String UpdateAllConfig_Values = "update m_generic_config_dtl set CONFIG_VALUE= ${configValue} where config_id= ${configId}";
		
		public static final String Update_Min_Task_in_Area = "update m_generic_config_dtl set config_value='200' where config_id in (select config_id from m_generic_config where config_code = 'PROTEUS/ARRANGER/LOWPICKALERT' and column_name='MIN_REL_TASKS')"
				+ "and config_row_id=(select config_row_id from m_generic_config_dtl where config_id in (select config_id from m_generic_config where config_code = 'PROTEUS/ARRANGER/LOWPICKALERT' and column_name='PICK_AREA')"
				+ "and config_value='A')";
		
		/**Queries for 1575 story*/
		public static final String Update_CONFIG_Value="update m_generic_config_dtl set config_value = ${configValue} where config_row_id = ${configRowID} and "
				+ "config_id = (select config_id from m_generic_config where config_code = ${configCode} and column_name = ${colName})";
		
		public static final String Update_Config_tasks = "update m_generic_config_dtl set config_value = ${configTasksValue} where config_row_id = ${configRowID} and config_id = "
				+ " (select config_id from m_generic_config where config_code = 'PROTEUS/REPLENISH/REPLENTASK' and column_name = 'MIN_TASKS_RELEASED_PER_REPLENISHER')";
		
	public static final String update_Automation_Settings="update m_generic_config_dtl set config_value= ${flag} where config_id in (select config_id from m_generic_config where config_code = 'PROTEUS/WAVE/AUTOSETTINGS/RELASEPACKWAVE' and column_name='AUTO_PACK_WAVE')"+
				" and config_row_id=("+
				" select config_row_id from m_generic_config_dtl where config_id in (select config_id from m_generic_config where config_code = 'PROTEUS/WAVE/AUTOSETTINGS/RELASEPACKWAVE' and column_name='CHUTE_TYPE_GRP')"+
				" and config_value='BE1,BE2,NST')";
	
	public static final String update_Rpw_Flag=" update sys_code set MISC_FLAGS='Y' where  code_type ='MST' and code_id ='RPW'";
	
	public static final String updateRWFValue ="update m_generic_config_dtl set config_value = '${configValue}' where config_row_id= ${rowID} "
				+" and config_id = (select config_id from m_generic_config where config_code = 'PROTEUS/REPLENISH/ORDSELRULE' and column_name = '${colName}')";
		
	/**Queries for RULE_FRAME_WORK story*/
	public static final String updateAllRowsToSameConfigValue="update m_generic_config_dtl set config_value = ${configValue} where "
			+ "config_id = (select config_id from m_generic_config where config_code = ${configCode} and column_name = ${colName})";
	
	/**Queries for 2540 story*/
	public static final String Update_OVERRIDE_Value="update m_generic_config_dtl set config_value= ${configValue} where config_id in (select config_id from m_generic_config where config_code = ${configCode} and column_name=${colName})"
			+" and config_row_id=("+
			" select config_row_id from m_generic_config_dtl where config_id in (select config_id from m_generic_config where config_code = ${configCode} and column_name='RULE_NAME')"+
			" and config_value='LPA-BEUMER')";
	
	/**Queries for 2034 story*/
	public static final String Update_Min_Rel_Tasks = "update m_generic_config_dtl set config_value = ${configValue} where config_id = (select config_id from m_generic_config where config_code = ${configCode} and column_name='MIN_REL_TASKS')"
			+" and  config_row_id = ("
			+" select config_row_id from m_generic_config_dtl where config_id = "
			+"(select config_id from m_generic_config where config_code = ${configCode} and column_name='OVER_RIDE_LIST') and "
			+"config_value=${override} and config_row_id in (select config_row_id from m_generic_config_dtl where config_id in "
			+"(select config_id from m_generic_config where config_code = ${configCode} and column_name='RULE_NAME') and config_value=${ruleName}))";

	public static final String updateMinRelTasksForRule = "update m_generic_config_dtl set config_value = ${configValue} where config_id = (select config_id from m_generic_config where config_code = ${genConfigCode} and column_name='MIN_REL_TASKS')"
			+" and config_row_id in (select config_row_id from m_generic_config_dtl where config_id in "
			+"(select config_id from m_generic_config where config_code = ${genConfigCode} and column_name='RULE_NAME') and config_value=${ruleName})";
	
	
	public static final String deleteAllRowsWithThisConfig = "delete from m_generic_config_dtl where config_row_id in ("
			+ " select mgdtl.config_row_id  from m_generic_config_dtl mgdtl INNER JOIN m_generic_config mgc on mgdtl.config_id= mgc.config_id where mgc.config_id in (select config_id from m_generic_config where config_code = ${genConfigCode} " 
			+ " AND  column_name= ${colName})   and  mgdtl.config_value like ${configValue}) "
			+ " AND config_id in (select config_id from m_generic_config where config_code = ${genConfigCode})";
	
	}


	interface Insert
	{
		/**
		 *  All Insert Queries
		 */
		public static final String INSERT_RULE_RULEHDR = "insert into rule_hdr (RULE_ID,RULE_GRP,RULE_TYPE,RULE_NAME,RULE_DESC,STAT_CODE,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,REC_TYPE,RULE_HDR_ID,WM_VERSION_ID,MHE_RULE_GROUP_ID)"
				+ " values (${maxRuleId},'W','SS',${ruleName},${ruleDesc},'0',sysdate,sysdate,'wmdev','A',${maxRuleId},'1','')";
		
		public static final String INSERT_RULE_SEL_DTL = "insert into rule_sel_dtl (RULE_ID,SEL_SEQ_NBR,OPEN_PARAN,TBL_NAME,COLM_NAME,OPER_CODE,RULE_CMPAR_VALUE,AND_OR_OR,CLOSE_PARAN,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,RULE_SEL_DTL_ID,RULE_HDR_ID,WM_VERSION_ID)"
				+ " values (${maxRuleId},'1','','ORDERS','TC_ORDER_ID','Like',${orderPrefix},'A','',sysdate,sysdate,'wmdev',${maxRuleSelDtlId},${maxRuleId},'1')"; 
	
		public static final String INSERT_RULE_SEL_DTL2= "insert into rule_sel_dtl (RULE_ID,SEL_SEQ_NBR,OPEN_PARAN,TBL_NAME,COLM_NAME,OPER_CODE,RULE_CMPAR_VALUE,AND_OR_OR, CLOSE_PARAN,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,RULE_SEL_DTL_ID,RULE_HDR_ID,WM_VERSION_ID) "
				+ " values (${maxRuleId},'2','','ORDERS','DO_STATUS','=','110','','',sysdate,sysdate,'wmdev',${maxRuleSelDtlId},${maxRuleId},'1')";
	
		public static final String INSERT_RULE_SEL_DTL3= "insert into rule_sel_dtl (RULE_ID,SEL_SEQ_NBR,OPEN_PARAN,TBL_NAME,COLM_NAME,OPER_CODE,RULE_CMPAR_VALUE,AND_OR_OR, CLOSE_PARAN,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,RULE_SEL_DTL_ID,RULE_HDR_ID,WM_VERSION_ID) "
				+ " values (${maxRuleId},'2','','ORDERS','CREATED_DTTM','>','@Date','A','',sysdate,sysdate,'wmdev',${maxRuleSelDtlId},${maxRuleId},'1')";
	
		public static final String INSERT_RULE_SEL_DTL_CSR= "insert into rule_sel_dtl (RULE_ID,SEL_SEQ_NBR,OPEN_PARAN,TBL_NAME,COLM_NAME,OPER_CODE,RULE_CMPAR_VALUE,AND_OR_OR, CLOSE_PARAN,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,RULE_SEL_DTL_ID,RULE_HDR_ID,WM_VERSION_ID) "
				+ " values (${maxRuleId},'3','(','ITEM_WMS','PROD_LINE','=','CSR','O','',sysdate,sysdate,'wmdev',${maxRuleSelDtlId},${maxRuleId},'1')";
	
		public static final String INSERT_RULE_SEL_DTL_BTR= "insert into rule_sel_dtl (RULE_ID,SEL_SEQ_NBR,OPEN_PARAN,TBL_NAME,COLM_NAME,OPER_CODE,RULE_CMPAR_VALUE,AND_OR_OR, CLOSE_PARAN,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,RULE_SEL_DTL_ID,RULE_HDR_ID,WM_VERSION_ID) "
				+ " values (${maxRuleId},'4','','ITEM_WMS','PROD_LINE','=','BTR','',')',sysdate,sysdate,'wmdev',${maxRuleSelDtlId},${maxRuleId},'1')";
	
		public static final String INSERT_RULE_SORT_DTL1 ="insert into rule_sort_dtl (RULE_ID,SORT_SEQ_NBR,TBL_NAME,COLM_NAME,SORT_SEQ,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,BREAK_CAPCTY,RULE_SORT_DTL_ID,RULE_HDR_ID,WM_VERSION_ID)"
				+ "values (${maxRuleId},'1','ORDERS','PICKUP_START_DTTM',${sort_seq},sysdate,sysdate,'wmdev','0',${maxRuleSortDtlId},${maxRuleId},'3')";
		
		public static final String INSERT_RULE_SORT_DTL2 ="insert into rule_sort_dtl (RULE_ID,SORT_SEQ_NBR,TBL_NAME,COLM_NAME,SORT_SEQ,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,BREAK_CAPCTY,RULE_SORT_DTL_ID,RULE_HDR_ID,WM_VERSION_ID)"
				+ "values (${maxRuleId},'2','ORDERS','CREATED_DTTM',${sort_seq},sysdate,sysdate,'wmdev','0',${maxRuleSortDtlId},${maxRuleId},'3')";
	
		
		public static final String INSERT_RULE_SEL_DTL_BUYERCODE= "insert into rule_sel_dtl (RULE_ID,SEL_SEQ_NBR,OPEN_PARAN,TBL_NAME,COLM_NAME,OPER_CODE,RULE_CMPAR_VALUE,AND_OR_OR, CLOSE_PARAN,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,RULE_SEL_DTL_ID,RULE_HDR_ID,WM_VERSION_ID) "
				+ " values (${maxRuleId},'3','','ORDERS','BUYER_CODE','=','KD','','',sysdate,sysdate,'wmdev',${maxRuleSelDtlId},${maxRuleId},'1')";
	
		
		public static final String INSERT_RULE_WAVE_RULE_PARM = "insert into wave_rule_parm (WAVE_PARM_ID,RULE_ID,WHSE,RULE_PRTY,RULE_NAME,UNITS_CAPCTY,PKT_CAPCTY,NBR_OF_LABELS_PER_SPOOL,NBR_OF_LABELS_PER_ROW,"
				+ " STAT_CODE,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,PRINT_CONTENT_LABEL,PRINT_SHIP_LABEL,WAVE_RULE_PARM_ID,SHIP_WAVE_PARM_ID,RULE_HDR_ID,WM_VERSION_ID)"
				+ " values (${parmIdWaveType},${maxRuleId},'03','1',${ruleName},'200','80','0','0', '90',sysdate,sysdate,'wmdev','0','0',${maxWaveRuleparmId},${parmIdWaveType},${maxRuleId},'260')";
		
		public static final String INSERT_BATCH="insert into m_wave_recommend_batch (M_WAVE_RECOMMEND_BATCH_ID,BATCH_NUMBER,WAVE_TEMPLATE_DESC,ORDER_SEL_RULE,STAT_CODE,WAVE_NBR,WAVE_RULE,USER_ID,NUM_ORDERS,NUM_UNITS,CREATED_DTTM,UPDATED_DTTM,DESCRIPTION,IS_AUTO,WAVED_ORDERS,WAVED_UNITS,RUN_WAVE,PICK_READY_PERCENTAGE) "
				+ " values (${maxBatchId},${maxBatchNo},${waveType},${rule},0,null,null,'B003065','0','0',SYSDATE,SYSDATE,'No orders to select',0,0,0,0,0)";
		public static final String INSERT_SYS_CODE="insert into whse_sys_code (REC_TYPE,CODE_TYPE,WHSE,CODE_ID,CODE_DESC,SHORT_DESC,MISC_FLAGS,CREATE_DATE_TIME,MOD_DATE_TIME,USER_ID,WHSE_SYS_CODE_ID,SYS_CODE_TYPE_ID,WM_VERSION_ID) VALUES ('C','PCK',03,${chuteType},${codeDesc},${shortDesc},null,sysdate,sysdate,'wmdev',${sysCodeId},null,2)";
		
	}
	
	interface OrderXmlStrings
	{
		public static final String xmlHeaderString = "<tXML>"+
					"<Header>"+
					"<Source>Host</Source>"+
					"<Action_Type>create</Action_Type>"+
					"<Message_Type>DistributionOrder</Message_Type>"+
					"<Company_ID>88</Company_ID>"+
					"<Version>2010</Version>"+
					"</Header>"+
					"<Message>";
		
		public static final String xmlOrderString = "<DistributionOrder>"+
				"<DistributionOrderId>${orderNumber}</DistributionOrderId>"+
				"<OrderType>${orderType}</OrderType>"+
				"<OrderedDttm>${dateFormatNow}</OrderedDttm>"+
				"<OriginalEstimatedNbrOfPalletsBridged>${palletsBridged}</OriginalEstimatedNbrOfPalletsBridged>"+
				"<BusinessUnit>88</BusinessUnit>"+
				"<ProductionScheduleRefNbr>${orderNumber}</ProductionScheduleRefNbr>"+
				"<ReferenceField1>1</ReferenceField1>"+
				"<ReferenceField2>0000</ReferenceField2>"+
				"<ReferenceField3>0</ReferenceField3>"+
				"<ResidentialDeliveryRequired>False</ResidentialDeliveryRequired>"+
				"<ExternalSystemPurchaseOrderNbr>${orderNumber}</ExternalSystemPurchaseOrderNbr>"+
				"<IsBackOrdered>false</IsBackOrdered>"+
				"<DcCenterNbr>101</DcCenterNbr>"+
				"<OriginFacilityAliasId>${facilityAliasId}</OriginFacilityAliasId>"+
				"<PickupStartDttm>${pickupStartDttm}</PickupStartDttm>"+
				"<PickupEndDttm>${pickupEndDttm}</PickupEndDttm>"+
		        "<DestinationAddressLine1>3425 W LUCIA DR</DestinationAddressLine1>"+
		        "<DestinationAddressLine2 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<DestinationAddressLine3 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<DestinationCity>APO</DestinationCity>"+
		        "<DestinationCountry>US</DestinationCountry>"+
		        "<DestinationStateOrProvince>CA</DestinationStateOrProvince>"+
		        "<DestinationPostalCode>94063</DestinationPostalCode>"+
		        "<DeliveryStartDttm>${delStartDttm}</DeliveryStartDttm>"+
		        "<DeliveryEndDttm>${delEndDttm}</DeliveryEndDttm>"+
		       "<CustomerName>ROOHI,ARORA</CustomerName>"+
		        "<DestinationContactName>PDD,SLD</DestinationContactName>"+
		        "<DestinationContactTelephoneNbr>782234444</DestinationContactTelephoneNbr>"+
		        "<LpnAsnRequired>Y</LpnAsnRequired>"+
		        "<PackAndHoldFlag>N</PackAndHoldFlag>"+
		        "<LpnCubingIndicator>51</LpnCubingIndicator>"+
		       " <BolType>R</BolType>"+
		        "<BillingAccountNbr>037376883</BillingAccountNbr>"+
		        "<BillToName>PDD,SLD</BillToName>"+
		        "<BillToAddressLine1>3425 W LUCIA DR</BillToAddressLine1>"+
		        "<BillToAddressLine2 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<BillToAddressLine3 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<BillToCity>APO</BillToCity>"+
		        "<BillToCountryCode>US</BillToCountryCode>"+
		        "<BillToStateOrProv>CA</BillToStateOrProv>"+
		        "<BillToPostalCode>94404</BillToPostalCode>"+
		        "<BillToTelephoneNbr>782234444</BillToTelephoneNbr>"+
		        "<RouteTo>3</RouteTo>"+
		       " <RouteType1>C</RouteType1>"+
		        "<RouteType2>NN</RouteType2>"+
		       " <ContentLabelType>COL</ContentLabelType>"+
		        "<NbrOfContentLabelsToPrint>1</NbrOfContentLabelsToPrint>"+
		        "<AddressCode>02</AddressCode>"+
		       " <ZoneSkip xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<CustomerRouting>N</CustomerRouting>"+
		        "<DistributionOrderType>Customer Order</DistributionOrderType>"+
		        "<RoutingAttribute xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<SalesmanNumber>${buyerCode}</SalesmanNumber>"+
		        "<AllowPreBilling>true</AllowPreBilling>"+
		        "<Comment>"+
		           " <NoteType>CI</NoteType>"+
		            "<NoteCode>AC</NoteCode>"+
		            "<CommentText>0000000000000000000000000000000069500000</CommentText></Comment> <Comment>"+
		            "<NoteType>CI</NoteType>"+
		            "<NoteCode>MT</NoteCode>"+
		            "<CommentText>VISA8433</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>1</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_1</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		           "<NoteType>2</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_2</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>3</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_3</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>4</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_4</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>5</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_5</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>6</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_6</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>7</NoteType>"+
		            "<NoteCode>NA</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_7</CommentText><!-- Change this for special instruction code 7-->"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>9</NoteType>"+
		            "<NoteCode>${noteCode}</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_9</CommentText><!-- Change this for special instruction code 9-->"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>10</NoteType>"+
		            "<NoteCode>007</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_10</CommentText>"+
		        "</Comment>"+
		        "<AccessorialOptionGroupList>"+
		            "<AccessorialOptionGroup/>"+
		        "</AccessorialOptionGroupList>";	
		
		public static final String xmlOrderStringOld = "<DistributionOrder>"+
				"<DistributionOrderId>${orderNumber}</DistributionOrderId>"+
				"<OrderType>${orderType}</OrderType>"+
				"<OrderedDttm>${dateFormatNow}</OrderedDttm>"+
				"<BusinessUnit>88</BusinessUnit>"+
				"<ProductionScheduleRefNbr>${orderNumber}</ProductionScheduleRefNbr>"+
				"<ReferenceField1>1</ReferenceField1>"+
				"<ReferenceField2>0000</ReferenceField2>"+
				"<ReferenceField3>0</ReferenceField3>"+
				"<ResidentialDeliveryRequired>False</ResidentialDeliveryRequired>"+
				"<ExternalSystemPurchaseOrderNbr>${orderNumber}</ExternalSystemPurchaseOrderNbr>"+
				"<IsBackOrdered>false</IsBackOrdered>"+
				"<DcCenterNbr>101</DcCenterNbr>"+
				"<OriginFacilityAliasId>${facilityAliasId}</OriginFacilityAliasId>"+
				"<PickupStartDttm>${dateFormatNow}</PickupStartDttm>"+
				"<PickupEndDttm>${dateFormatNow}</PickupEndDttm>"+
		        "<DestinationAddressLine1>3425 W LUCIA DR</DestinationAddressLine1>"+
		        "<DestinationAddressLine2 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<DestinationAddressLine3 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<DestinationCity>APO</DestinationCity>"+
		        "<DestinationCountry>US</DestinationCountry>"+
		        "<DestinationStateOrProvince>CA</DestinationStateOrProvince>"+
		        "<DestinationPostalCode>94063</DestinationPostalCode>"+
		        "<DeliveryStartDttm>${dateFormatThen}</DeliveryStartDttm>"+
		        "<DeliveryEndDttm>${dateFormatThen}</DeliveryEndDttm>"+
		       "<CustomerName>ROOHI,ARORA</CustomerName>"+
		        "<DestinationContactName>PDD,SLD</DestinationContactName>"+
		        "<DestinationContactTelephoneNbr>782234444</DestinationContactTelephoneNbr>"+
		        "<LpnAsnRequired>Y</LpnAsnRequired>"+
		        "<PackAndHoldFlag>N</PackAndHoldFlag>"+
		        "<LpnCubingIndicator>51</LpnCubingIndicator>"+
		       " <BolType>R</BolType>"+
		        "<BillingAccountNbr>037376883</BillingAccountNbr>"+
		        "<BillToName>PDD,SLD</BillToName>"+
		        "<BillToAddressLine1>3425 W LUCIA DR</BillToAddressLine1>"+
		        "<BillToAddressLine2 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<BillToAddressLine3 xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<BillToCity>APO</BillToCity>"+
		        "<BillToCountryCode>US</BillToCountryCode>"+
		        "<BillToStateOrProv>CA</BillToStateOrProv>"+
		        "<BillToPostalCode>94404</BillToPostalCode>"+
		        "<BillToTelephoneNbr>782234444</BillToTelephoneNbr>"+
		        "<RouteTo>3</RouteTo>"+
		       " <RouteType1>C</RouteType1>"+
		        "<RouteType2>NN</RouteType2>"+
		       " <ContentLabelType>COL</ContentLabelType>"+
		        "<NbrOfContentLabelsToPrint>1</NbrOfContentLabelsToPrint>"+
		        "<AddressCode>02</AddressCode>"+
		       " <ZoneSkip xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<CustomerRouting>N</CustomerRouting>"+
		        "<DistributionOrderType>Customer Order</DistributionOrderType>"+
		        "<RoutingAttribute xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
		        "<SalesmanNumber>${buyerCode}</SalesmanNumber>"+
		        "<AllowPreBilling>true</AllowPreBilling>"+
		        "<Comment>"+
		           " <NoteType>CI</NoteType>"+
		            "<NoteCode>AC</NoteCode>"+
		            "<CommentText>0000000000000000000000000000000069500000</CommentText></Comment> <Comment>"+
		            "<NoteType>CI</NoteType>"+
		            "<NoteCode>MT</NoteCode>"+
		            "<CommentText>VISA8433</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>1</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_1</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		           "<NoteType>2</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_2</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>3</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_3</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>4</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_4</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>5</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_5</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>6</NoteType>"+
		            "<NoteCode>N</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_6</CommentText>"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>7</NoteType>"+
		            "<NoteCode>NA</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_7</CommentText><!-- Change this for special instruction code 7-->"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>9</NoteType>"+
		            "<NoteCode>${noteCode}</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_9</CommentText><!-- Change this for special instruction code 9-->"+
		        "</Comment>"+
		        "<Comment>"+
		            "<NoteType>10</NoteType>"+
		            "<NoteCode>007</NoteCode>"+
		            "<CommentText>SPL_INSTR_CODE_10</CommentText>"+
		        "</Comment>"+
		        "<AccessorialOptionGroupList>"+
		            "<AccessorialOptionGroup/>"+
		        "</AccessorialOptionGroupList>";
		
		public static final String xmlItemOrderString = "<LineItem>"+
                "<DoLineNbr>${lineItemNo}</DoLineNbr>"+
                "<ItemName>${itemName}</ItemName>"+
                "<ItemSubstituitionCode>*</ItemSubstituitionCode>"+
                "<ItemSubstituitionCodeValue>*</ItemSubstituitionCodeValue>"+
                "<CubeMultipleQty>1</CubeMultipleQty>"+
                "<LpnType xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
                "<Price>17.99</Price>"+
                "<RetailPrice>19.99</RetailPrice>"+
                "<SKU100Inventory xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:nil='true'/>"+
                "<FulfillmentType>1</FulfillmentType>"+
                "<Quantity>"+
                    "<OrderQty>${orderQty}</OrderQty>"+
                    "<QtyUOM>Unit</QtyUOM>"+
                "</Quantity>"+
                "<Comment>"+
                   " <NoteType>CI</NoteType>"+
                    "<NoteCode>MD</NoteCode>"+
                    "<CommentText>ACTIVE MEZZ HARD UPC </CommentText>"+
                "</Comment>"+
                "<Comment>"+
                    "<NoteType>CI</NoteType>"+
                    "<NoteCode>TX</NoteCode>"+
                    "<CommentText>00000</CommentText>"+
                "</Comment>"+
                "<Comment>"+
                   " <NoteType>CI</NoteType>"+
                    "<NoteCode>RD</NoteCode>"+
                    "<CommentText>ACTIVE MEZZ HARD UPC </CommentText>"+
                "</Comment>"+
                "<CapacityType>"+
                    "<CapacityType>BEU</CapacityType>"+
                    "<SAM>1</SAM>"+
                   " <Units>1</Units>"+
                    "<Picks>0</Picks>"+
                    "<LPNs>1</LPNs>"+
                "</CapacityType>"+
                "<SKUAttributes>"+
		     "<ProductStatus>${prodStat}</ProductStatus>"+
		    "</SKUAttributes> </LineItem>";
	}

	interface talosEventMessages
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();

		public static final String Logout = "{"+
				  "time"+":"+date+","+				
				  "eventName"+":"+ "Logout"+","+				
				  "transactionName"+":"+ "Handheld Logout"+","+
				  "locnBrcd"+":"+ "HA02P089"+","+				
				  "input"+":"+ null+","+				
				  "output"+":"+ null+","+			
				  "server"+":"+"wms"+","+			
				  "app"+":"+ "HHELD"+","+				
				  "user"+":"+ "B002147"+","+				
				  "sessionId"+":"+ "session-001"+
				  "}";
		
	}
	
	
	public static final String containerRetrieve = "select * from entity_attributes where entity_id='%s' and entity_type='%s' and enabled = 1 and %s ";
	public static final String updateAllAttributesMandatory = "update attribute_definition set MANDATORY = %s where DESCRIPTION %s in (%s) and entity_type_id "
			+ " in (select id from entity_info where description = '%s')";
	
	String dltCont="delete from entity_attributes where entity_id='%s' and entity_type='%s' and enabled = 1 and %s";
	
	String dltDimention="delete ENTITY_dimension where entity_id='%s' ";
	String GETMGENDTLID ="select gcd.M_GENERIC_CONFIG_DTL_ID from m_generic_config_dtl gcd,m_generic_config gc where gcd.config_id = gc.config_id and gcd.config_id in "
    		+ " (select config_id from m_generic_config where config_code = '$configCode' and gc.COLUMN_NAME = '$colName')";
	String UPDATEGENERICCONFIG ="update m_generic_config_dtl set CONFIG_VALUE = '$configValue' where M_GENERIC_CONFIG_DTL_ID = $configDtlId";
	String GET_ITEM_NAME_FROM_WMINV_FOR_LOCN="select item_name from item_cbo ic inner join wm_inventory wmi on item_id=item_id where location_id='#location_id' ";
	String GETLOCNIDFORWORKDTL="SELECT FROM_LOCATION_ID AS LOCN_ID FROM M_WORK_DTL WHERE M_WORK_HDR_ID='#WORKID#' order by DATE_CREATED asc";
	String GETWORKLOG_CANCEL="select * from(select * from m_work_log where action ='#ACTION#' and work_batch_number='#WORK_BATCH_NUMBER#' and ITEM_ID='#ITEM_ID#' and DATE_CREATED >= SYSDATE - 1  order by date_created desc)where rownum<=1";
	String GETWORKLOG="select * from(select * from m_work_log where action ='#ACTION#' and work_batch_number='#WORK_BATCH_NUMBER#' and DATE_CREATED >= SYSDATE - 1  order by date_created desc)where rownum<=1";

	

}
