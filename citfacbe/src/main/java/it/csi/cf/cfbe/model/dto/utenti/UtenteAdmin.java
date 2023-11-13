/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto.utenti;

import java.util.List;

import it.csi.cf.cfbe.enumeration.Role;
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
public class UtenteAdmin {
	
	private String nome;
	private String cognome;
	private String codFisc;
	private Role ruolo;
	private List<String> enti;
	private String mail;
}
