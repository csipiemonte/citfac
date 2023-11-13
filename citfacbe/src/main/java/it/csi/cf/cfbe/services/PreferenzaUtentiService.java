/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.PreferenzaUtente;
import it.csi.cf.cfbe.repository.PreferenzaUtenteRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class PreferenzaUtentiService {
	
	@Autowired
	private PreferenzaUtenteRepository repositoryPreferenze;
	
	public PreferenzaUtente getPreferenzaUtente(String cf, String tenant) {
		return repositoryPreferenze.findByCodiceFiscaleAndTenant(cf, tenant);
	}
	
	
	public PreferenzaUtente addPreferenza(String cf, String tenant, Integer idServizio) {
		PreferenzaUtente preferenzaUtente = getPreferenza(cf, tenant);
		
		if(preferenzaUtente == null) {
			List<Integer> servizi = new ArrayList<>();
			servizi.add(idServizio);
			preferenzaUtente = new PreferenzaUtente(null, cf, tenant, servizi);

			return repositoryPreferenze.save(preferenzaUtente);
		} else {
			if(!preferenzaUtente.getServizi().contains(idServizio)) {
				List<Integer> servizi = preferenzaUtente.getServizi();
				log.debug("size lista servizi: {}", servizi.size());
				servizi.add(idServizio);
				log.debug("size lista servizi: {}", servizi.size());
				preferenzaUtente.setServizi(servizi);
				return repositoryPreferenze.save(preferenzaUtente);
			}	
		}
		
		return null;
		
	}
	
	public boolean deletePreferenza(String cf, Integer idServizio, String tenant) {
		PreferenzaUtente preferenzaUtente = getPreferenza(cf, tenant);
		
		if(preferenzaUtente != null) {
			List<Integer> servizi = preferenzaUtente.getServizi();
			if (servizi.contains(idServizio) && servizi.removeIf(id -> id == idServizio)) {
				preferenzaUtente.setServizi(servizi);
				return true;
			}
		}
		
		return false;
		
	}
	
	private PreferenzaUtente getPreferenza(String cf, String tenant) {
		return repositoryPreferenze.findByCodiceFiscaleAndTenant(cf, tenant);
	}
	
	//get tutte le freferenze di un utente 
	
	//post aggiungere la preferenza
	
	//delete rimuovere una preferenza
}
