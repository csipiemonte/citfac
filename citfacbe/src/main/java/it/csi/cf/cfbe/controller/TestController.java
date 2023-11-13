/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api-public/test")
public class TestController {

    @GET
    @Produces("application/json")
    public String test() {
      return "hello world!!!";
    }
    
    @GET
    @Path("/host")
    @Produces("application/json")
    public String host(@Context HttpServletRequest req) {
      return ContextBFF.getTenant(req);
    }
    
   
    @GET
    @Path("/hostname")
    @Produces("application/json")
    public Map<String,String> hostname(@Context HttpServletRequest req, @Context UriInfo uriInfo) {
      Map<String,String> m = new HashMap<>();
      m.put("requestURI", req.getRequestURI());
      m.put("remoteHost", req.getRemoteHost());
      m.put("remotePort", ""+req.getRemotePort());
      m.put("remoteUser", ""+req.getRemoteUser());
      m.put("remoteAddr", req.getRemoteAddr());
      m.put("localName", req.getLocalName());
      m.put("localPort", ""+req.getLocalPort());
      
      m.put("baseUri", uriInfo.getBaseUri().toString());
      m.put("baseUri.host", uriInfo.getBaseUri().getHost());
      
      
      StringBuilder headersDesc = new StringBuilder();
      Enumeration<String> headerNames = req.getHeaderNames();
      while(headerNames.hasMoreElements()) {
    	  String headerName = headerNames.nextElement();
    	  headersDesc.append(headerName+ "="+req.getHeader(headerName)+";\n");
      }
      
      m.put("headers", headersDesc.toString());
      
      //x-forwarded-host=poc-cittafacile.csi.it;x-forwarded-server=dv-ap1-fe-poccitfac.site02.nivolapiemonte.it;
      
    	 
      return m;
    }
    
    @GET
    @Path("/multi")
    public Response testMultipartRequest(@RequestParam("test") String test) {
    	return ContextBFF.buildResponse(200, test);
    }
}