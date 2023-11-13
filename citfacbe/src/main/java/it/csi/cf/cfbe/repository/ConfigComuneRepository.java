/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.csi.cf.cfbe.entity.ConfigComune;

@Repository
public interface ConfigComuneRepository extends JpaRepository<ConfigComune, Integer>{
	ConfigComune findByTenant(String tenant);
	ConfigComune findByCodiceComune(String codiceComune);
}
