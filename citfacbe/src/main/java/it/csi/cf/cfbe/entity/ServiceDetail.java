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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"id_servizio", "comune_id"})})
public class ServiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String titolo;
    @Column(name = "id_servizio")
    private Integer idServizio;
    private String moduloMoon;
    private String idSportello;
    private String codiceNotifiche;
    private String codiceAssistenza;
    private Integer idFaq;
    private Integer idBot;
    @NotNull
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	private ConfigComune comune;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Asset asset;
}
