package com.macys.mst.DC2.EndToEnd.configuration;

import java.util.HashMap;
import java.util.Map;

public class ProdUrls {

	private static Map<String,String> urlMap;
	
	static
	{
		urlMap = new HashMap();
		
		urlMap.put("ApptIntegration", "	http://lp000xsmms0001:8780/msp-whm-ApptIntegration-service/actuator/info");
		urlMap.put("BuyIntegration", "http://lp000xsmms0001:8580/msp-whm-BuyIntegration-service/actuator/info");
		urlMap.put("LocnIntegration", "http://lp000xsmms0001:8880/msp-whm-locnintegration-service/actuator/info");
		urlMap.put("RcvIntegration", "http://lp000xsmms0001:8680/msp-whm-RcvIntegration-service/actuator/info");
		urlMap.put("CebIntegration", "http://lp000xsmms0001:8980/msp-whm-cebintegration-service/actuator/info");
		urlMap.put("SortationIntegration", "http://lp000xsmms0001:8380/msp-whm-sortationintegration-service/actuator/info");
		urlMap.put("AuthSecurity", "https://lp000xswhm0001:8080/security/actuator/info");
		
	}
	
	public static String getProdServiceURL(String serviceName) {
		return urlMap.get(serviceName);
	}
}
