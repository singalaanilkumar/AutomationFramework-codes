package com.macys.mst.DC2.EndToEnd.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;

@Data
@EqualsAndHashCode
public class PoLine {
    private BigInteger inhouseUpc;
    private Integer vendorUpc;
}