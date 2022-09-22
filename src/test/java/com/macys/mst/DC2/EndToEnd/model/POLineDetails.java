package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class POLineDetails {
	private String inhouseUpc;
	private String vendorUpc;
	private String mkStyl;
	private String vndStyle;
	private String pid;
	private String pidDesc;
	private String colDesc;
	private String sizeDesc;
	private String compareRetail;
	private String expectedUnits;
	private String actualUnits;
	private String rcv;
	private String status;
	private String processArea;
}
