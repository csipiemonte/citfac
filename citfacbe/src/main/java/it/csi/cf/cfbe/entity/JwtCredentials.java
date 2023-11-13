/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Entity
@Data
@Setter
@AllArgsConstructor
public class JwtCredentials extends BaseCredentials {
	
	public JwtCredentials () {
		super("JWT");
	}
	
	@Column(columnDefinition="TEXT")
	private String jwt;
	
}
