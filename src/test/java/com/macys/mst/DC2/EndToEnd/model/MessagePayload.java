package com.macys.mst.DC2.EndToEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class MessagePayload {
    private String containerId;
    private String items;
    private Integer quantity;
    private String dept;
    private String newStore;
    private String messageType;
}
