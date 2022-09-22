package com.macys.mst.DC2.EndToEnd.db.app;

public class SQLWave {

    public static final String WAVEDashboard_Default ="SELECT   WVE.WAVE_NBR as 'Wave No',WVE.ID,WVE.TOTAL_QTY AS ORDER_DEMAND,WVE.ORDER_COUNT AS ORDER_COUNT,WVE.CONTAINER_COUNT AS CONTAINER_COUNT,WVE.CREATED_BY,WVE.STATUS,WVE.STATUS_DESC,WVE.CREATED_TIME,WVE.SHIP_OUT_START_DT AS OLDEST_SHIP_DATE,WVE.FLOW_TYPE,CFG.WAVE_TYPE,LC.STATUS AS LIFE_CYCLE_STATUS \n" +
            "            FROM ORDERSELECTION.WAVE WVE\n" +
            "            INNER JOIN ORDERSELECTION.WAVE_CONFIG CFG ON CFG.ID = WVE.WAVE_TYPE_CONFIG_ID\n" +
            "            INNER JOIN ORDERSELECTION.WAVE_LIFE_CYCLE LC  ON LC.WAVE_ID = WVE.ID WHERE LC.ENABLED = 1 AND WAVE_NBR IS NOT NULL AND WAVE_NBR != 0\n" +
            "            AND WVE.STATUS NOT IN ('COMPLETED','CANCEL')\n" +
            "            GROUP BY WVE.ID, LC.STATUS\n" +
            "            ORDER BY WVE.WAVE_NBR DESC LIMIT 10;";


    public static final String WAVE_DASHBOARD_INVENTORY_DETAILS_QUERY="SELECT\n" +
            "Wave,\n" +
            "ProcessArea,\n" +
            "COUNT(CONTAINER) AS CONTAINER_COUNT,\n" +
            "SUM(QTY) AS QTY,\n" +
            "ENTITY_STATUS,\n" +
            "ENTITY_TYPE FROM\n" +
            "(SELECT CEA.ATTRIBUTE_VALUE AS Wave,\n" +
            " CEA1.ATTRIBUTE_VALUE AS ProcessArea,\n" +
            "CONTAINER,\n" +
            "SUM(QUANTITY) AS QTY,\n" +
            "ENT1.ENTITY_STATUS, ENT1.ENTITY_TYPE\n" +
            "FROM INVENTORY.INVENTORY_SNAPSHOT INV\n" +
            "INNER JOIN INVENTORY.ENTITY ENT ON ENT.ENTITY_ID = INV.ID\n" +
            "INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES CEA ON CEA.ENTITY_ID = ENT.ID\n" +
            "INNER JOIN INVENTORY.ENTITY ENT1 ON ENT1.ENTITY_ID = INV.CONTAINER\n" +
            "INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES CEA1 ON CEA1.ENTITY_ID = ENT1.ID\n" +
            "WHERE CEA.ATTRIBUTE_NAME IN  ('waveNumber')\n" +
            "AND CEA.ATTRIBUTE_VALUE IN (:waveNumber) \n" +
            "AND CEA1.ATTRIBUTE_NAME = 'ProcessArea'\n" +
            "AND INV.ENABLED = 1\n" +
            "AND ENT.ENABLED = 1\n" +
            "AND ENT1.ENABLED = 1\n" +
            "AND CEA.ENABLED = 1\n" +
            "AND CEA1.ENABLED = 1\n" +
            "GROUP BY Wave, ProcessArea, CONTAINER, ENT1.ENTITY_STATUS, ENT1.ENTITY_TYPE\n" +
            "ORDER BY Wave DESC) INV\n" +
            "GROUP BY Wave, ProcessArea, ENTITY_STATUS,ENTITY_TYPE\n" +
            "HAVING MAX(CASE WHEN ENTITY_STATUS = 'VSC' AND ENTITY_TYPE NOT IN ('TOTE') THEN 1 ELSE 0 END) = 0;\n";


