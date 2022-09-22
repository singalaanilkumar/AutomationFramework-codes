package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ExistingStoreUnits {
	private String expected;
	private String received;
	private String pending;
	private String total;
}
