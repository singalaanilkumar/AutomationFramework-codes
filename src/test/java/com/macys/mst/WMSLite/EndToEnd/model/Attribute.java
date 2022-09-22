package com.macys.mst.WMSLite.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class Attribute {
	private List<String> values;
	private String key;
}
