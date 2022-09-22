package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Activity {

    public String id;

    public String type;

    public String qty;

    public String status;

    public String containerId;

    public String containerType;

    public String locnNbr;

    public String upc;

    public String totalQty;

    public String actor;

    public Attributes attributes;

}
