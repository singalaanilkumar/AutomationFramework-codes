package com.macys.mst.DC2.EndToEnd.model;

public class ShippingRequestDimensionObject {
	private Double length;
	private Double width;
	private Double height;
	private Double volume;
	private Double weight;
	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	private String UOM;
	private String weightUom;

	public String getWeightUom() {
		return weightUom;
	}

	public void setWeightUom(String weightUom) {
		this.weightUom = weightUom;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public Double getWidth() {
		return width;
	}

	public void setWidth(Double width) {
		this.width = width;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getUOM() {
		return UOM;
	}

	public void setUOM(String uOM) {
		UOM = uOM;
	}

	@Override
	public String toString() {
		return "{length:" + length + ", width:" + width + ", height:" + height
				+ ", volume:" + volume + ", UOM:" + UOM + "}";
	}

	
}
