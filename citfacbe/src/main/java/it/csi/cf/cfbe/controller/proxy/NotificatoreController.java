/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller.proxy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.JwtCredentials;
import it.csi.cf.cfbe.repository.JwtRepository;
import it.csi.cf.cfbe.services.NotificatoreService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api/notificatore")
public class NotificatoreController {
	
	@Autowired
	private NotificatoreService service;
	
	@Autowired
	private JwtRepository jwtRepository;
	
	@GET
	@Path("/preferenze/{param:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxyGet(@Context HttpServletRequest req, @PathParam("param") String param) throws IOException {
		String queryString = req.getQueryString() != null ? "?" +req.getQueryString() : "";
		String baseUrl = service.getUriPreferenze() + param + queryString;
		return service.doGet(req, baseUrl, getJwt(req));
	}
	
	@GET
	@Path("/preferenze/services")
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxGetServices (@Context HttpServletRequest req) throws IOException {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		String baseUrl = service.getUriPreferenze() + "services?filter={ \"tags\": { \"match\": \""+ comune.getCodiceComune() + " citcaf\" } }";
		return service.doGet(req, baseUrl, getJwt(req));
	}
	
	@PUT
	@Path("/preferenze/{param:.*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxyPut(@Context HttpServletRequest req, @PathParam("param") String param) throws IOException {
		String queryString = req.getQueryString() != null ? "?" +req.getQueryString() : "";
		String baseUrl = service.getUriPreferenze() + param + queryString;
		return service.doPut(req, baseUrl, getJwt(req));
	}
	
	@PUT
	@Path("/preferenze/users/{cf}/terms")
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxyAcceptTerms(@Context HttpServletRequest req, @PathParam("cf") String cf) {
		
		return service.acceptTerms(req, cf);
		
	}
	
	@DELETE
	@Path("/preferenze/{param:.*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxyDelete(@Context HttpServletRequest req, @PathParam("param") String param) throws IOException {;
		String baseUrl = service.getUriPreferenze() + param;
		return service.doDelete(req, baseUrl, getJwt(req));
	}
	
	@GET
	@Path("/messagestore/{param:.*}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxyGetMessage(@Context HttpServletRequest req, @PathParam("param") String param) throws IOException {
		String queryString = req.getQueryString() != null ? "?" +req.getQueryString() : "";
		String baseUrl = service.getUriMEssageStore() + param + queryString;
		return service.doGet(req, baseUrl, getJwt(req));
	}
	
	@PUT
	@Path("/messagestore/{param:.*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxyPutMEssage(@Context HttpServletRequest req, @PathParam("param") String param) throws IOException {
		String queryString = req.getQueryString() != null ? "?" +req.getQueryString() : "";
		String baseUrl = service.getUriMEssageStore() + param + queryString;
		return service.doPut(req, baseUrl, getJwt(req));
	}
	
	@DELETE
	@Path("/messagestore/{param:.*}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response proxyDeleteMEssage(@Context HttpServletRequest req, @PathParam("param") String param) throws IOException {
		String baseUrl = service.getUriMEssageStore() + param;
		return service.doDelete(req, baseUrl, getJwt(req));
	}
	
	private String getJwt(HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, "NOTIFICATIONS");
		
		return jwt == null ? null : jwt.getJwt();
	}

}
