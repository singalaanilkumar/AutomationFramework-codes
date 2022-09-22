package com.macys.mst.DC2.EndToEnd.model;

public class ShippingRequestWeightObject {

	private Double actualWeight;
	private String uom;

	public Double getActualWeight() {
		return actualWeight;
	}

	public void setActualWeight(Double actualWeight) {
		this.actualWeight = actualWeight;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	@Override
	public String toString() {
		return "{actualWeight:" + actualWeight + ", uom:" + uom + "}";
	}




}
