/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {
	
	private Integer id;
	
	private String pagePath;
	
	private String pageTitle;
	
	private Integer serviceID;
	
	private int rating;
	
	private String option;
	
	private String details;
	
	private String bind;
}
