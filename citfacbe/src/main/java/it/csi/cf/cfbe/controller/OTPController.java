/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.OTP;
import it.csi.cf.cfbe.enumeration.TipoRichiestaOTP;
import it.csi.cf.cfbe.model.dto.otp.OTPRequestDto;
import it.csi.cf.cfbe.services.OTPService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api-public/otp")
@Produces(MediaType.APPLICATION_JSON_VALUE)
public class OTPController {
	
	@Autowired
	private OTPService service;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON_VALUE)
	public Response generateOTP(OTPRequestDto requestDto, @Context HttpServletRequest req) {
		try {
			return service.generateRequest(requestDto, req);

		} catch (IOException e ) {
			return ContextBFF.buildResponse(200, e.getMessage());
		}
	}
	
	@GET
	@Path("/verify")
	public Response isValid(@QueryParam("otp") String codiceOTP, 
			@QueryParam("cf") String codiceFiscale, 
			@QueryParam("richiesta") TipoRichiestaOTP tipoRichiesta) {
		return service.isValidOTP(codiceOTP, codiceFiscale.toUpperCase(), tipoRichiesta);
	}
	
	@GET
	@Path("/channels/sms/attempts-left")
	public Response countSmsRequest(@QueryParam("cf") String cf) {
		return ContextBFF.buildResponse(200, service.smsAttemptsLeft(cf));
	}
	
	@GET
	public List<OTP> getAll() {
		return service.getAll();
	}
	
}
