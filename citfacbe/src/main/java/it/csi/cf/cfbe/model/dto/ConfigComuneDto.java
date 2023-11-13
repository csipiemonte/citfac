/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.csi.cf.cfbe.entity.Asset;
import it.csi.cf.cfbe.entity.Cms;
import it.csi.cf.cfbe.entity.FirebaseCredentials;
import it.csi.cf.cfbe.entity.Regione;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigComuneDto {
	
	private String nome;
	
	private String codiceImmagine;
	
	private Regione regione;
	
	@JsonAlias("codiceEnte")
	@JsonProperty("codiceEnte")
	private String codiceComune;
	
	private String tenant;
	
	private FirebaseCredentials firebase;
	
	private List<AssetDto> assets;
	
	private String homePage;
	
	@JsonIgnore
	private boolean appschedPublic;
	
	private boolean maintenance;
	
	private boolean cms;
	
	public List<AssetDto> getAssets() {
		boolean appschedPublic = this.isAppschedPublic();
	
		for (AssetDto asset : assets) {
			
			
			if(asset.getCodice().equals("APPSCHED")) {
				asset.setShowPublic(appschedPublic);
			}

		}
		
		return assets;
	}
}
