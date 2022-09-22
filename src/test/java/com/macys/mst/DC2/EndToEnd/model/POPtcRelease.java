package com.macys.mst.DC2.EndToEnd.model;

import java.math.BigInteger;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode
public class POPtcRelease {
	private Integer poNbr;
    private Integer rcptNbr;
    private Set<BigInteger> skuUpcs;
}
