/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.FirebaseCredentials;

@Repository
public interface FireBaseCredentialsRepository extends JpaRepository<FirebaseCredentials, Integer>{
	FirebaseCredentials findByComune (ConfigComune comune);
}
