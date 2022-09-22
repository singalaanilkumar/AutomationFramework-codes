package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class StoreAlloc {
    private String  storeNum;
    private Integer allocatedQuantity;
    private String  skuUpcNum;
    private String  deptNum;
}
