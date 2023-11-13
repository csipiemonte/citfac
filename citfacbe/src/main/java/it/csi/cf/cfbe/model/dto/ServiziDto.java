/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.model.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import it.csi.cf.cfbe.entity.CategoriaServizio;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ServiziDto {

	private Integer id;
	
	private String nome;
	
	private String descrizione;
	
	private CategoriaServizio categoriaServizio;
	
	private String linkScheda;
	
	private String linkServizio;

	private boolean servizioCittaFacile;

	private boolean servizioAttivo;
	
	private String messaggioManutenzione;

	private boolean notificabile;
	
	private String codiceNotifiche;

	private boolean assistenza;
	
	private String codiceAssistenza;
}
