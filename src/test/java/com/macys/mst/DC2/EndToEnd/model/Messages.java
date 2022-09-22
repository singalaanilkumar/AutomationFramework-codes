package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Messages {
	private Integer id;
	private String updatedTime;
	private String updatedBy;
	private String createdTime;
	private String createdBy;
	private Integer version;
	private Integer enabled;
	private String clientId;
	private String transactionName;
	private String destinationId;
	private String destinationDescription;
	private String incomingPayload;
	private String outgoingPayload;
	private Integer locnNbr;
	private String sequenceNumber;
	private Integer retryAttempts;
	private TransmissionStatus transmissionStatus;
}
