package com.macys.mst.DC2.EndToEnd.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macys.mst.artemis.rest.RestUtilities;

public class LocationService {
    
    /**
     * @param barCode
     * @return
     */
    public HashMap<String, String> callLocationService(String barCode) {
        String locationServiceUrl = ReadHostConfiguration.LOCATION_SERVICE_HOST.value().replace("{barcode}", barCode);
        String response = RestUtilities.getRequestResponse(locationServiceUrl);
        HashMap<String, String> returnMap = mapLocationServiceResponse(response);
        return returnMap;
    }
    
    /**
     * @param response
     * @return
     */
//    @SuppressWarnings("unchecked")
    private HashMap<String, String> mapLocationServiceResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, String> returnMap = new HashMap<String, String>();
        try {
            Map<String, Object> mapObject = mapper.readValue(response, new TypeReference<Map<String, Object>>(){});
            Map<String, String> messageObj = (Map<String, String>) mapObject.get("message");
            String description = messageObj.get("description");
            Map<String, Object> dataObj = (Map<String, Object>)mapObject.get("data");
            Map<String, String> barcodeDetail = (Map<String, String>)dataObj.get("barcodeDetail");
//            jsonObj = new JSONObject(mapObject);
            
            List<Map<String, Object>> attributes = (List<Map<String, Object>>) dataObj.get("attributes");
            attributes.forEach(action->{
                Map<String, Object> obj = action;
                if(obj.get("key").equals("StorageType")) {
                    List<String> valueList = (List<String>)obj.get("values");
                    String value = valueList.get(0);
                    returnMap.put("StorageTypeKey",(String) obj.get("key"));
                    returnMap.put("StorageTypevalue",value);
                    
                }
            });
            
//            Map<String, String> attributes = (Map<String, String>)dataObj.get("attributes");
//            Map<String, String> storageType = (Map<String, String>)attributes.get("Storage Type");
            String locationType = barcodeDetail.get("locationType");
            String locationCategory = barcodeDetail.get("locationCategory");
//            String storageType = attributes.get(attributes);
            returnMap.put("description", description);
            returnMap.put("locationType", locationType);
            returnMap.put("locationCategory", locationCategory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMap;
    }

}
