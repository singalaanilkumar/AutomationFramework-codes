package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.List;

@Data
@EqualsAndHashCode
public class InventorySnapshot {
	private BigInteger id;
	private Integer locationNbr;
	private String item;
	private Integer quantity;
	private String statusCode;
	private String statusDescription;
	private String container;
	private String containerType;
	private String referenceContainer;
	private String referenceContainerType;
	private Object destinationContainer;
	private Object destinationContainerType;
	private List<String> conditionCodeList;
	private List<Attribute> attributeList;

}
