/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller.proxy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.enumeration.TipoRichiestaOTP;
import it.csi.cf.cfbe.services.OTPService;
import it.csi.cf.cfbe.services.proxy.CRMProxyService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Produces(MediaType.APPLICATION_JSON)
@Path("/api-public/crm")
public class CRMPublicController {
	
	@Autowired
	private OTPService otpService;
	
	@Autowired
	private CRMProxyService proxyService;
	
	@POST
	@Path("/tickets")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTicket(@QueryParam("otp") String codiceOTP, 
										@QueryParam("cf") String codiceFiscale, 
										@QueryParam("richiesta") TipoRichiestaOTP tipoRichiesta,
										@Context HttpServletRequest req) {
		if(otpService.checkValidOTP(codiceOTP, codiceFiscale, tipoRichiesta)) {
			return proxyService.proxyPost(req, "tickets", true);
		}
		
		return ContextBFF.buildResponseError(401, "autenticazione fallita");

	}
	
	@GET
	@Path("/services")
	public Response getServices(@Context HttpServletRequest req ) throws IOException {
		return proxyService.proxyGet(req, "services");
	}
	
	@GET
	@Path("/services/{id}")
	public Response getServicesSubItems(@Context HttpServletRequest req, @PathParam("id") String id ) throws IOException {
		return proxyService.proxyGet(req, "services/"+id);
	}

	@GET
	@Path("/knowledge_bases/{param:.*}")
	public Response getFaq(@Context HttpServletRequest req, @PathParam("param") String param ) throws IOException {
		return proxyService.proxyGet(req, "knowledge_bases/" + param);
	}
	
	@POST
	@Path("/knowledge_bases/search")
	public Response getFaqSearch(@Context HttpServletRequest req) throws IOException {
		return proxyService.proxyPost(req, "/knowledge_bases/search");
	}
	
}
