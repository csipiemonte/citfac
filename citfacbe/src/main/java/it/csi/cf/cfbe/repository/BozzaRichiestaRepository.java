/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.csi.cf.cfbe.entity.BozzaRichiesta;
import it.csi.cf.cfbe.entity.ConfigComune;

@Repository
public interface BozzaRichiestaRepository extends JpaRepository<BozzaRichiesta, Integer>{
	
	@Query(value = "SELECT  * "
			+ "FROM bozze_richieste "
			+ "WHERE codice_fiscale = ?1 "
			+ "AND asset_id = ?2 "
			+ "AND comune_id = ?3 ",
			nativeQuery = true)
	List<BozzaRichiesta> findByCodiceFiscaleAndAsset(String codiceFiscale, Integer assetId, Integer comuneId);
	
	BozzaRichiesta findByIdAndCodiceFiscale(Integer id, String codiceFiscale);
	
	List<BozzaRichiesta> findAllByCodiceFiscaleAndComune(String codiceFiscale, ConfigComune comune);
	
	@Modifying
	@Transactional
	int deleteBozzaRichiestaById(Integer idBozza);
}
