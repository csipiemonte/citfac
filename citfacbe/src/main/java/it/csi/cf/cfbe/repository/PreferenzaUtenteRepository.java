/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.csi.cf.cfbe.entity.PreferenzaUtente;

@Repository
public interface PreferenzaUtenteRepository extends JpaRepository<PreferenzaUtente, Integer>{
	PreferenzaUtente findByCodiceFiscaleAndTenant(String codiceFiscale, String tenant);
}
