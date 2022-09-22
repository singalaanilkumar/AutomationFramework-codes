package com.macys.mst.DC2.EndToEnd.stepdefinitions;

import com.macys.mst.artemis.jms.JMSUtils;
import com.macys.mst.artemis.jms.TibjmsSSLConnection;
import com.macys.mst.artemis.testNg.TestNGListener;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.*;
import org.junit.Assert;

import java.util.concurrent.ConcurrentHashMap;


public class TibcoSSLTest {
	
	Logger logger = Logger.getLogger(TibcoSSLTest.class.getName());
	public  long TestNGThreadID = Thread.currentThread().getId();
	//public TibjmsSSLConnection tibcoSSL;
	
	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = TestNGListener.EnvMap.get(TestNGThreadID);
		TestNGListener.EnvMap.put((Thread.currentThread().getId()), obj);
	}
	
	TibjmsSSLConnection tibcoSSL = new TibjmsSSLConnection();
	
	@Given("parameters to connect to Tibco SSL server")
	public void instantiateParameters() {
		try {		
			TibjmsSSLConnection.getinstance();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@When("user publishes JMS message to a topic")
	public void publishMessageToTopic(@Named("TopicName") String topicName) {
		try {
			
		    tibcoSSL.publishTopicMessage(topicName, JMSUtils.message,null);
		}		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Then("message shall be received when subscribed to topic")
	public void subscribeMessageFromTopic(@Named("TopicName") String topicName) {
		try {
		
		String message = tibcoSSL.subscribeTopicMessage(topicName);
		Assert.assertTrue(message.equals(JMSUtils.message));
		}		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	

}

