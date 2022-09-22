package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class GenericMessagePayload {
    private  Integer store;
    private List<SKUDetails> items;
    private Integer dept;
    private Integer quantity;
    private Integer prevPutQuantity;
    private String casePack;
    private String orderSource;
    private Integer orderNumber;
    private Integer receiptNumber;
    private String container;
    private Boolean newStore;
    private String messageType;
}
