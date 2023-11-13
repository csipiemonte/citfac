/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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

import it.csi.cf.cfbe.entity.Feedback;
import it.csi.cf.cfbe.model.FeedbackMoonDto;
import it.csi.cf.cfbe.model.dto.FeedbackDto;
import it.csi.cf.cfbe.services.FeedbackService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api-public")
@Produces(MediaType.APPLICATION_JSON)
public class FeedbackController {
	
	@Autowired
	private FeedbackService service;
	
	
	@GET
	@Path("/feedback/{codiceComune}")
	public Response getComuneFeedback (@PathParam("codiceComune") String codiceComune) {
		List<FeedbackDto> feedbacks = service.getComuneFeedback(codiceComune);
		
		return ContextBFF.buildResponse(200, feedbacks);
	}
	
	@POST
	@Path("/feedback")
	public Response addFeedback (Feedback feedback, @Context HttpServletRequest req) {
		
		String mediaType = req.getHeader("Content-Type");
		
		if(!mediaType.equals(MediaType.APPLICATION_JSON)) {
			return ContextBFF.buildResponseError(415, "unsupported content-type");
		}
		
		return service.addFeedback(feedback, req);
		
//		if(!saved) {
//			return ContextBFF.buildResponse(200, "{\"status\":200, \"title\": \"success\"}");
//		}
//		
//		return ContextBFF.buildResponse(201, "{\"status\":201, \"title\": \"success\"}");
	}
	
	@POST
	@Path("/feedback-moon")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addFeedbackMoon (FeedbackMoonDto dto, @Context HttpServletRequest req) {
		try {			
			return service.addFeedbackMoon(dto, req);
		} catch (Exception e) {
			e.printStackTrace();
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
	}


}
