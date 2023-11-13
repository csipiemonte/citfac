/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.services.proxy.MoonProxyService;

@Service
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/moon")
public class MoonController {
	
	@Autowired
	private MoonProxyService proxyService;
	
	@GET
	@Path("/{path:.*}")
	public Response proxtGet(@Context HttpServletRequest req, @PathParam("path") String path) {
		return proxyService.proxyGet(req, path);
	}
	
	@DELETE
	@Path("/{path:.*}")
	public Response proxyDelete(@Context HttpServletRequest req, @PathParam("path") String path) {
		return proxyService.proxyDelete(req, path);
	}
	
	@GET
	@Path("/moon-identita")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMoonIdentita (@Context HttpServletRequest req) {
		return proxyService.getIdentitaMoon(req);
	}

}
