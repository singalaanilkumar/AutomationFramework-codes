package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class SKUDetails {
    private BigInteger sku;
    private Integer quantity;

}
