package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class PODetails {
    private Integer poNbr;
    private List<POLineItems> poLineItemList;
}
