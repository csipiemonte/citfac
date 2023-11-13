/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto.otp;

import it.csi.cf.cfbe.enumeration.TipoRichiestaOTP;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class OTPRequestDto {
	
	private String codiceFiscale;
	
	private TipoRichiestaOTP tipoRichiesta;
	
	private String contatto;
	
}
