/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
@Setter
@AllArgsConstructor
@Table( uniqueConstraints = {@UniqueConstraint(columnNames={"comune_id", "api"})})
public class Credenziali extends BaseCredentials {
	
	public Credenziali () {
		super("BASIC");
	}
	
	private String username;
	
	@JsonIgnore
	private String password;
}
