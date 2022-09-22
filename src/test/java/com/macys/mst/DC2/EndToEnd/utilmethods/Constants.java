package com.macys.mst.DC2.EndToEnd.utilmethods;

public class Constants {

	public static final String PO = "PO";
	public static final String POReceipt = "POReceipt";
	// location related constants
	public static final String STORAGE_TYPE = "StorageType";
	public static final String BARCODE = "barcode";
	public static final String HALF_PALLET = "Half Pallet";
	public static final String BINBOX = "BINBOX";
	public static final String FULL_PALLET = "Full Pallet";
	public static final String SORT_ZONE = "Sort Zone";
	public static final String STORAGE_TYPE_VALUE = "StorageTypevalue";

	// PO related constants
	public static final String PO_NBR = "poNbr";
	public static final String PO_RECEIPT = "poRcpt";
	public static final String PID = "pid";
	public static final String POLINE_BARCODE = "poLineBarCode";
	public static final String SKU_UPC = "skuUpc";
	
	public static final String PROD_VERSION = "PROD_VERSION";
		
	public enum PackType {
		IP, CP, SK
	}
	
	public enum ContainerType {
		PLT("pallet"), LCN("location"), BINBOX("binbox"), TOTE("tote"), CRT("carton");

		private String description;

		private ContainerType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return this.description;
		}
		
		public static ContainerType valueFromDescription(String description) {
			for (ContainerType c : ContainerType.values()) {
				if (c.description.equalsIgnoreCase(description))
					return c;
			}
			return null;
		}
	}

	public enum SortMode{
		SKU,PO,PID,POReceipt
	}
}
