/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Servizio;

@Repository
public interface ServiziRepository extends JpaRepository<Servizio, Integer>{
	
	List<Servizio> findByComune(ConfigComune comune);
	
	@Transactional
	@Modifying
	int deleteAllByComune (ConfigComune comune);
}
