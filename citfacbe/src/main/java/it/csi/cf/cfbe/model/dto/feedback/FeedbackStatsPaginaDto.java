/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto.feedback;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackStatsPaginaDto {
	private String pageTitle;
    private String pagePath;
    private long totalFeedback;
    private double averageRating;
}
