package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class ToteCont {
	// 1 SKU BINBOX currently
    private String  containerId;
    private Integer allocatedQuantity;
    private String  skuUpcNum;
}
