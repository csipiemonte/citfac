/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto.utenti;

import it.csi.cf.cfbe.model.notificatore.Contatti;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtentePublic {
	
	private String nome;
	private String cognome;
	private String codFisc;
	private String email;
	private String phoneNumber;
	private Contatti contatti;
}
