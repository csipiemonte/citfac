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

import it.csi.cf.cfbe.services.proxy.PPayProxyService;

@Service
@Produces(MediaType.APPLICATION_JSON)
@Path("/api-public/ppay")
public class PPayPublicController {
	
	@Autowired
	private PPayProxyService proxyService;
	
	@GET
	@Path("/paymenttypes")
	public Response getPaymentType(@Context HttpServletRequest req) throws IOException {
		return proxyService.proxyGet(req, "paymenttypes");
	}
	
	@POST
	@Path("/citizens/{cf}/paymenttypes/{paymenttype}/debtpositions")
	public Response postDebtPosition(@Context HttpServletRequest req, @PathParam("paymenttype") String type, @PathParam("cf") String cf) {
		String url = "citizens/" + cf + "/paymenttypes/" + type +"/debtpositions";
		return proxyService.proxyPost(req, url);
	}
	
	@POST
	@Path("/paymenttypes/{paymenttype}/payment")
	public Response postPayment(@Context HttpServletRequest req, @PathParam("paymenttype") String type) {
		String url = "paymenttypes/" + type +"/payment";
		return proxyService.proxyPost(req, url);
	}	
}
