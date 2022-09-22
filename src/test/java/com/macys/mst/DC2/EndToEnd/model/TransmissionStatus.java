package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TransmissionStatus {
	private Integer id;
	private String updatedTime;
	private String createdTime;
	private String createdBy;
	private Integer version;
	private Integer enabled;
	private String status;
}
