package com.macys.mst.WMSLite.EndToEnd.configuration.EndPoints;

import com.macys.mst.artemis.config.FileConfig;

public class ConnectShipEndPoint {

	
	public static final String ReprintServiceURL = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.Reprint");
	
	public static final String ConfigurationServiceURL = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.Configuration");
	
	public static final String ShipServiceURL = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.Ship");

	public static final String PrintPostServiceURL = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.Print");

	public static final String RouteServiceURL = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.Route");

	public static final String RateServiceURL = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.Rate");
	
	public static final String ProjectID = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.projectId");
	
	public static final String Subscriptioninboundshipping= FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.shipping-inbound-shipping-test-dev");
	
	public static final String Subscriptionshippinginbounddev= FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.shipping-inbound-dev");

	public static final String SubscriptionOutboundId = FileConfig.getInstance()
			.getStringConfigValue("Connectshipservices.shipping-outbound-shipping-test-dev");

}
