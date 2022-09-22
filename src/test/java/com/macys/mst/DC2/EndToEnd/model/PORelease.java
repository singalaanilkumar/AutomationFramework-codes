package com.macys.mst.DC2.EndToEnd.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode
public class PORelease {
    private Integer poNumber;
    private Integer rcptNbr;
    private List<DistroRqstPOLines> distroRqstPOLines;
    private String requestType;
    private String distroType;
    private Integer locnNbr;
    private String requestStatus;
}
