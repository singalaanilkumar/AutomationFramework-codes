package com.macys.mst.DC2.EndToEnd.model;

import lombok.*;

import java.math.BigInteger;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@ToString
public class WSMActivity {
    private BigInteger id;
    private String subType;
    private Integer quantity;
    private String containerType;
    private String containerId;
    private String upc;
    private Integer sequence;

}
