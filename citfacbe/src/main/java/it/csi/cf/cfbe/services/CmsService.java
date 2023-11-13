/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.Cms;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.repository.CmsRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
public class CmsService {
	
	
	@Autowired
	private CmsRepository repository;
	
	public Response getCms (HttpServletRequest req, Integer tenantId) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non presente");
		}
		
		Cms cms = repository.findByComune(comune);
		
		if(cms == null) {
			return ContextBFF.buildResponseError(404, "Cms non presente");
		}
		
		return ContextBFF.buildResponse(200, cms);
	}
	
	public Cms getCms (ConfigComune comune) {
		
		return repository.findByComune(comune);
		
	}
	
	public Response addCms (HttpServletRequest req, Integer tenantId, Cms newCms) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(400, "Comune non presente");
		}
		
		Cms cms = repository.findByComune(comune);
		
		if(cms != null) {
			return ContextBFF.buildResponseError(400, "Cms già presente");
		}
		
		newCms.setId(null);
		newCms.setComune(comune);
		
		repository.save(newCms);
		
		return ContextBFF.buildResponse(201, "Cms Added");	
	}
	
	public Response updateCms (HttpServletRequest req, Integer tenantId, Cms newCms) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(400, "Comune non presente");
		}
		
		Cms cms = repository.findByComune(comune);
		
		if(cms == null) {
			return ContextBFF.buildResponseError(400, "Cms non presente");
		}
		
		newCms.setId(cms.getId());
		newCms.setComune(comune);
		
		repository.save(newCms);
		
		return ContextBFF.buildResponse(200, "Cms Updated");	
	}
	
	public Response deleteCms (HttpServletRequest req, Integer tenantId) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(400, "Comune non presente");
		}
		
		Cms cms = repository.findByComune(comune);
		
		if(cms == null) {
			return ContextBFF.buildResponseError(400, "Cms non presente");
		}
		
		repository.delete(cms);
		
		return ContextBFF.buildResponse(200, "Cms deleted");
	}
	
	
}
