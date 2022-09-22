package com.macys.mst.Atlas.utilmethods;

import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.artemis.reports.StepDetail;
import com.macys.mst.artemis.selenium.LocalDriverManager;
import com.macys.mst.artemis.selenium.WebDriverListener;
import com.macys.mst.wavefunction.activespace.ASOperations;
import com.macys.mst.wavefunction.activespace.MetaspaceConnection;
import com.macys.mst.wavefunction.activespace.QueryHelper;
import com.macys.mst.wavefunction.db.app.DBMethods;
import com.macys.mst.wavefunction.db.app.DBMetods;
import com.macys.mst.wavefunction.sqlconstants.Constants;
import com.macys.mst.wavefunction.stepdefinitions.WMSASSteps;
import com.macys.mst.Atlas.db.app.SQLQueriesMA;
import com.tibco.as.space.ASException;
import com.tibco.as.space.Space;
import com.tibco.as.space.Tuple;
import org.apache.log4j.Logger;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.model.ExamplesTable;
import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GenericConfigUtils {

	public static WebDriver driver = LocalDriverManager.getInstance().getDriver();
	private final Logger logger = Logger.getLogger(GenericConfigUtils.class.getName());
	public static String member = FileConfig.getInstance().getStringConfigValue("activespace.member.name"); 
	public static String activeSpaceWait=FileConfig.getInstance().getStringConfigValue("activespace.session"); 
	public static String activeSpaceServer=FileConfig.getInstance().getStringConfigValue("activespace.server.url");
	public static MetaspaceConnection ms = null;

	public long TestNGThreadID = Thread.currentThread().getId();
	@BeforeStory
	public void beforeStory() {
		ConcurrentHashMap<String,String> obj = WebDriverListener.EnvMap.get(TestNGThreadID);
		WebDriverListener.EnvMap.put((Thread.currentThread().getId())	, obj);
		WMSASSteps.setDriverForE2E(driver);
	}
	public void genericConfigLogicalGrp(String rule,String overRide,ExamplesTable parametersTable) throws Exception{
		logger.info("Setting generic configuration parameters for PROTEUS/ARRANGER/LOWPICKALERT" );
		/* Getting row Id */	
		String getRowIdQuery = SQLQueriesMA.getConfigForLogicalGrp.replace("${colname1}","OVER_RIDE_LIST").replace("${colname2}","RULE_NAME");
		    getRowIdQuery=getRowIdQuery.replace("${genConfig}", "PROTEUS/ARRANGER/LOWPICKALERT");
			getRowIdQuery=getRowIdQuery.replace("${overrideValue}",overRide);	
			getRowIdQuery=getRowIdQuery.replace("${ruleValue}",rule);	
			String rowID = DBMethods.getDBValueInString(getRowIdQuery);
    		logger.info("getRowIdQuery: " + getRowIdQuery);
			logger.info("rowID: "+rowID);
			List<Map<String,String>> exRows=parametersTable.getRows();
			exRows.stream().forEach(m ->{
				m.forEach((k,v)->{
					/* Getting Config ID*/
					String getConfigQuery = Constants.Select.Config_id_CONSTRAINT_TYPE.replace("${CONSTRAINT_TYPE}","'"+k+"'");
					getConfigQuery = getConfigQuery.replace("${genConfigCode}", "'PROTEUS/ARRANGER/LOWPICKALERT'");
					String ConfigID = DBMethods.getDBValueInString(getConfigQuery);
					logger.info("getConfigQuery: " + getConfigQuery);
					logger.info("ConfigID: "+ConfigID);
					
					/* Getting Config value*/
					String getConfigValueQuery1 = Constants.Select.Config_value_CONSTRAINT_TYPE.replace("${Config_id_CONSTRAINT_TYPE}", "'"+ConfigID+"'");
					getConfigValueQuery1 = getConfigValueQuery1.replace("${Config_Row_ID}", "'"+rowID+"'");
					String ConfigValue = DBMethods.getDBValueInString(getConfigValueQuery1);
					logger.info("getConfigValueQuery1: " + getConfigValueQuery1);
					logger.info("ConfigValue: "+ConfigValue);
					if(!v.equalsIgnoreCase(ConfigValue) && rowID!=null){
						String setConfigValueQuery= Constants.Update.Update_Config_Values.replace("${configId}",  "'"+ConfigID+"'");
						setConfigValueQuery = setConfigValueQuery.replace("${confidRowID}","'"+rowID+"'" );
						setConfigValueQuery = setConfigValueQuery.replace("${configValue}", "'"+v+"'");
						DBMetods.deleteOrUpdateWMSDataBase(setConfigValueQuery);
						logger.info("setConfigValueQuery: " + setConfigValueQuery);
						logger.info("Update is done with value "+v);	
					}else {
							logger.info("Config value for "+k+" is "+ConfigValue);
						}
				});
			});
	}
	
	public void genericConfigReleasePackWave(String chuteType,ExamplesTable parametersTable) throws Exception{
		String getRowIdQuery = Constants.Select.Confid_Row_ID.replace("${configValue}","'"+chuteType+"'");	
	    getRowIdQuery=getRowIdQuery.replace("${genConfigCode}", "'PROTEUS/WAVE/AUTOSETTINGS/RELASEPACKWAVE'");
		getRowIdQuery=getRowIdQuery.replace("${colName}","'CHUTE_TYPE_GRP'");		
		String rowID = DBMethods.getDBValueInString(getRowIdQuery);
		logger.info("getRowIdQuery: " + getRowIdQuery);
		logger.info("rowID: "+rowID);
		List<Map<String,String>> exRows=parametersTable.getRows();
		exRows.stream().forEach(m ->{
			m.forEach((k,v)->{
				/* Getting Config ID*/
				String getConfigQuery = Constants.Select.Config_id_CONSTRAINT_TYPE.replace("${CONSTRAINT_TYPE}","'"+k+"'");
				getConfigQuery = getConfigQuery.replace("${genConfigCode}", "'PROTEUS/WAVE/AUTOSETTINGS/RELASEPACKWAVE'");
				String ConfigID = DBMethods.getDBValueInString(getConfigQuery);
				logger.info("getConfigQuery: " + getConfigQuery);
				logger.info("ConfigID: "+ConfigID);
				
				/* Getting Config value*/
				String getConfigValueQuery1 = Constants.Select.Config_value_CONSTRAINT_TYPE.replace("${Config_id_CONSTRAINT_TYPE}", "'"+ConfigID+"'");
				getConfigValueQuery1 = getConfigValueQuery1.replace("${Config_Row_ID}", "'"+rowID+"'");
				String ConfigValue = DBMethods.getDBValueInString(getConfigValueQuery1);
				logger.info("getConfigValueQuery1: " + getConfigValueQuery1);
				logger.info("ConfigValue: "+ConfigValue);
				if(!v.equalsIgnoreCase(ConfigValue) && rowID!=null){
					String setConfigValueQuery= Constants.Update.Update_Config_Values.replace("${configId}",  "'"+ConfigID+"'");
					setConfigValueQuery = setConfigValueQuery.replace("${confidRowID}","'"+rowID+"'" );
					setConfigValueQuery = setConfigValueQuery.replace("${configValue}", "'"+v+"'");
					DBMetods.deleteOrUpdateWMSDataBase(setConfigValueQuery);
					logger.info("setConfigValueQuery: " + setConfigValueQuery);
					logger.info("Update is done with value "+v);	
				}else {
						logger.info("Config value for "+k+" is "+ConfigValue);
					}
			});
		});
	}
	
	public void genConfigForLogicalGrp(String colName,String rule,String overRide) throws Exception{
		logger.info("Setting generic configuration parameters for PROTEUS/ARRANGER/LOWPICKALERT" );
		/* Getting row Id */	
		String getRowIdQuery = SQLQueriesMA.getConfigForLogicalGrp.replace("${colname1}","OVER_RIDE_LIST").replace("${colname2}","RULE_NAME");
		    getRowIdQuery=getRowIdQuery.replace("${genConfig}", "PROTEUS/ARRANGER/LOWPICKALERT");
			getRowIdQuery=getRowIdQuery.replace("${overrideValue}",overRide);	
			getRowIdQuery=getRowIdQuery.replace("${ruleValue}",rule);	
			String rowID = DBMethods.getDBValueInString(getRowIdQuery);
    		logger.info("getRowIdQuery: " + getRowIdQuery);
			logger.info("rowID: "+rowID);
			/* Getting Config ID*/
			String getConfigQuery = Constants.Select.Config_id_CONSTRAINT_TYPE.replace("${CONSTRAINT_TYPE}","'"+colName+"'");
			getConfigQuery = getConfigQuery.replace("${genConfigCode}", "'PROTEUS/ARRANGER/LOWPICKALERT'");
			String ConfigID = DBMethods.getDBValueInString(getConfigQuery);
			logger.info("getConfigQuery: " + getConfigQuery);
			logger.info("ConfigID: "+ConfigID);
			/* Getting Config value*/
			String getConfigValueQuery1 = Constants.Select.Config_value_CONSTRAINT_TYPE.replace("${Config_id_CONSTRAINT_TYPE}", "'"+ConfigID+"'");
			getConfigValueQuery1 = getConfigValueQuery1.replace("${Config_Row_ID}", "'"+rowID+"'");
			int ConfigValue = DBMethods.getDBValueInteger(getConfigValueQuery1);
			logger.info("getConfigValueQuery1: " + getConfigValueQuery1);
			logger.info("ConfigValue: "+ConfigValue);
			int unitsFromAS = getValuesFromActiveSpace(overRide);
			if(ConfigValue>unitsFromAS){
				ConfigValue++;
				String setConfigValueQuery= Constants.Update.Update_Config_Values.replace("${configId}",  "'"+ConfigID+"'");
				setConfigValueQuery = setConfigValueQuery.replace("${confidRowID}","'"+rowID+"'" );
				setConfigValueQuery = setConfigValueQuery.replace("${configValue}", "'"+ConfigValue+"'");
				DBMetods.deleteOrUpdateWMSDataBase(setConfigValueQuery);
				logger.info("setConfigValueQuery: " + setConfigValueQuery);
				logger.info("Update is done with value "+ConfigValue);
			}
	}
	
	public int getValuesFromActiveSpace(String override){
		logger.info(ASOperations.getMetaspace());
		  int value = 0;
		  Map<String,String> values = new HashMap<>();
		  List<Map<String,String>> listFromAS = new ArrayList<>();
		  StepDetail.addDetail("prefix:"+override+"", true);
		  String filter = "OVERRIDE LIKE '"+override+"'";
		  logger.info("the filter is    " + filter);
		  listFromAS = getActiveSpace("D2CWMS_WA_INT", "PICK_AREA_STATE", filter);
		  values = listFromAS.get(0);
		  for(Map<String,String> e:listFromAS){
			  value += Integer.parseInt(e.get("REL_TASKS"));
		  }
		  return value;
	}
		 
	public List<Map<String,String>>  getActiveSpace(String metaSpace,String Spaces,String filter) {
		  Random random = new Random();
		  int randInt = random.nextInt();
		  String memberName= member+String.valueOf(randInt);
		  long activeSpaceSession=Long.parseLong(activeSpaceWait); 
		  Space space=null;
		  Tuple[] tuple = null;
		  List<Map<String,String>> listFromAS = new ArrayList<>();
		  
		  try{
		   logger.info("Getting the metaspace connection");
		   ms=new MetaspaceConnection(metaSpace, memberName,activeSpaceServer,true,"",activeSpaceSession);
		   StepDetail.addDetail("Space:"+Spaces+"", true);
		   space = ms.getSpace(metaSpace,Spaces);
		   tuple = QueryHelper.query(metaSpace, Spaces, filter);
		   Map<String,String> map =null;
		   for (int i = 0; i < tuple.length; i++) {
			   map = new HashMap<>();
		    for(Map.Entry<String, Object> entry:tuple[i].entrySet()){
		     if(entry.getValue() != null){
		    	 map.put(entry.getKey(), entry.getValue().toString());
		     }
		    }
		    listFromAS.add(map);
		    logger.info(listFromAS);
		   }
		  }catch(Exception e){
		   logger.info("Exception while connecting to active spave"+e.getMessage());
		   e.printStackTrace();
		  }finally{
		   try {
		    space.close();
		    ms.closeMetaSpace(ASOperations.getMetaspace());
		   } catch (ASException e) {
		    e.printStackTrace();
		   }
		  }
		  logger.info("List of records from Active space"+listFromAS);
		  return listFromAS;
		 }
		 
}
