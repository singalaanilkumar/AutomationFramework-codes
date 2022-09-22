package com.macys.mst.DC2.EndToEnd.utilmethods;

import java.util.HashMap;
import java.util.Map;

public class StepsDataStore {

    private StepsDataStore(){}

    private static StepsDataStore instance=null;

    private Map<String, Object> storedData = new HashMap<>();

    public Map<String, Object> getStoredData() {
        return storedData;
    }

    public static StepsDataStore getInstance(){
        if(instance==null){
            instance=new StepsDataStore();
        }
        return instance;
    }
}
