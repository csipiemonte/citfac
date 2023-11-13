/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto;

import java.util.List;

import it.csi.cf.cfbe.entity.Asset;
import it.csi.cf.cfbe.entity.Regione;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConfigComuneDto {
	
	private Integer id;
	
	private String nome;
	
	private String tenant;

	private String templateNotificatore;
	
	private String codiceImmagine;
	
	private String codiceComune;
	
	private Regione regione;
	
	private String logoutUri;
	
	private String cf;
	
	private String apiManagerKey;
	
	private String apiManagerSecret;
	
	private String urlCms;
	
	private String urlServizi;
	
	private String urlMenu;
	
	private List<Asset> assets;
	
	private String codiceMoon;
}
