/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.BozzaRichiesta;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.repository.BozzaRichiestaRepository;

@Service
public class BozzeService {
	
	@Autowired
	private BozzaRichiestaRepository repositoryBozze;
	
	public List<BozzaRichiesta> getAllBozze(String codiceFiscale, ConfigComune comune) {
		return repositoryBozze.findAllByCodiceFiscaleAndComune(codiceFiscale, comune);
	}
	
	public List<BozzaRichiesta> getAllBozzeOfAsset(String codiceFiscale, Integer idAsset, Integer idComune) {
		return repositoryBozze.findByCodiceFiscaleAndAsset(codiceFiscale, idAsset, idComune);
	}
	
	public BozzaRichiesta addBozza (BozzaRichiesta bozzaRichiesta) throws Exception {
		return repositoryBozze.save(bozzaRichiesta);
	}
	
	public BozzaRichiesta updateBozza(BozzaRichiesta bozzaRichiesta) throws Exception {
		BozzaRichiesta vecchiaBozza = repositoryBozze.findByIdAndCodiceFiscale(bozzaRichiesta.getId(), bozzaRichiesta.getCodiceFiscale());
		
		if(vecchiaBozza == null)
			return null;
		
		return addBozza(bozzaRichiesta);
	}
	
	public int deleteBozza(Integer idBozza) {
		return repositoryBozze.deleteBozzaRichiestaById(idBozza);
	}
	
	//get per ultima richiesta di un dato servizio
	
	//get per tutte le bozze dato cf e servizio
	
	//post per salvare richiesta in bozza
	
	//put per modificare la richiesta
	
	//delete per eliminare richiesta quando inviata
}
