package com.macys.mst.DC2.EndToEnd.datasetup;

public interface DataOutputSQLConstants {

	interface OutputInstert {
		/**
		 * These SQL's are to insert data into specific data output table
		 * 
		 */
		public static final String Order_Creation_putdataSQL = "INSERT INTO AUTO_DATA_OUTPUT "
				+ " (JIRA_ISSUE_ID,SCENARIO_ID,DT_ITR_NUM,DT_ORDERS,DT_LOCATION_ID,ENV)"
				+ " VALUES ('${jiraid}','${scenarioid}','${rownum}','${ordernumbers}','${locationid}','${execenv}')";

	}

}
