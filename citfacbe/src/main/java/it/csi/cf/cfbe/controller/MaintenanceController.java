/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api-public/maintenance")
public class MaintenanceController {

	@POST
	public Response getAuth (@Context HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(400, "Comune non presente");
		}
		
		try {
			if(ContextBFF.isValidMaintenanceCredential(req, comune)) {
				return ContextBFF.buildResponse(200, "utente autorizzato");
			}
		} catch (UnsupportedEncodingException e) {
			return ContextBFF.buildResponseError(500, "Errore interno del server durante la validazione, " + e.getMessage());
		}
		
		return ContextBFF.buildResponse(403, "Utente non autorizzato");
	}
	
}
