/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import it.csi.cf.cfbe.enumeration.CredentialsType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
	
	public Asset (int idAsset) {
		id = idAsset;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String codice;
	
	private String nome;
	
	@Enumerated(EnumType.STRING)
	private CredentialsType tipoCredenziali;
	
	@Column(columnDefinition="TEXT")
	private String descrizione;
	
	private String urlBackoffice;
	
	private String icon;
	
}
