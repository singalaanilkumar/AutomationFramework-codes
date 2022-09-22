package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class POLineBarCode {

    private String poNbr;
    private String poLineBarCode;
    private String sku;
    private String openQty;
    private String receiptNbr;
    private String reportId;
}
