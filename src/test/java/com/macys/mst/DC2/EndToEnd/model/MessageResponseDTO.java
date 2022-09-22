package com.macys.mst.DC2.EndToEnd.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MessageResponseDTO {
	private Integer totalElements;
	private Integer totalPageCount;
	private Integer currentPage;
	private List<Messages> messages;
}
