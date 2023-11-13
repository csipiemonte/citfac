/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "config_comuni")
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigComune {
	
	public ConfigComune(Integer id) {
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull
	private String nome;
	
	@Column(columnDefinition = "VARCHAR(255) DEFAULT ''")
	private String placeholderAsset;
	
	@Column(unique = true)
	@NotNull
	private String tenant;

	private String templateNotificatore;
	
	@JsonAlias("codiceEnte")
	@JsonProperty("codiceEnte")
	@NotNull
	private String codiceComune;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private Regione regione;
	
	@NotNull
	private String logoutUri;
	
	private String logoutUriBo;
	
	@NotNull
	private String cf;
	
	@NotNull
	private String apiManagerKey;
	
	@NotNull
	private String apiManagerSecret;
	
	private String homePage;
	
	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean appschedPublic;
	
	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	private boolean maintenance;	
	
	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	private List<Asset> assets;
	
}
