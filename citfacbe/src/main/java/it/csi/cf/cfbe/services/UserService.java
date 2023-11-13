/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.User;
import it.csi.cf.cfbe.repository.UsersRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
public class UserService {

	@Autowired
	private UsersRepository repository;
	
	public User getUser(HttpServletRequest req, String cf) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		
		return comune == null ? null : repository.findByCodiceFiscaleAndCodiceComune(cf, comune.getCodiceComune());
	}
	
	public User getUser(String cf) {
		return repository.findByCodiceFiscale(cf);
	}
}
