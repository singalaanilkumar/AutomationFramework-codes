package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLQueriesRL {

	public static final String GET_RL_ACTIVITY_BY_PORCPT_SQL= "\n" +
			"SELECT wa.id,"+"\n"+
			"was.status_cd,"+"\n"+
			"wal.assigned_to"+"\n"+
			"FROM   wsm.activity wa,"+"\n"+
			"wsm.activity_lifecycle wal,"+"\n"+
			"wsm.activity_status was,"+"\n"+
			"wsm.activity_attributes waa,"+"\n"+
			"wsm.activity_attributes waa1"+"\n"+
			"WHERE  wa.type = 'RELEASE'"+"\n"+
			"AND wa.container_type = 'LANE'"+"\n"+
			"AND wa.enabled = 1"+"\n"+
			"AND wal.activity_id = wa.id"+"\n"+
			"AND waa.activity_id = wa.id"+"\n"+
			"AND waa1.activity_id = wa.id"+"\n"+
			"AND waa.enabled = 1"+"\n"+
			"AND waa1.enabled = 1"+"\n"+
			"AND wa.locn_nbr = %s"+"\n"+
			"AND was.id = wal.status_id"+"\n"+
			"AND wal.enabled = 1"+"\n"+
			"AND waa.attribute_desc = 'poNbr'"+"\n"+
			"AND waa1.attribute_desc = 'poRcptNbr'"+"\n"+
			"AND waa.attribute_value = '%s'"+" and wa.created_time >= NOW() - INTERVAL 30 MINUTE ";

	public static final String GET_RL_ACTIVITY_BY_ID = "\n" +
            "SELECT wa.id,"+"\n"+
            	"wa.total_qty,"+"\n"+  
            	"was.status_cd,"+"\n"+ 
            	"wa.container,"+"\n"+ 
            	"wal.assigned_to,"+"\n"+ 
       			"wa.created_time,"+"\n"+ 
       			"waa.attribute_value poNbr,"+"\n"+ 
       			"waa1.attribute_value poRcptNbr,"+"\n"+ 
       			"wa.locn_nbr"+"\n"+ 
       			"FROM   wsm.activity wa,"+"\n"+ 
       			"wsm.activity_lifecycle wal,"+"\n"+ 
       			"wsm.activity_status was,"+"\n"+ 
       			"wsm.activity_attributes waa,"+"\n"+
       			"wsm.activity_attributes waa1"+"\n"+
       			"WHERE  wa.type = 'RELEASE'"+"\n"+ 
       			"AND wa.container_type = 'LANE'"+"\n"+ 
       			"AND wa.enabled = 1"+"\n"+ 
       			"AND wal.activity_id = wa.id"+"\n"+ 
       			"AND waa.activity_id = wa.id"+"\n"+
       			"AND waa1.activity_id = wa.id"+"\n"+
       			"AND waa.enabled = 1"+"\n"+
       			"AND waa1.enabled = 1"+"\n"+
       			"AND wa.locn_nbr = %s"+"\n"+ 
       			"AND was.id = wal.status_id"+"\n"+ 
       			"AND wal.enabled = 1"+"\n"+ 
       			"AND was.status_cd = '%s'"+"\n"+ 
       			"AND waa.attribute_desc = 'poNbr'"+"\n"+ 
       			"AND waa1.attribute_desc = 'poRcptNbr'"+"\n"+
       			"AND wa.id in ('%s')";
	public final static String validateToteAfterCompletion_sql = "Select * from inventory.container_relationship where CHILD in (Select ENTITY_ID from inventory.entity_attributes where JSON_CONTAINS(attribute_values,'{\"key\": \"POReceipt\", \"values\": [\"%s\"]}')) and enabled = 1";

	public final static String VALIDATE_TOTE_MOVEMENT_SQL = "SELECT parent FROM "
			+ "inventory.container_relationship "
			+ "WHERE CHILD in (Select ENTITY_ID from inventory.entity_attributes "
			+ "WHERE JSON_CONTAINS(attribute_values,'{\"key\": \"POReceipt\", \"values\": [\"%s\"]}')) and enabled = 1";

	public static final String GET_RL_ACTIVITYSTATUS_BY_ID_SQL = "\n" +
			"SELECT was.status_cd"+"\n"+
			"FROM   wsm.activity wa,"+"\n"+
			"wsm.activity_lifecycle wal,"+"\n"+
			"wsm.activity_status was,"+"\n"+
			"wsm.activity_attributes waa,"+"\n"+
			"wsm.activity_attributes waa1"+"\n"+
			"WHERE  wa.type = 'RELEASE'"+"\n"+
			"AND wa.container_type = 'LANE'"+"\n"+
			"AND wa.enabled = 1"+"\n"+
			"AND wal.activity_id = wa.id"+"\n"+
			"AND waa.activity_id = wa.id"+"\n"+
			"AND waa1.activity_id = wa.id"+"\n"+
			"AND waa.enabled = 1"+"\n"+
			"AND waa1.enabled = 1"+"\n"+
			"AND wa.locn_nbr = %s"+"\n"+
			"AND was.id = wal.status_id"+"\n"+
			"AND wal.enabled = 1"+"\n"+
			"AND waa.attribute_desc = 'poNbr'"+"\n"+
			"AND waa1.attribute_desc = 'poRcptNbr'"+"\n"+
			"AND wa.id in ('%s')";

}
