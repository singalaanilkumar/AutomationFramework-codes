package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;

import java.util.List;

@Data
public class SkuDetailDTO {
    private String sku;
    private Integer deptNbr;
    private Integer skuQty;
    private List<ZoneDTO> zoneDTO;
}
