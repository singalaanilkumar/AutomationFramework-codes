package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLResearchInventory {
    public static final String Inventory_Snapshot = "select quantity, item, container, locn_nbr from inventory_snapshot where container in (%s) and enabled = 1";
    public static final String Inventory_Container = "select entity_status from entity_attributes where entity_id = '%s' and enabled = 1";
    public static final String INVENTORY_QTY = "select sum(quantity) from inventory.inventory_snapshot where container in (%s)";
    public static final String Cartons_Attribute_Disabled_Status = "select distinct container from inventory.inventory_snapshot where container in (select entity_id from inventory.entity where entity_type = 'CRT' and entity_status in ('IPK','PCK','MFT','WGH','SHP') and id in (select entity_id from inventory.common_entity_attributes where attribute_value = '%s') and enabled = 1)";
    public static final String Enable_Container = "update inventory.entity set enabled = 1 where entity_id = '%s' order by created_time desc limit 1";

    public static final String ContainerInquiry = "SELECT ENTITY_ID as 'Container',ENTITY_TYPE as 'Container Type',CASE ENTITY_STATUS WHEN 'CRE' THEN 'Created' WHEN 'LCT' THEN 'Located' WHEN 'PRT' THEN 'Printed' WHEN 'VSC' THEN 'PrepCompleted' WHEN 'PTS' THEN 'PutToStore' WHEN 'STG' THEN 'Staged' END AS 'Status',CREATED_BY as 'Created By',CONCAT(CREATED_TIME,' UTC') as 'Created On' FROM inventory.entity where ENTITY_TYPE='%s' AND ENABLED='1' AND ID in (SELECT ENTITY_ID FROM inventory.common_entity_attributes where ATTRIBUTE_NAME='POReceipt' and ATTRIBUTE_VALUE='%s' and ENABLED='1');";

    public static final String ContainerInquirydetails="select DISTINCT ITEM as 'Item',QUANTITY as 'Quantity',inventory.status.STATUS_CODE as 'Status',inventory_snapshot.CREATED_BY as 'Created By',CONCAT(inventory_snapshot.CREATED_TIME,' UTC') as 'Created On' from inventory.inventory_snapshot\n" +
            "INNER JOIN inventory.status\n" +
            "ON inventory_snapshot.STATUS_ID=inventory.status.ID\n" +
            " where CONTAINER='%s';";

    public static final String ContainerInquiryattributes="SELECT GROUP_CONCAT(PO) AS PO, GROUP_CONCAT(PO_RECEIPT) AS POReceipt, GROUP_CONCAT(PROCESSS_AREA) AS ProcessArea\n" +
            "FROM\n" +
            "(SELECT\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME = 'PO' THEN  CEA.ATTRIBUTE_VALUE END AS PO ,\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME = 'POReceipt' THEN  CEA.ATTRIBUTE_VALUE END AS PO_RECEIPT,\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME = 'ProcessArea' THEN  CEA.ATTRIBUTE_VALUE END AS PROCESSS_AREA\n" +
            "FROM INVENTORY.ENTITY ENT INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES CEA\n" +
            "ON ENT.ID = CEA.ENTITY_ID\n" +
            "AND ENT.ENTITY_ID = '%s') T1\n" +
            ";";

    public static final String ContainerInquiryassociations="SELECT PARENT as 'Parent Container',CT1.CODE as 'Parent Container Type',CHILD as 'Child Container',\n" +
            "                   CT2.CODE as 'Child Container Type',DEPTH as 'Position' \n" +
            "                   FROM inventory_ng.container_relationship CR,inventory_ng.container_type CT1,inventory_ng.container_type CT2 \n" +
            "            WHERE CHILD='%s' and CT1.CODE=CR.PARENT_CONTAINER_TYPE and CT2.CODE=CHILD_CONTAINER_TYPE;";


/*          "SELECT PARENT as 'Parent Container',CT1.CODE as 'Parent Container Type',CHILD as 'Child Container',CT2.CODE as 'Child Container Type',
            DEPTH as 'Position' FROM inventory.container_relationship CR,inventory.container_type CT1,inventory.container_type CT2
            WHERE CHILD='%s' and CT1.id=CR.PARENT_TYPE_ID and CT2.id=CHILD_TYPE_ID;";*/

    public static final String InventoryInquiry="select DISTINCT ITEM as 'UPC',QUANTITY as 'Quantity',inventory_ng.status.STATUS as 'Status',CONTAINER as 'Container',\n" +
            "       inventory_ng.container_type.CODE as 'Container Type',inventory_snapshot.CREATED_BY as 'Created By',\n" +
            "       inventory_snapshot.ENABLED as 'Enabled',CONCAT(inventory_snapshot.CREATED_TIME,' UTC') as 'Created On'\n" +
            "from inventory_ng.inventory_snapshot\n" +
            "INNER JOIN inventory_ng.status ON inventory_snapshot.INVENTORY_STATUS=inventory_ng.status.STATUS_CODE\n" +
            "INNER JOIN inventory_ng.container_type ON CONTAINER_TYPE=inventory_ng.container_type.CODE\n" +
            "where CONTAINER='%s'\n";

/*    "select DISTINCT ITEM as 'UPC',QUANTITY as 'Quantity',inventory.status.STATUS as 'Status',CONTAINER as 'Container',inventory.container_type.CODE as 'Container Type',inventory_snapshot.CREATED_BY as 'Created By',inventory_snapshot.ENABLED as 'Enabled',CONCAT(inventory_snapshot.CREATED_TIME,' UTC') as 'Created On' from inventory.inventory_snapshot\n" +
            "    INNER JOIN inventory.status\n" +
            "    ON inventory_snapshot.STATUS_ID=inventory.status.ID\n" +
            "    INNER JOIN inventory.container_type\n" +
            "    ON CONTAINER_TYPE_ID=inventory.container_type.ID\n" +
            "    where CONTAINER='%s'";*/

    public static final String InventoryInquiryAttributes="SELECT  PO_LINE_BARCODE as POLineBarcode, \n" +
            "MAX(Case When CEA.ATTRIBUTE_NAME = 'Department' Then  CEA.ATTRIBUTE_VALUE End ) Department,\n" +
            "MAX(Case When CEA.ATTRIBUTE_NAME = 'PID' Then  CEA.ATTRIBUTE_VALUE End ) PID \n" +
            "from inventory_ng.inventory_snapshot \n" +
            "Inner Join inventory_ng.inventory_snapshot_attributes CEA On CEA.INVENTORY_SNAPSHOT_ID = inventory_ng.inventory_snapshot.ID\n" +
            "where INV_CONTAINER_ID in(\n" +
            "\t       (select ID from inventory_ng.inv_container \n" +
            "\t\t\tWHERE ID in(SELECT INV_CONTAINER_ID FROM inventory_ng.inventory_snapshot WHERE CONTAINER='%s')\n" +
            "            )) \n" +
            "GROUP BY poLineBarcode;";

/*           "SELECT GROUP_CONCAT(POLineBarcode) as POLineBarcode,GROUP_CONCAT(Department) as Department,GROUP_CONCAT(PID) as PID from\n" +
            "(SELECT CASE WHEN CEA.ATTRIBUTE_NAME='Department' THEN CEA.ATTRIBUTE_VALUE END AS Department,\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME='PID' THEN CEA.ATTRIBUTE_VALUE END AS PID,\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME='POLineBarcode' THEN CEA.ATTRIBUTE_VALUE END AS POLineBarcode\n" +
            "FROM inventory.common_entity_attributes CEA\n" +
            "where ENTITY_ID in(\n" +
            "(select ID from inventory.entity where ENTITY_TYPE='INVN' AND ENTITY_ID in(SELECT ID FROM inventory.inventory_snapshot WHERE CONTAINER='%s')))) T1;\n";*/


    public static final String InventoryAdjustmentInquiry="SELECT CONTAINER as 'Container',ITEM as 'UPC',CURRENT_QUANTITY-PREVIOUS_QUANTITY as 'Adjustment',REASON_CODE as 'Reason Code',USER_ADDED_REASON_CODE as 'User Reason Code',CREATED_BY as 'Created By',CONCAT(CREATED_TIME,' UTC') as 'Created Time' from inventory_ng.inventory_adjustment_history where CONTAINER='%s';\n";

    public static final String InventoryAdjustmentAttributes=" SELECT T1.PO,T1.POReceipt,T2.Department,T2.PID,T2.POLineBarcode FROM\n" +
            "(SELECT GROUP_CONCAT(PO) AS PO, GROUP_CONCAT(PO_RECEIPT) AS POReceipt, GROUP_CONCAT(PROCESSS_AREA) AS ProcessArea\n" +
            "FROM\n" +
            "(SELECT\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME = 'PO' THEN  CEA.ATTRIBUTE_VALUE END AS PO ,\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME = 'POReceipt' THEN  CEA.ATTRIBUTE_VALUE END AS PO_RECEIPT,\n" +
            "CASE WHEN CEA.ATTRIBUTE_NAME = 'ProcessArea' THEN  CEA.ATTRIBUTE_VALUE END AS PROCESSS_AREA\n" +
            "FROM INVENTORY.ENTITY ENT INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES CEA\n" +
            "ON ENT.ID = CEA.ENTITY_ID\n" +
            "AND ENT.ENTITY_ID = '%s')AS X) AS T1\n" +
            ",\n" +
            "(SELECT GROUP_CONCAT(Department) as Department,GROUP_CONCAT(PID) as PID,GROUP_CONCAT(POLineBarcode) as POLineBarcode\n" +
            " FROM\n" +
            "            (SELECT CASE WHEN CEA.ATTRIBUTE_NAME='Department' THEN CEA.ATTRIBUTE_VALUE END AS Department,\n" +
            "            CASE WHEN CEA.ATTRIBUTE_NAME='PID' THEN CEA.ATTRIBUTE_VALUE END AS PID,\n" +
            "            CASE WHEN CEA.ATTRIBUTE_NAME='POLineBarcode' THEN CEA.ATTRIBUTE_VALUE END AS POLineBarcode\n" +
            "            FROM inventory.common_entity_attributes CEA\n" +
            "            where ENTITY_ID in\n" +
            "            (select ID from inventory.entity where ENTITY_TYPE='INVN' AND ENTITY_ID in(SELECT ID FROM inventory.inventory_snapshot WHERE CONTAINER='%s'))) AS Y) AS T2;";


    public static final String LocationView="SELECT distinct container_relationship.PARENT as 'Location',ITEM as 'UPC',sum(QUANTITY) as 'Units',count(container_relationship.CHILD) as 'Container',\n" +
            "       location.entity_attributes.ATTRIBUTE_VALUES as 'ATTRIBUTEVALUES' FROM inventory_ng.inventory_snapshot \n" +
            "INNER JOIN inventory_ng.container_relationship\n" +
            "ON inventory_snapshot.CONTAINER=container_relationship.CHILD and inventory_snapshot.ENABLED='1' and container_relationship.ENABLED='1'\n" +
            "INNER JOIN location.entity_attributes\n" +
            "ON entity_attributes.ENTITY_ID=container_relationship.PARENT\n" +
            "where container_relationship.PARENT='CC04A093' group by ITEM,ATTRIBUTE_VALUES;";

 /*  Query from Inventory
    public static final String LocationView="SELECT distinct container_relationship.PARENT as 'Location',ITEM as 'UPC',sum(QUANTITY) as 'Units',count(container_relationship.CHILD) as 'Container'," +
            "location.entity_attributes.ATTRIBUTE_VALUES as 'ATTRIBUTEVALUES' FROM inventory.inventory_snapshot \n" +
            "INNER JOIN inventory.container_relationship\n" +
            "ON inventory_snapshot.CONTAINER=container_relationship.CHILD and inventory_snapshot.ENABLED='1' and container_relationship.ENABLED='1'\n" +
            "INNER JOIN location.entity_attributes\n" +
            "ON entity_attributes.ENTITY_ID=container_relationship.PARENT\n" +
            "where container_relationship.PARENT='CC04A093' group by ITEM,ATTRIBUTE_VALUES;";*/

    public static final String LocationViewHeaders="SELECT DESCRIPTION FROM location.attribute_definition where Id in (SELECT ATTRIBUTE_ID FROM location.applicable_attributes where group_id='%s');";
    public static final String ParentEnquiry = "select parent from inventory_ng.CONTAINER_RELATIONSHIP where child='%s';";
}

