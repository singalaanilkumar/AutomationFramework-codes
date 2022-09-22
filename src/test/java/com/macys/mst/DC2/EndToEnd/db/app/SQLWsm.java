package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLWsm {
    public static final String WSMActivities = "select * from wsm.activity where container = '%s' and container_type = '%s' and enabled = 1 and created_time >= NOW() - INTERVAL 30 MINUTE";
    public static final String SEARCH_ACTIVITY =
    		"SELECT\n"
    		+ "            ACT.ID AS 'Activity ID', ATYPE.DESCRIPTION AS 'Activity Type', LIFE.QTY AS 'Quantity', LIFE.ASSIGNED_TO AS 'Assigned To', STAT.STATUS_CD AS Status, ACT.CONTAINER AS 'Container Id', ACT.CONTAINER_TYPE AS 'Container Type', ACT.UPC AS 'UPC', CONCAT(ACT.CREATED_TIME,' UTC') AS 'Created Time', CONCAT(ACT.UPDATED_TIME,' UTC') AS 'Last Updated Time',ATTR.PO_NBR AS 'PO', ATTR.PO_RCPT_NBR AS 'PO Receipt Number', ATTR.SEQUENCE AS 'Sequence', CASE ATTR.SUB_TYPE WHEN 'R' THEN 'Regular Store' WHEN 'N' THEN 'New Store' WHEN 'P' THEN 'Packaway' END AS 'SubType', ATTR.PID AS 'PID', ATTR.PROCESS_AREA AS 'Process Area', ATTR.PREP_INSTRUCTIONS AS 'Prep Instructions', ATTR.LOCN_NUMBER AS 'Location Number', ATTR.CARTON_NUMBER AS 'Carton Number', ATTR.LOCN_SEQUENCE AS 'Location Sequence'\n"
    		+ "        FROM\n"
    		+ "            WSM.ACTIVITY ACT\n"
    		+ "            JOIN WSM.ACTIVITY_TYPE ATYPE\n"
    		+ "                ON ACT.TYPE_ID = ATYPE.ID\n"
    		+ "            JOIN WSM.ACTIVITY_LIFECYCLE LIFE\n"
    		+ "                ON ACT.ID = LIFE.ACTIVITY_ID\n"
    		+ "            JOIN WSM.ACTIVITY_STATUS STAT\n"
    		+ "                ON LIFE.STATUS_ID = STAT.ID\n"
    		+ "            LEFT JOIN\n"
    		+ "                (\n"
    		+ "                    SELECT\n"
    		+ "                        ATTR1.ACTIVITY_ID, ATTR1.ATTRIBUTES, GROUP_CONCAT(PO_NBR) PO_NBR, GROUP_CONCAT(PO_RCPT_NBR) PO_RCPT_NBR, GROUP_CONCAT(SEQUENCE) SEQUENCE, GROUP_CONCAT(SUB_TYPE) SUB_TYPE, GROUP_CONCAT(PID) PID, GROUP_CONCAT(PROCESS_AREA) PROCESS_AREA, GROUP_CONCAT(PREP_INSTRUCTIONS) PREP_INSTRUCTIONS, GROUP_CONCAT(WAVE_NUMBER) WAVE_NUMBER, GROUP_CONCAT(LOCN_NUMBER) LOCN_NUMBER, GROUP_CONCAT(CARTON_NUMBER) CARTON_NUMBER, GROUP_CONCAT(LOCN_SEQUENCE) LOCN_SEQUENCE\n"
    		+ "                    FROM\n"
    		+ "                        (SELECT ATT.ACTIVITY_ID, CONCAT('{', GROUP_CONCAT(CONCAT('\"',DEF.DESCRIPTION,'\":\"',ATT.ATTRIBUTE_VALUE,'\"')),'}') ATTRIBUTES FROM WSM.ACTIVITY_ATTRIBUTES ATT JOIN WSM.ATTRIBUTES_DEFINITION DEF ON ATT.ATTRIBUTE_ID = DEF.ID WHERE ATT.ATTRIBUTE_VALUE NOT LIKE '%%\"%%' GROUP BY ATT.ACTIVITY_ID) ATTR1\n"
    		+ "                        JOIN(SELECT\n"
    		+ "                               ATT.ACTIVITY_ID\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'poNbr' THEN ATT.ATTRIBUTE_VALUE END AS PO_NBR\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'poRcptNbr' THEN ATT.ATTRIBUTE_VALUE END AS PO_RCPT_NBR\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'sequence' THEN ATT.ATTRIBUTE_VALUE END AS SEQUENCE\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'subType' THEN ATT.ATTRIBUTE_VALUE END AS SUB_TYPE\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'pid' THEN ATT.ATTRIBUTE_VALUE END AS PID\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'locationNbr' THEN ATT.ATTRIBUTE_VALUE END AS LOCN_NUMBER\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'cartonNbr' THEN ATT.ATTRIBUTE_VALUE END AS CARTON_NUMBER\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'LocationSequence' THEN ATT.ATTRIBUTE_VALUE END AS LOCN_SEQUENCE\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'processArea' THEN ATT.ATTRIBUTE_VALUE END AS PROCESS_AREA\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'prepInstructions' THEN ATT.ATTRIBUTE_VALUE END AS PREP_INSTRUCTIONS\n"
    		+ "                               , CASE WHEN DEF.DESCRIPTION = 'waveNumber' THEN ATT.ATTRIBUTE_VALUE END AS WAVE_NUMBER\n"
    		+ "                             FROM WSM.ACTIVITY_ATTRIBUTES ATT JOIN WSM.ATTRIBUTES_DEFINITION DEF ON ATT.ATTRIBUTE_ID = DEF.ID WHERE ATT.ATTRIBUTE_VALUE NOT LIKE '%%\"%%') ATTR2\n"
    		+ "                        ON ATTR1.ACTIVITY_ID = ATTR2.ACTIVITY_ID\n"
    		+ "                    GROUP BY ATTR1.ACTIVITY_ID, ATTR1.ATTRIBUTES\n"
    		+ "                ) ATTR\n"
    		+ "                ON ACT.ID = ATTR.ACTIVITY_ID\n"
    		+ "        WHERE\n"
    		+ "            ((ACT.ENABLED = 1 AND LIFE.ENABLED = 1) OR (ACT.ENABLED IS NULL AND LIFE.ENABLED IS NULL AND STAT.STATUS_CD = 'DELETED'))\n"
    		+ "            %s\n"
    		+ "        ORDER BY\n"
    		+ "            'Activity ID' DESC LIMIT 100\n";

   }

