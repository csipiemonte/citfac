/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.ServiceDetail;
import it.csi.cf.cfbe.repository.ServiceDetailRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
public class ServiceDetailService {
	
	@Autowired
	private ServiceDetailRepository repository;
	
	public Response getServicesDetails(HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non presente");
		}
		
		List<ServiceDetail> dettagliServizi = repository.findAllByComune(comune);
		
		return ContextBFF.buildResponse(200,dettagliServizi);
		
	}
	
	public Response getServiceDetail(Integer id, Integer tenantId, HttpServletRequest req) {
		Optional<ServiceDetail> optionalServiceDetail = repository.findById(id);
		
		if(optionalServiceDetail.isEmpty()) {
			return ContextBFF.buildResponseError(404, "Dettaglio servizio non presente");
		}
		
		ServiceDetail serviceDetail = optionalServiceDetail.get();
		
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId); 
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "Comune non presente");
		
		if(serviceDetail.getComune().getId() != comune.getId()) {
			
			return ContextBFF.buildResponseError(403, "Non autorizzato");
		}
		
		return ContextBFF.buildResponse(200, serviceDetail);
	}
	
	public Response addServiceDetail(HttpServletRequest req, Integer tenantId, ServiceDetail detail) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non configurato");
		}
		
		detail.setComune(comune);
	    
	    return ContextBFF.validateAndSave(detail, repository, false);
	}
	
	public Response updateServiceDetail(HttpServletRequest req, Integer tenantId, Integer serviceId, ServiceDetail detail) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non configurato");
		}
		
		Optional<ServiceDetail> oldServiceDetail = repository.findById(serviceId);
		
		if (oldServiceDetail.isEmpty()) {
	        return ContextBFF.buildResponseError(404, "Dettaglio servizio non trovato");
	    }
		
		if(oldServiceDetail.get().getComune().getId() != comune.getId()) {
			return ContextBFF.buildResponseError(403, "Dettaglio servizio non coerente con il comune indicato");
		}
		
		detail.setComune(comune);
		detail.setId(serviceId);
		
		return ContextBFF.validateAndSave(detail, repository, true);
	}
	
	public Response deleteServiceDetail(Integer idServizio, Integer tenantId, HttpServletRequest req) {
		Optional<ServiceDetail> oldServiceDetail = repository.findById(idServizio);
		
		if (oldServiceDetail.isEmpty()) {
	        return ContextBFF.buildResponseError(404, "dettaglio servizio non trovato");
	    }
		
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId); 
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "Comune non presente");
		
		if(oldServiceDetail.get().getComune().getId() != comune.getId()) {
			
			return ContextBFF.buildResponseError(403, "Non autorizzato");
		}
		
		repository.deleteById(idServizio);
		
		return ContextBFF.buildResponse(200, "{\"status:\":\"eliminato\"}");
	}
	
	public Response deleteAll(HttpServletRequest req, Integer tenantId) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		int deleted = repository.deleteAllByComune(comune);
		
		return ContextBFF.buildResponse(200, "{\"info:\":\"deleted " + deleted + " items\"}");
	}	

}
