/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.csi.cf.cfbe.entity.BaseCredentials;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Credenziali;

public interface CredenzialiRepository extends JpaRepository<Credenziali, Integer> {

	Credenziali findByComuneAndApi(ConfigComune comune, String api);
	
	List<BaseCredentials> findAllByComune(ConfigComune comune);
}
