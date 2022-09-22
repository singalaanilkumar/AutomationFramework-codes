package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;

import java.util.List;

@Data
public class ZoneDTO {
    private String zoneNbr;
    private Integer zoneQty;
    private List<StoreDetailDTO> storeDetailDTO;

}