    public static final String WAVE_DASHBOARD="SELECT Wave as 'Wave No'\n" +
            "    ,WAVE_TYPE as 'Wave Type'\n" +
            "    ,CREATED_BY as 'Created By'\n" +
            "    ,CASE STATUS WHEN 'Packing In Progress' THEN 'Packing in Progress' END AS 'Status'\n" +
            "    ,substr(CREATED_TIME,11) as 'Wave Activation'\n" +
            "    ,OLDEST_SHIP_DATE as 'Oldest Ship by Date'\n" +
            "    ,ORDER_DEMAND as 'Demand'\n" +
            "    ,ProcessArea as 'Process Area'\n" +
            "    ,SUM(PICKED_UNITS) AS Pickedvalue\n" +
            "    ,ROUND(SUM(PICKED_UNITS) / SUM(ORDER_DEMAND) * 100) AS Picked\n" +
            "    ,SUM(STAGED_UNITS) AS Stagedvalue\n" +
            "    ,ROUND(SUM(STAGED_UNITS) / SUM(ORDER_DEMAND) * 100) AS Staged\n" +
            "    ,SUM(SORTED_UNITS) AS SortedUnitsvalue\n" +
            "    ,ROUND(SUM(SORTED_UNITS) / SUM(ORDER_DEMAND) * 100) AS 'Sorted Units'\n" +
            "    ,SUM(SORT_REMAIING_CONTAINER_COUNT) AS 'Sorted Remaining containers'\n" +
            "    ,SUM(PUT_UNITS) AS PutUnitsvalue\n" +
            "    ,ROUND(SUM(PUT_UNITS) / SUM(ORDER_DEMAND) * 100) AS 'Put Units'\n" +
            "    ,SUM(PUT_REMAIING_CONTAINER_COUNT) AS 'PUT Remaining containers'                \n" +
            "    ,SUM(PACKED_UNITS) AS Packedvalue\n" +
            "\t,ROUND(SUM(PACKED_UNITS) / SUM(ORDER_DEMAND) * 100) AS Packed\n" +
            "    ,SUM(SHIPPED_UNITS) AS Shippedvalue\n" +
            "    ,ROUND(SUM(SHIPPED_UNITS) / SUM(ORDER_DEMAND) * 100) AS Shipped\n" +
            "FROM (SELECT Wave\n" +
            "        ,ProcessArea\n" +
            "        ,ENTITY_TYPE\n" +
            "        ,ORDER_DEMAND\n" +
            "        ,CREATED_BY\n" +
            "        ,STATUS\n" +
            "        ,CREATED_TIME\n" +
            "        ,OLDEST_SHIP_DATE\n" +
            "        ,WAVE_TYPE\n" +
            "        ,LIFE_CYCLE_STATUS\n" +
            "        ,SUM(QTY) AS QTY\n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('PIK','SPK','PSIP','SIP','PREP','VSC') AND ENTITY_TYPE in ('BINBOX','TOTE') THEN COUNT(DISTINCT CONTAINER) ELSE 0 END AS SORT_REMAIING_CONTAINER_COUNT\n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('PIK','SPK','PSIP','SIP','PREP','VSC','SRT','PTS') AND ENTITY_TYPE in ('BINBOX','TOTE') THEN COUNT(DISTINCT CONTAINER) ELSE 0 END AS PUT_REMAIING_CONTAINER_COUNT\n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('RSV') AND ENTITY_TYPE = 'BINBOX' THEN SUM(QTY) ELSE 0 END AS NOT_PICKED_UNITS\n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('PIK','SPK','PSIP','SIP','SRT', 'PREP','VSC' ,'PTS','IPK','PCK','WGH','MFT','SHR','SHP') AND ENTITY_TYPE in ('BINBOX','TOTE','CRT') THEN SUM(QTY) ELSE 0 END AS PICKED_UNITS\n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('SPK','PSIP','SIP','SRT','PREP','VSC','PTS','IPK','PCK','WGH','MFT','SHR','SHP') AND ENTITY_TYPE in ('BINBOX','TOTE','CRT') THEN SUM(QTY) ELSE 0 END AS STAGED_UNITS\n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('SRT','PREP','VSC','PTS','IPK','PCK','WGH','MFT','SHR','SHP') AND ENTITY_TYPE in ('BINBOX','TOTE','CRT') THEN SUM(QTY) ELSE 0 END AS SORTED_UNITS  \n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('PTS','IPK','PCK','WGH','MFT','SHR','SHP') AND ENTITY_TYPE in ('BINBOX','TOTE','CRT') THEN SUM(QTY) ELSE 0 END AS PUT_UNITS  \n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('PCK','WGH','MFT','SHR','SHP') AND ENTITY_TYPE='CRT' THEN SUM(QTY) ELSE 0 END AS PACKED_UNITS\n" +
            "        ,CASE WHEN ENTITY_STATUS IN ('SHP') AND ENTITY_TYPE='CRT' THEN SUM(QTY) ELSE 0 END AS SHIPPED_UNITS\n" +
            "FROM (SELECT CEA.ATTRIBUTE_VALUE AS Wave\n" +
            "        ,CEA1.ATTRIBUTE_VALUE AS ProcessArea\n" +
            "        ,WVE.CREATED_BY\n" +
            "        ,CONTAINER\n" +
            "        ,QUANTITY AS QTY\n" +
            "        ,ENT1.ENTITY_STATUS\n" +
            "        ,ENT1.ENTITY_TYPE\n" +
            "        ,WVE.TOTAL_QTY AS ORDER_DEMAND\n" +
            "        ,CS.STATUS AS STATUS\n" +
            "        ,WVE.CREATED_TIME\n" +
            "        ,WVE.SHIP_OUT_START_DT AS OLDEST_SHIP_DATE\n" +
            "        ,CFG.WAVE_TYPE\n" +
            "        ,CASE WHEN LC.STATUS = 'REQ' THEN 'NOT_STARTED' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'ALC' THEN 'NOT_STARTED' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'RLS' THEN 'NOT_STARTED' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'PIKIP' THEN 'IN_PROGRESS' ELSE                                             \n" +
            "         CASE WHEN LC.STATUS = 'PIK' THEN 'IN_PROGRESS' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'SPK' THEN 'IN_PROGRESS' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'PSIP' THEN 'IN_PROGRESS' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'PSRT' THEN 'IN_PROGRESS' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'IPK' THEN 'IN_PROGRESS' ELSE\n" +
            "         CASE WHEN LC.STATUS = 'PCK' THEN 'IN_PROGRESS' ELSE\n" +
            "         CASE WHEN LC.STATUS = 'SHPIP' THEN 'IN_PROGRESS' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'PSR' THEN 'IN_PROGRESS' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'PTSR' THEN 'IN_PROGRESS' ELSE          \n" +
            "         CASE WHEN LC.STATUS = 'PTSIP' THEN 'IN_PROGRESS' ELSE\n" +
            "         CASE WHEN LC.STATUS = 'SHP' THEN 'COMPLETE' ELSE \n" +
            "         CASE WHEN LC.STATUS = 'FAIL' THEN 'FAILED' ELSE \n" +
            "         LC.STATUS END END END END END END END END END END END END END END END END AS LIFE_CYCLE_STATUS\n" +
            "         FROM INVENTORY.INVENTORY_SNAPSHOT INV\n" +
            "         INNER JOIN INVENTORY.ENTITY ENT ON ENT.ENTITY_ID = INV.ID\n" +
            "         INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES CEA ON CEA.ENTITY_ID = ENT.ID\n" +
            "         INNER JOIN INVENTORY.ENTITY ENT1 ON ENT1.ENTITY_ID = INV.CONTAINER\n" +
            "         INNER JOIN INVENTORY.COMMON_ENTITY_ATTRIBUTES CEA1 ON CEA1.ENTITY_ID = ENT1.ID\n" +
            "         INNER JOIN ORDERSELECTION.WAVE WVE ON CEA.ATTRIBUTE_VALUE = WVE.WAVE_NBR\n" +
            "         INNER JOIN ORDERSELECTION.WAVE_CONFIG CFG ON CFG.ID = WVE.WAVE_TYPE_CONFIG_ID\n" +
            "         INNER JOIN ORDERSELECTION.WAVE_LIFE_CYCLE LC ON LC.WAVE_ID = WVE.ID\n" +
            "         INNER JOIN INVENTORY.container_status CS ON LC.STATUS=CS.STATUS_CODE\n" +
            "         WHERE CEA.ATTRIBUTE_NAME IN ('waveNumber')\n" +
            "            AND CEA1.ATTRIBUTE_NAME = 'ProcessArea'\n" +
            "            AND INV.ENABLED = 1\n" +
            "            AND ENT.ENABLED = 1\n" +
            "            AND ENT1.ENABLED = 1\n" +
            "            AND CEA.ENABLED = 1\n" +
            "            AND CEA1.ENABLED = 1\n" +
            "            AND LC.ENABLED = 1\n" +
            "            AND WVE.WAVE_NBR = '%s') INV\n" +
            "                WHERE ENTITY_STATUS NOT IN ('NRCVD','CRE','PTW')\n" +
            "                GROUP BY Wave, ProcessArea, ENTITY_TYPE, ENTITY_STATUS,ORDER_DEMAND,CREATED_BY,STATUS,CREATED_TIME,OLDEST_SHIP_DATE,WAVE_TYPE,LIFE_CYCLE_STATUS\n" +
            "                HAVING MAX(CASE WHEN ENTITY_STATUS = 'VSC' AND ENTITY_TYPE NOT IN ('TOTE') THEN 1 ELSE 0 END) = 0) T1\n" +
            "                GROUP BY Wave, ProcessArea, ENTITY_TYPE, ORDER_DEMAND,CREATED_BY,STATUS,CREATED_TIME,OLDEST_SHIP_DATE,WAVE_TYPE,LIFE_CYCLE_STATUS;\n";
}
