/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "preferenze_utenti")
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenzaUtente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String codiceFiscale;
	
	private String tenant;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "servizi_preferiti", joinColumns = @JoinColumn(name = "preferenze_utenti_id"))
    @Column(name = "id_servizio")
	private List<Integer> servizi;
	
}
