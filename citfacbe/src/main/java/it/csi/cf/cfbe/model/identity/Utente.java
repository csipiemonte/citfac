/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.identity;

import java.io.Serializable;
import java.util.List;

import it.csi.cf.cfbe.enumeration.Role;
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
public class Utente implements Serializable {

	private static final long serialVersionUID = 5602007492075298016L;
	
	private String nome;
	private String cognome;
	private String codFisc;
	private String email;
	private String phoneNumber;
	private Contatti contatti;
	private Role ruolo;
	private List<String> enti;
}
