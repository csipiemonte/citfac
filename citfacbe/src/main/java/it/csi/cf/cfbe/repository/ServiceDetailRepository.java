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
import it.csi.cf.cfbe.entity.ServiceDetail;

@Repository
public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, Integer> {
	
	List<ServiceDetail> findAllByComune(ConfigComune comune);
	
	ServiceDetail findByComuneAndModuloMoon(ConfigComune comune, String moduloMoon);
	
	@Transactional
	@Modifying
	int deleteAllByComune (ConfigComune comune);
	
}