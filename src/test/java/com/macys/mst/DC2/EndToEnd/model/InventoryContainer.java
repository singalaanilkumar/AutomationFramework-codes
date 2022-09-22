package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class InventoryContainer {
	private Container container;
	private List<InventorySnapshot> inventorySnapshotList;

}
