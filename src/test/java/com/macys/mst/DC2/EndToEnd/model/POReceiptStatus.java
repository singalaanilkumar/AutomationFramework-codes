package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class POReceiptStatus {
	private Cartons cartons;
	private Units units;
	private NewStoreUnits newStoreUnits;
	private ExistingStoreUnits existingStoreUnits;
	private PackawayUnits packawayUnits;
	private UnallocatedUnits unallocatedUnits;
}
