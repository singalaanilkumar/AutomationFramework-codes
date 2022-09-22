package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.List;

@Data
@EqualsAndHashCode
public class POLineItems {
    private Integer  poNbr;
    private Integer id;
    private BigInteger skuUpc;
    private String pid;
    private Integer orderQuantity;
    private Integer receivedQuantity;
    private Integer openQuantity;
    private Integer deptNbr;
    private List<LocationDistro> poLocationDistroList;

}
