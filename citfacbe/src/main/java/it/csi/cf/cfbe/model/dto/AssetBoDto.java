/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto;

import it.csi.cf.cfbe.entity.Asset;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssetBoDto extends Asset {
	
	private boolean showPublic = false;
}
