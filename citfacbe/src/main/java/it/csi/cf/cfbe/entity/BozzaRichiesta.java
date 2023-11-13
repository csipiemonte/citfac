/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "bozze_richieste")
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BozzaRichiesta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String codiceFiscale;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	private Asset asset;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	private ConfigComune comune;
	
	@Column(columnDefinition="TEXT")
	private String body;
	
	private Timestamp dataCreazione;
	
}
