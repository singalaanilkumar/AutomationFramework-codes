package com.macys.mst.DC2.EndToEnd.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PODetail {
	private PODetailHdr poDetailHdr;
	private POReceiptStatus poreceiptStatus;
	private List<POLineDetails> poLineDetails;
}
