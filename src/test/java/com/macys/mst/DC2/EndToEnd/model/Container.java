package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class Container {

    private String barCode;
    private String containerType;
    private String containerStatusCode;
    private List<ContainerRelation> containerRelationshipList;
    private List<Attribute> attributeList;
    private List<Dimension> dimensionList;

}

