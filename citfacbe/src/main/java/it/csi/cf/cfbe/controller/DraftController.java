/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.Asset;
import it.csi.cf.cfbe.entity.BozzaRichiesta;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.model.dto.BozzaRichiestaDto;
import it.csi.cf.cfbe.services.BozzeService;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class DraftController {
	
	@Autowired
	private BozzeService service;
	
	@GET
	@Path("/drafts")
	public Response getAllBozze(@Context HttpServletRequest request, @QueryParam("asset") Integer idAsset) {
		ConfigComune comune = ContextBFF.getConfigComune(request);
		String cf = ContextBFF.getCodiceFiscaleShib(request);
		List<BozzaRichiesta> bozze;
		
		if(idAsset == null) {
			bozze = service.getAllBozze(cf, comune);
		} else {
			bozze = service.getAllBozzeOfAsset(cf, idAsset, comune.getId());
		}
		
		List<BozzaRichiestaDto> dtos = new ArrayList<>();
		
		for(BozzaRichiesta bozza : bozze) {
			BozzaRichiestaDto dto = new BozzaRichiestaDto(bozza.getId(), bozza.getAsset().getId(), bozza.getBody(), bozza.getDataCreazione());
					
			dtos.add(dto);
		}
		
		return ContextBFF.buildResponse(200, dtos);
	}

	@POST
	@Path("/draft")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBozza(BozzaRichiestaDto dto, @Context HttpServletRequest request) {
		ConfigComune comune = ContextBFF.getConfigComune(request);
		
		BozzaRichiesta bozzaRichiesta = 
				BozzaRichiesta.builder()
								.codiceFiscale(ContextBFF.getCodiceFiscaleShib(request))
								.asset(new Asset(dto.getIdAsset()))
								.comune(comune)
								.body(dto.getBody())
								.dataCreazione(new Timestamp(System.currentTimeMillis()))
								.build();
		try {
			bozzaRichiesta = service.addBozza(bozzaRichiesta);
			return ContextBFF.buildResponse(201, bozzaRichiesta);
		} catch (Exception e) {
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
	}
	
	@PUT
	@Path("/draft/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateBozza(BozzaRichiestaDto dto, @Context HttpServletRequest request, @PathParam("id") Integer idBozza) {
		ConfigComune comune = ContextBFF.getConfigComune(request);
		
		BozzaRichiesta bozzaRichiesta = 
				BozzaRichiesta.builder()
								.id(idBozza)
								.codiceFiscale(ContextBFF.getCodiceFiscaleShib(request))
								.asset(new Asset(dto.getIdAsset()))
								.comune(comune)
								.body(dto.getBody())
								.dataCreazione(new Timestamp(System.currentTimeMillis()))
								.build();
		
		try {
			bozzaRichiesta = service.updateBozza(bozzaRichiesta);
			
			if(bozzaRichiesta == null)
				return ContextBFF.buildResponseError(403, "Id bozza e codice fiscale non coerenti");
				
			return ContextBFF.buildResponse(200, bozzaRichiesta);
		} catch (Exception e) {
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
	}
	
	@DELETE
	@Path("/draft/{id}")
	public Response deleteBozza(@PathParam("id") Integer idBozza) {
		int deleteCount = service.deleteBozza(idBozza);
		
		if(deleteCount > 0) 
			return ContextBFF.buildResponse(200, "Bozza eliminata con successo");
		
		return ContextBFF.buildResponseError(404, "Bozza non trovata");
	}

}
