package com.macys.mst.WMSLite.EndToEnd.utilmethods;

public class Constants {
	
	// location related constants
	public static final String STORAGE_TYPE = "StorageType";
	public static final String BARCODE = "barcode";
	public static final String HALF_PALLET = "Half Pallet";
	public static final String BINBOX = "BINBOX";
	public static final String FULL_PALLET = "Full Pallet";
	public static final String SORT_ZONE = "Sort Zone";
	public static final String STORAGE_TYPE_VALUE = "StorageTypevalue";
		
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
}
