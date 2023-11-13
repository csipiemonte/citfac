/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;


import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.filter.IrideIdAdapterFilter;
import it.csi.cf.cfbe.model.dto.utenti.UtentePublic;
import it.csi.cf.cfbe.model.identity.Utente;
import it.csi.cf.cfbe.services.NotificatoreService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api/utenti")
@Produces(MediaType.APPLICATION_JSON)
public class UtentiController {
	
	@Autowired
	private NotificatoreService notificatoreService;
    
    @GET
    @Path("/me")
    public Response meGet(@Context HttpServletRequest request ) {
    	
    	Utente utente = (Utente) request.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
    	
    	if(utente == null) {
    		return ContextBFF.buildResponseError(401, "Accesso non effettuato");
    	}
    	
    	utente.setContatti(notificatoreService.getCustomerInfo(request));
    	
    	UtentePublic dto = new UtentePublic();
    	
    	BeanUtils.copyProperties(utente, dto);
    	
        return ContextBFF.buildResponse(200, dto);
    }
    
    @GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request, @Context UriInfo info ) {
    	String tenant = ContextBFF.getTenant(request);
    	ConfigComune configComune = ContextBFF.getConfigComune(request);
    	URI uri = URI.create("https://" + tenant + "/" + configComune.getLogoutUri());
    	return Response.status(302).location(uri).build();
    }
    
    @GET
    @Path("/login")
    public Response login(@Context HttpServletRequest request, @Context UriInfo info, @QueryParam("redirector") String redirector ) {
    	String tenant = ContextBFF.getTenant(request);
    	
    	String query = "";
    	
    	if(redirector != null && !redirector.equals("")) {
    		query = "?redirector=" + redirector;
    	}
    	
    	URI uri = URI.create("https://" + tenant + "/area-personale" + query );
    	return Response.status(302).location(uri).build();
    }

}
