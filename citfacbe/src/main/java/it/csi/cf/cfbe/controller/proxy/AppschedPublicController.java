/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller.proxy;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
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

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.services.proxy.AppschedProxyService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Produces(MediaType.APPLICATION_JSON)
@Path("/api-public/appsched")
public class AppschedPublicController {
	
	@Autowired
	private AppschedProxyService proxyService;
	
	@GET
	@Path("/{path:.*}")
	public Response proxyGet(@Context HttpServletRequest req , @PathParam("path") String path) throws IOException {
		boolean enabled = isEnabled(req);
		
		if(!enabled) {
			return ContextBFF.buildResponseError(403, "Not authorized");
		}
		
		return proxyService.proxyGet(req, path);
	}
	
	@POST
	@Path("/{path:.*}")
	public Response proxyPost(@Context HttpServletRequest req , @PathParam("path") String path) throws IOException {
		boolean enabled = isEnabled(req);
		
		if(!enabled) {
			return ContextBFF.buildResponseError(403, "Not authorized");
		}
		
		return proxyService.proxyPost(req, path);
	}
	
//	@PUT
//	@Path("/{path:.*}")
//	public Response proxyPut(@Context HttpServletRequest req , @PathParam("path") String path) throws IOException {
//		return proxyService.proxyPut(req, path);
//	}
//	
//	@DELETE
//	@Path("/{path:.*}")
//	public Response proxyDelete(@Context HttpServletRequest req , @PathParam("path") String path) throws IOException {
//		return proxyService.proxyDelete(req, path);
//	}
	
	private boolean isEnabled (HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		
		if(comune == null) {
			return false;
		}
		
		return comune.isAppschedPublic();
	}

}
