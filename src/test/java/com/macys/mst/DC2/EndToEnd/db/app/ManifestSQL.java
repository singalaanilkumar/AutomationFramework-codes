package com.macys.mst.DC2.EndToEnd.db.app;

public class ManifestSQL {
	public static final String Manifest="SELECT cast(base_amount as DECIMAL(9,2)) as \"Base Amount\",cast(total_amount as DECIMAL(9,2)) as \"Total Shipment Amount\",\n" +
			"    CASE WHEN status=\"MFT\" THEN \"Manifested\" END as \"Carton Status\", cast(width as DECIMAL(9,1)) as \"Width (Dimension)\",\n" +
			"    tracking_number as \"Tracking Number\",\n" +
			"\tcast(length as DECIMAL(9,1)) as \"Length (Dimension)\", cast(height as DECIMAL(9,1)) as \"Height (Dimension)\", \n" +
			"    cast(weight as DECIMAL(9,1)) as \"Actual Weight\", ship_via as \"Ship Via\" FROM package.package \n" +
			"where enabled =1 and barcode ='%s';";

			/*
			"SELECT X.TrackingNbr as \"Tracking Number\",X.BaseAmount as \"Base Amount\",X.ShipVia as \"Ship Via\",X.TotalAmount AS \"Total Shipment Amount\",Y.STATUS as \"Carton Status\",replace(Z.Length,'0','') AS \"Length (Dimension)\",replace(Z.Width,'0','') AS \"Width (Dimension)\",replace(Z.Height,'0','') AS \"Height (Dimension)\",replace(Z.Weight,'0','') AS \"Actual Weight\"\n" +
			"FROM\n" +
			"(SELECT GROUP_CONCAT(TrackingNbr) as TrackingNbr,GROUP_CONCAT(ShipVia) as ShipVia,GROUP_CONCAT(BaseAmount) as BaseAmount,group_concat(TotalAmount) as TotalAmount\n" +
			" from\n" +
			"            (SELECT CASE WHEN CEA.ATTRIBUTE_NAME='TrackingNbr' THEN CEA.ATTRIBUTE_VALUE END AS TrackingNbr,\n" +
			"            CASE WHEN CEA.ATTRIBUTE_NAME='BaseAmount' THEN CEA.ATTRIBUTE_VALUE END AS BaseAmount,\n" +
			"             CASE WHEN CEA.ATTRIBUTE_NAME='ShipVia' THEN CEA.ATTRIBUTE_VALUE END AS ShipVia,\n" +
			"             CASE WHEN CEA.ATTRIBUTE_NAME='TotalAmount' THEN CEA.ATTRIBUTE_VALUE END AS TotalAmount\n" +
			"            FROM inventory.common_entity_attributes CEA\n" +
			"\t\t\twhere ENTITY_ID in(\n" +
			"            (select ID from inventory.entity where ENTITY_TYPE='CRT' and ENTITY_ID='%s')))T1) AS X\n" +
			"    ,\n" +
			"    (SELECT container_status.STATUS FROM inventory.entity inner join inventory.container_status on container_status.STATUS_CODE = entity.ENTITY_STATUS where ENTITY_ID='%s') AS Y,\n" +
			"    \n" +
			"    (select group_concat(Length) as \"Length\",group_concat(Width) as \"Width\",group_concat(Height) as \"Height\",group_concat(Weight) as \"Weight\"\n" +
			" from\n" +
			"( SELECT CASE WHEN DIMENSION_ID='1' THEN VALUE END AS 'Length',\n" +
			" CASE WHEN DIMENSION_ID='2' THEN VALUE END AS 'Width',\n" +
			" CASE WHEN DIMENSION_ID='3' THEN VALUE END AS 'Height',\n" +
			" CASE WHEN DIMENSION_ID='5' THEN VALUE END AS 'Weight'\n" +
			" FROM inventory.entity_dimension  where ENTITY_ID='%s')T2)Z;";*/


}
