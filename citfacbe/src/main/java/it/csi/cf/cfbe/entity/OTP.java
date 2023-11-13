/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import it.csi.cf.cfbe.enumeration.TipoRichiestaOTP;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "otps")
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTP {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "codice_fiscale", length = 16)
	private String codiceFiscale;
	
	private String codiceOTP;
	
	private boolean used;
	
	private Timestamp expirationDate;

	private TipoRichiestaOTP tipoRichiesta;
	
}
