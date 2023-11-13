/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.PreferenzaUtente;
import it.csi.cf.cfbe.services.PreferenzaUtentiService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api/preferenze")
@Produces(MediaType.APPLICATION_JSON)
public class PreferenzeController {
	
	@Autowired
	private PreferenzaUtentiService service;
	
    @GET
    public Response getAllPreferenze(@Context HttpServletRequest request) {
    	String cf = ContextBFF.getCodiceFiscaleShib(request);
    	String tenant = ContextBFF.getTenant(request);
    	
    	PreferenzaUtente preferenzaUtente = service.getPreferenzaUtente(cf, tenant);
    	
    	if(preferenzaUtente == null)
    		return ContextBFF.buildResponse(200, "[]");
    		
    	return ContextBFF.buildResponse(200,preferenzaUtente.getServizi());
    }
    
    @POST
    @Path("{idServizio}")
    public Response addPreferenza(@PathParam("idServizio") Integer idServizio, @Context HttpServletRequest request) {
    	String cf = ContextBFF.getCodiceFiscaleShib(request);
    	String tenant = ContextBFF.getTenant(request);
    	
    	PreferenzaUtente preferenzaUtente = service.addPreferenza(cf, tenant, idServizio);
    	
    	if(preferenzaUtente == null) {
    		return ContextBFF.buildResponseError(400, "Preferenza già aggiunta");
    	}
    	
    	return ContextBFF.buildResponse(200, "Preferenza aggiunta con Successo");
    }
    
    @DELETE
    @Path("{idServizio}")
    public Response removePreferenza(@PathParam("idServizio") Integer idServizio, @Context HttpServletRequest request) {
    	String cf = ContextBFF.getCodiceFiscaleShib(request);
    	String tenant = ContextBFF.getTenant(request);
    	
    	boolean deleted = service.deletePreferenza(cf, idServizio, tenant);
    	
    	if(deleted)
    		return ContextBFF.buildResponse(200, "Preferenza eliminata con successo");
    	
    	return ContextBFF.buildResponseError(400, "Nessuna corrispondenza trovata");
    }
	
}
