package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import org.jbehave.core.annotations.Given;

import com.macys.mst.DC2.EndToEnd.configuration.ProdUrls;
import com.macys.mst.DC2.EndToEnd.execdrivers.ExecutionConfig;
import com.macys.mst.DC2.EndToEnd.utilmethods.CommonUtils;
import com.macys.mst.DC2.EndToEnd.utilmethods.Constants;
import com.macys.mst.DC2.EndToEnd.utilmethods.ExpectedDataProperties;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.whm.coreautomation.rest.WhmRestCoreAutomationUtils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import jxl.common.Assert;

public class ServicesVersionVerifier {

	String executionEnv = ExecutionConfig.getExecEnv();
	CommonUtils commonUtil = new CommonUtils();
	
	
	@Given("below details validate all the services version")
	public void verifyServicesVersion(String uri, String requiredVersion) {

		String lowerEnvUrl = getFinalUrl(uri,false);
		String versionFound = WhmRestCoreAutomationUtils.getRequestResponse(lowerEnvUrl).getString("build.version");

		if (requiredVersion.equals(Constants.PROD_VERSION)) {
			String prodUrl = getFinalUrl(uri,true);
			Response prodResponse = RestAssured.given().relaxedHTTPSValidation()
					.headers(ExpectedDataProperties.getHeaderProps()).get(prodUrl);
			String prodVersion = prodResponse.jsonPath().getString("build.version");

			StepDetail.addDetail(
					"PROD_VERSION = " + prodVersion + " VERSION FOUND IN " + executionEnv + " = " + versionFound,
					versionFound.equals(prodVersion));
			
			Assert.verify(versionFound.equals(prodVersion),
					"PROD_VERSION = " + prodVersion + " VERSION FOUND IN " + executionEnv + " = " + versionFound);
		} else {
			StepDetail.addDetail("REQUIRED_VERSION = " + requiredVersion + ", VERSION FOUND IN " + executionEnv + " = "
					+ versionFound, versionFound.equals(requiredVersion));
			
			Assert.verify(versionFound.equals(requiredVersion), "REQUIRED_VERSION = " + requiredVersion
					+ ", VERSION FOUND IN " + executionEnv + " = " + versionFound);
		}

	}
	
	private String getFinalUrl(String uri, boolean isProd) {

		String prodGCPHostName = "https://msc.gcp.cloudrts.net/";
		String lowerEnvGCPHostName = commonUtil.getEnvConfigValue("services.hostName") + "/";

		if (isProd) {
			if (uri.contains("Integration") || uri.contains("Security")) {
				return ProdUrls.getProdServiceURL(uri);
			} else {
				return prodGCPHostName + uri;
			}
		} else {
			if (uri.contains("Integration") || uri.contains("Security")) {
				return commonUtil.getUrl(uri);
			} else {
				return lowerEnvGCPHostName + uri;
			}
		}
	}

}
