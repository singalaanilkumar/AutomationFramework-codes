package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ContainerRelation {

    private String parentContainer;
    private String parentContainerType;
    private Integer depth;
    private String childContainer;
    private String childContainerType;
}

