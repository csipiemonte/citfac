/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto.feedback;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatsGeneralDto {
	
	private Long totalFeedbacks;
	private double averageRating;
	private List<FeedbackStatsRatingDto> rating;
	private List<FeedbackStatsOptionDto> options;
}
