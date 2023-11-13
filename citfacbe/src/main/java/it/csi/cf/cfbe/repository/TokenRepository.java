/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.csi.cf.cfbe.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Integer> {
	Token findByTypeAndCodiceComune(String type, String codiceComune);
}
