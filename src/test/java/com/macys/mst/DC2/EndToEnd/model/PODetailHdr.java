package com.macys.mst.DC2.EndToEnd.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class PODetailHdr {
	private String reportId;
	private String rcptNbr;
	private Integer locnNbr;
	private String wkcnt;
	private String deptNbr;
	private String orderProfile;
	private String keyRec;
	private String poNbr;
	private String vendor;
	private String newStore;
	private String poType;
	private String distro;
	private String cdp;
	private String apptNo;
	private String poStatus;
}
