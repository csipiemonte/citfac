/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseCredentials {
	
	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String api;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	private ConfigComune comune;
	
	@JsonIgnore
	private String credentialType;
	
	public BaseCredentials (String credentialType) {
		this.credentialType = credentialType;
	}
	
}
