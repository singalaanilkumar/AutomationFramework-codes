package com.macys.mst.DC2.EndToEnd.model;

public class ShipInfoPoObject {

	private String tname;
	private ShippingRequestPayloadObject payload;
	private String messageType;
	private String clientId;
	private String bulkIndicator;
	private String hazmatIndicator;
	private String locnNbr;

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public ShippingRequestPayloadObject getPayload() {
		return payload;
	}

	public void setPayload(ShippingRequestPayloadObject payload) {
		this.payload = payload;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getBulkIndicator() {
		return bulkIndicator;
	}

	public void setBulkIndicator(String bulkIndicator) {
		this.bulkIndicator = bulkIndicator;
	}

	public String getHazmatIndicator() {
		return hazmatIndicator;
	}

	public void setHazmatIndicator(String hazmatIndicator) {
		this.hazmatIndicator = hazmatIndicator;
	}

	public String getLocnNbr() {
		return locnNbr;
	}

	public void setLocnNbr(String locnNbr) {
		this.locnNbr = locnNbr;
	}

	@Override
	public String toString() {
		return "{tname:" + tname + ", payload:" + payload + ", messageType:" + messageType
				+ ", clientId:" + clientId + ", bulkIndicator:" + bulkIndicator + ", hazmatIndicator:" + hazmatIndicator
				+ ", locnNbr:" + locnNbr + "}";
	}



}
