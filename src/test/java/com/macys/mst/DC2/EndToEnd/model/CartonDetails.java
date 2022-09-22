package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class CartonDetails {
    private String sequenceId;
    private String cartonId;
    private Integer totalQuantity;
    private String skuupc;
    private String storeLocationNumber;
    private String toteId;
}
