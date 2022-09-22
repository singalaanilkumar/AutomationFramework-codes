package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
@Data
@EqualsAndHashCode
public class PODataInsert {
    public Integer reportId;
    public Integer rcptNbr;
    public Integer locnNbr;
    public Integer poNbr;
    public String status;
    public Integer deptNbr;
    public List<PoLine> poLineItms = null;
}
