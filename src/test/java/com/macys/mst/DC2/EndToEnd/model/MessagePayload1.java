package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MessagePayload1 {
    private String tname;
    private GenericMessagePayload payload;
}
