package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.artemis.rest.RestUtilities;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Given;



public class RestSample {
	
	private static Logger LOGGER = Logger.getLogger(RestSample.class.getName());
	
	private String schema ="locnuser";
	private String deleteContainerResponse;
	public long TestNGThreadID = Thread.currentThread().getId();

	@Given("attributes are defined for entity")
	public void restCall(){

		LOGGER.info("Making a rest call ...");
		RestUtilities.getRequestResponse("https://dev-backstage.devops.fds.com/inventory-service/inventory/77/containers?barcode=TZEEE238948932743742384OO");
	}
	

}
