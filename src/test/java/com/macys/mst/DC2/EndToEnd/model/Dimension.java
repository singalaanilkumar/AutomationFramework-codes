package com.macys.mst.DC2.EndToEnd.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Dimension {
    private String name;
    private String value;
    private String uomCode;
    private String uomDescription;
    private String code;

    
}
