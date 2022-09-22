package com.macys.mst.DC2.EndToEnd.model;

public class ShippingRequestPayloadObject {
	private String packageNumber;
	private ShippingRequestDimensionObject dimension;
	private String storeNumber;
	private String department;

	public String getPackageNumber() {
		return packageNumber;
	}

	public void setPackageNumber(String packageNumber) {
		this.packageNumber = packageNumber;
	}

	public ShippingRequestDimensionObject getDimension() {
		return dimension;
	}

	public void setDimension(ShippingRequestDimensionObject dimension) {
		this.dimension = dimension;
	}

	
	public String getStoreNumber() {
		return storeNumber;
	}

	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return "{packageNumber:" + packageNumber + ", dimension:" + dimension + ",storeNumber:" + storeNumber + ", department:" + department + "}";
	}
	
}
