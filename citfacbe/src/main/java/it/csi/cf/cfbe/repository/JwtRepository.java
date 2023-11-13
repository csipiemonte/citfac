/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.csi.cf.cfbe.entity.BaseCredentials;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.JwtCredentials;

@Repository
public interface JwtRepository extends JpaRepository<JwtCredentials, Integer> {
	JwtCredentials findByComuneAndApi (ConfigComune comune, String Api);
	
	List<BaseCredentials> findAllByComune(ConfigComune comune);
}
