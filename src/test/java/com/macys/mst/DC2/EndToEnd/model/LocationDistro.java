package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode
public class LocationDistro {

    private Integer poNbr;
    private BigInteger skuUpcNbr;
    private Integer locationNbr;
    private Integer storeNbr;
    private String storeAbbr;
    private Integer orderQty;
    private Integer receivedQty;
    private Integer openQty;
    private Boolean newStore;
}
