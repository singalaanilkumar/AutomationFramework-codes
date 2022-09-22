package com.macys.mst.DC2.EndToEnd.db.app;

public class NetworkMapSQL {

	public static final String selectRoute = "SELECT * FROM cfnetworkmap.central_fulfillment_center_route where CFC_ID=#CFCID and ENABLED=1";
	public static final String selectNetworkRoute = "\n" +
			"Select cfcroute.* from cfnetworkmap.central_fulfillment_center_route as cfcroute inner join cfnetworkmap.central_fulfillment_center  as cfc\n" +
			"on cfc.ID = cfcroute.CFC_ID Where cfc.CFC_LOC_DESC = \"#CFC_Loc_Desc\" And cfcroute.ENABLED=1";

}
