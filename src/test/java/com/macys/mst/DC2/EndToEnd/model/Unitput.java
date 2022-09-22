package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class Unitput {

    private String containerId;
    private Integer storeLocationNbr;
    private Integer quantity;
    private List<SKUDetails> skuDetails;
    private String deptNbr;
    private String casePack;
}
