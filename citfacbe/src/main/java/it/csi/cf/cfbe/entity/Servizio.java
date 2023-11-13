/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servizi")
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Servizio {
	
	public Servizio (int idServizio) {
		id = idServizio;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotNull
	private String nome;
	
	private String descrizione;
	
	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private CategoriaServizio categoriaServizio;
	
	private String linkScheda;
	
	private String linkServizio;
	
	@NotNull
	private boolean servizioCittaFacile;

	@NotNull
	private boolean servizioAttivo;
	
	@Column(columnDefinition="TEXT")
	private String messaggioManutenzione;
	
	@NotNull
	private boolean notificabile;
		
	private String codiceNotifiche;

	@NotNull
	private boolean assistenza;
	
	private String codice_assistenza;
	
	@NotNull
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	private ConfigComune comune;

}
