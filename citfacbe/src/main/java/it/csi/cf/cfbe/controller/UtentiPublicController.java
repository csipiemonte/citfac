/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.filter.IrideIdAdapterFilter;
import it.csi.cf.cfbe.model.dto.utenti.UtentePublic;
import it.csi.cf.cfbe.model.identity.Utente;
import it.csi.cf.cfbe.services.NotificatoreService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api-public/utenti")
@Produces(MediaType.APPLICATION_JSON)
public class UtentiPublicController {
	
	@Autowired
	private NotificatoreService notificatoreService;
    
    @GET
    @Path("/me")
    public Response meGet(@Context HttpServletRequest request ) {
    	
    	Utente utente = (Utente) request.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
    	
    	if(utente == null) {
    		return ContextBFF.buildResponseError(401, "Accesso non effettuato");
    	}
    	
    	try {
    		
    		utente.setContatti(notificatoreService.getCustomerInfo(request));
    	} catch (Exception e) {
			// TODO: handle exception
		}
    	
    	UtentePublic dto = new UtentePublic();
    	
    	BeanUtils.copyProperties(utente, dto);
    	
        return ContextBFF.buildResponse(200, dto);
    }
    
    @GET
    @Path("/menu")
    @Produces("application/javascript")
    public Response getMenu(@Context HttpServletRequest request) {
    	Utente utente = (Utente) request.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
    	String tenant = ContextBFF.getTenant(request);
    	String baseUrl = "https://" + tenant;
    	
    	String base = "const user = {\"auth\" : [AUTH], \"maintenance\" : [MAINTENANCE], \"buttonLabel\" : \"[BUTTON_LABEL]\", \"loginUrl\" : [LOGIN_URL], \"menu\": [MENU]}";
    	
    	ConfigComune comune = ContextBFF.getConfigComune(request);
    	
    	base = base.replace("[MAINTENANCE]", comune.isMaintenance() + "");
    	
    	if (utente == null) {    		
    		String loginUrl = baseUrl + "/api/utenti/login";
    		
    		String currentObj = base.replace("[BUTTON_LABEL]", "Accedi all'area personale");
    		
    		currentObj = currentObj.replace("[AUTH]", "false");
    		
    		currentObj = currentObj.replace("[LOGIN_URL]", "\""+loginUrl+"\"");
    		
    		currentObj = currentObj.replace("[MENU]", "null");
    		
    		return ContextBFF.buildResponse(200, currentObj);  
    	}
    	
    	String currentObj = base.replace("[BUTTON_LABEL]", utente.getNome() + " " + utente.getCognome());
		
		currentObj = currentObj.replace("[LOGIN_URL]", "null");
		
		String areaPersonale = baseUrl + "/area-personale";
		
		String profilo = areaPersonale + "/profilo-utente";
		
		String logout = baseUrl + "/api/utenti/logout";
		
		String menu = "[{\"label\": \"Area personale\", \"url\": \"[AREA_PERSONALE]\"}, {\"label\": \"Profilo\", \"url\": \"[PROFILO]\"}, {\"label\": \"Esci\", \"url\": \"[LOGOUT]\"}]";
		
		menu = menu.replace("[AREA_PERSONALE]", areaPersonale).replace("[PROFILO]", profilo).replace("[LOGOUT]", logout); 
		
		currentObj = currentObj.replace("[MENU]", menu);
		
		currentObj = currentObj.replace("[AUTH]", "true");
		
		return ContextBFF.buildResponse(200, currentObj);
    }

}
