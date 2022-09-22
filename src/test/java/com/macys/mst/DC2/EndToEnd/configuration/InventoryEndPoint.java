package com.macys.mst.DC2.EndToEnd.configuration;

import com.macys.mst.artemis.config.FileConfig;

public class InventoryEndPoint {

    public static final String Inventory_Hostname= FileConfig.getInstance().getStringConfigValue("services.hostnameForInventory");
    public static final String Inventory= FileConfig.getInstance().getStringConfigValue("services.inventory");
    public static final String Containers= FileConfig.getInstance().getStringConfigValue("services.ContainerServices");
    public static final String LOCATION_NBR= FileConfig.getInstance().getStringConfigValue("services.locationNbr");
    public static final String InventoryGetCallWithAttributes= Inventory_Hostname+Inventory+"/%s/containers/inventory?PO=%s&POReceipt=%s&containerStatusCode=%s&containerType=%s";
    public static final String InventoryWithContainerBarcode= Inventory_Hostname+Inventory+"/%s/containers?barcode=%s";
    public static final String CreateContainer= Inventory_Hostname+Containers+"/%s";
    public static final String RetrieveContainer= Inventory_Hostname+Inventory+"/%s"+"/containers";
    public static final String GET_INVENTORY_PORECEIPT_PONBR = Inventory_Hostname+Inventory+"/"+LOCATION_NBR+"/containers/inventory?poNbr=%s&POReceipt=%s";
    public static final String CONSUME_TOTE = Inventory_Hostname+Inventory+"/%s/containers/%s?reasonCode=%s";

}
