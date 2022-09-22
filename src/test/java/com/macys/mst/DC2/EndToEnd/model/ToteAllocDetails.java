package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ToteAllocDetails {
    private String containerId;
    private String sku;
    private String storeLocNbr;
    private Integer quantity;
    private String storeType;
    private String barcode;
}
