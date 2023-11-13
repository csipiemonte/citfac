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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.model.dto.ConfigComuneDto;
import it.csi.cf.cfbe.services.ConfigComuniService;
import it.csi.cf.cfbe.services.ServiceDetailService;

@Service
@Path("api-public/configComune")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigComuneController {

	@Autowired
	private ConfigComuniService service;
	
	@Autowired
	private ServiceDetailService serviceDetailService;
	
	@GET
	@Path("/fromtenant")
	public ConfigComuneDto getConfigComuneFromReq(@Context HttpServletRequest req) {
		return service.getConfig(req);
	}
	
	@GET
    @Path("/menu")
    public Response menu(@Context HttpServletRequest request) {
    	return service.getMenu(request);
    }
	
	@GET
    @Path("/servizi")
    public Response servizi(@Context HttpServletRequest request) {
    	return service.getService(request);
    }

	@GET
	@Path("/dettaglioServizi")
	public Response dettagliServizi (@Context HttpServletRequest request) {
		return serviceDetailService.getServicesDetails(request, 0);
	}

}
