package com.macys.mst.WMSLite.EndToEnd.utilmethods;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.macys.mst.artemis.config.FileConfig;
import com.macys.mst.whm.coreautomation.utils.RandomUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RequestUtil {

    public String requestBody;
    private static Logger logger = Logger.getLogger(RequestUtil.class.getName());
    RandomUtil randomUtil = new RandomUtil();

    private List<Map<String, String>> randomeValueMaps;

    public List<Map<String, String>> getRandomeValueMaps() {
        return randomeValueMaps;
    }

    public void setRandomeValueMaps(List<Map<String, String>> randomeValueMaps) {
        this.randomeValueMaps = randomeValueMaps;
    }

    public List<String> getRequestBody(String requestParams, String templateName){
        loadContentTemplate(templateName);
        return generateRequestBodyWithGivenInputData(getParmasToListOfMaps(requestParams));
    }

    public List<String> getRequestUrl(String requestParams, String requestUrl){
        this.requestBody =  FileConfig.getInstance().getStringConfigValue("services." + requestUrl).replace("#locNbr", FileConfig.getInstance().getStringConfigValue("warehouseLocNbr"));
        return generateRequestBodyWithGivenInputData(getParmasToListOfMaps(requestParams));
    }
    
    
    public Map<String, String> getRandomParamsfromMap(String params) {
    	Map<String, String> requestParamsMap = Maps.newHashMap(Splitter.on(",").withKeyValueSeparator(":").split(params));
    	requestParamsMap.replaceAll((key, value) -> value.replaceAll(";", ","));
        for(String key:requestParamsMap.keySet()){
        	requestParamsMap.put(key, randomUtil.getRandomValue(requestParamsMap.get(key)));
        }
    	return requestParamsMap;
    }


    public List<Map<String, String>> getParmasToListOfMaps(String requestParams) {
        List<Map<String, String>> inputData = new ArrayList<>();
        List<String> listOfRequests = Splitter.onPattern("[{}]").omitEmptyStrings().splitToList(requestParams);

        for (String request : listOfRequests) {
            if(!request.equals(",")) {
            	Map<String, String> requestParamsMap = Maps.newHashMap(Splitter.on(",").withKeyValueSeparator(":").split(request));
            	requestParamsMap.replaceAll((key, value) -> value.replaceAll(";", ","));
                inputData.add(requestParamsMap);
            }
        }

        return inputData;
    }

    public void loadContentTemplate(String template){
        String filePath = "src/test/resources/RequestTemplates/" + template;
        requestBody = getRequestBodyFromFile(filePath);
    }

    public String getRequestBodyFromFile(String fileName) {
        String body = null;
        try {
            File file = new File(fileName);
            body = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            logger.info("Request body obtained successfully from " + fileName);
        } catch (FileNotFoundException fe) {
            logger.info("Unable to find File: " + fileName);
        } catch (Exception e) {
            logger.info("Unable to get the request body from " + fileName);
        }
        return body;
    }

    public List<String> generateRequestBodyWithGivenInputData(List<Map<String, String>> inputData){

        List<String> requestList= new ArrayList<>();
        setRandomeValueMaps(new ArrayList<>());
        for(Map<String, String> row:inputData){
            Set<String> keyset = row.keySet();
            String copyOfRequest = requestBody;
            Map<String, String> randomeValueMap = new HashMap<>();
            for(String key:keyset){
                randomeValueMap.put(key, randomUtil.getRandomValue(row.get(key)));
                copyOfRequest = copyOfRequest.replace(key, randomeValueMap.get(key));
            }
            getRandomeValueMaps().add(randomeValueMap);
            requestList.add(copyOfRequest);
        }
        return requestList;
    }
}


