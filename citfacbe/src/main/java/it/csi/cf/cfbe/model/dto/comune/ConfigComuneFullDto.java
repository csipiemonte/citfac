/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto.comune;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.csi.cf.cfbe.entity.Asset;
import it.csi.cf.cfbe.entity.Cms;
import it.csi.cf.cfbe.entity.Regione;
import it.csi.cf.cfbe.model.dto.AssetBoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigComuneFullDto {
	
	private Integer id;
	
	private String nome;
	
	private String placeholderAsset;
	
	private String tenant;

	private String templateNotificatore;

	private String codiceImmagine;
	
	@JsonAlias("codiceEnte")
	@JsonProperty("codiceEnte")
	private String codiceComune;
	
	private Regione regione;
	
	private String logoutUri;
	
	private String cf;
	
	private Cms cms;
	
	private List<Asset> assets;
	
	private String homePage;
	
	@JsonIgnore
	private boolean appschedPublic;
	
	private boolean maintenance;
	
	public List<AssetBoDto> getAssets() {
		String nomeComune = this.getPlaceholderAsset().toLowerCase();
		boolean appschedPublic = this.isAppschedPublic();
		
		List<AssetBoDto> newAssets = new ArrayList<>();
	
		
		for (Asset asset : assets) {
			
			AssetBoDto dto = new AssetBoDto(); 
			
			
			if( asset.getUrlBackoffice() != null ) {				
				String urlBackoffice = asset.getUrlBackoffice();
				urlBackoffice = urlBackoffice.replace("[NOME]", nomeComune);
				asset.setUrlBackoffice(urlBackoffice);
			}
			
			if(asset.getCodice().equals("APPSCHED")) {
				dto.setShowPublic(appschedPublic);
			}
			
			BeanUtils.copyProperties(asset, dto);
			
			newAssets.add(dto);
		}
		
		return newAssets;
	}
}
