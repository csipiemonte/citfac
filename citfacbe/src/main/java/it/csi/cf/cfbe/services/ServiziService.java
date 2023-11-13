/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.Cms;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Servizio;
import it.csi.cf.cfbe.repository.ServiziRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
public class ServiziService {
	
	@Autowired
	private ServiziRepository repositoryServizi;
	
	@Autowired
	private CmsService cmsService;
	
	public Response getServices(HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non presente");
		}
		
		Cms cms = cmsService.getCms(comune);
		
		String urlServizi = cms != null ? cms.getUrlServizi() : null;
		
		if(urlServizi != null) {
	    	URI uri = URI.create(urlServizi);
	    	return Response.status(302).location(uri).build();
		} else {
			List<Servizio> servizi = repositoryServizi.findByComune(comune);
			
			return ContextBFF.buildResponse(200,servizi);
		}
	}
	
	public Response getService(Integer id, Integer tenantId, HttpServletRequest req) {
		Optional<Servizio> optionalServizio = repositoryServizi.findById(id);
		
		if(optionalServizio.isEmpty()) {
			return ContextBFF.buildResponseError(404, "Servizio non presente");
		}
		
		Servizio servizio = optionalServizio.get();
		
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId); 
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "Comune non presente");
		
		if(servizio.getComune().getId() != comune.getId()) {
			
			return ContextBFF.buildResponseError(403, "Non autorizzato");
		}
		
		return ContextBFF.buildResponse(200, servizio);
	}
	
	public Response addService(HttpServletRequest req, Integer tenantId, Servizio servizio) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non configurato");
		}
		
		Cms cms = cmsService.getCms(comune);
		
		if(cms != null && cms.getUrlServizi() != null) {
			return ContextBFF.buildResponseError(400, "Url Servizi presente");
		}
		
		servizio.setComune(comune);
	    
	    return ContextBFF.validateAndSave(servizio, repositoryServizi, false);
	}
	
	public Response updateService(HttpServletRequest req, Integer tenantId, Integer serviceId, Servizio servizio) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non configurato");
		}
		
		Cms cms = cmsService.getCms(comune);
		
		if(cms != null && cms.getUrlServizi() != null) {
			return ContextBFF.buildResponseError(400, "Url Servizi presente");
		}
		
		Optional<Servizio> oldServizio = repositoryServizi.findById(serviceId);
		
		if (oldServizio.isEmpty()) {
	        return ContextBFF.buildResponseError(404, "servizio non trovato");
	    }
		
		if(oldServizio.get().getComune().getId() != comune.getId()) {
			return ContextBFF.buildResponseError(403, "servizio non coerente con il comune indicato");
		}
		
		servizio.setComune(comune);
		servizio.setId(serviceId);
		
		return ContextBFF.validateAndSave(servizio, repositoryServizi, true);
	}
	
	public Response deleteService(Integer idServizio, Integer tenantId, HttpServletRequest req) {
		Optional<Servizio> oldServizio = repositoryServizi.findById(idServizio);
		
		if (oldServizio.isEmpty()) {
	        return ContextBFF.buildResponseError(404, "servizio non trovato");
	    }
		
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId); 
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "Comune non presente");
		
		if(oldServizio.get().getComune().getId() != comune.getId()) {
			
			return ContextBFF.buildResponseError(403, "Non autorizzato");
		}
		
		repositoryServizi.deleteById(idServizio);
		
		return ContextBFF.buildResponse(200, "{\"status:\":\"eliminato\"}");
	}
	
	public Response deleteAll(HttpServletRequest req, Integer tenantId) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		int deleted = repositoryServizi.deleteAllByComune(comune);
		
		return ContextBFF.buildResponse(200, "{\"info:\":\"deleted " + deleted + " items\"}");
	}
	
}
