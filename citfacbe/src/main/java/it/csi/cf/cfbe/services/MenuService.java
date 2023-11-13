/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.Cms;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Menu;
import it.csi.cf.cfbe.repository.MenuRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
public class MenuService {
	
	@Autowired
	private MenuRepository repository;
	
	@Autowired
	private CmsService cmsService;
	
	public Response getMenu(HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non presente");
		}
		
		Cms cms = cmsService.getCms(comune);
		
		String urlMenu = cms != null ? cms.getUrlMenu() : null; 
		
		if(urlMenu != null) {
			URI uri = URI.create(urlMenu);
	    	return Response.status(302).location(uri).build();
		}
		
		Menu menu = repository.findByComune(comune);
		
		if(menu == null) {
			return ContextBFF.buildResponseError(404, "Menu non presente");
		}
		
		return ContextBFF.buildResponse(200, menu.getBody());
	}
	
	public Response addMenu(HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non presente");
		}
		
		Cms cms = cmsService.getCms(comune);
		
		if(cms != null && cms.getUrlMenu() != null) {
			return ContextBFF.buildResponseError(400, "Url Menu presente");
		}
		
		Menu menu = repository.findByComune(comune);
		
		if(menu != null) {
			return ContextBFF.buildResponseError(400, "Menu già aggiunto");
		}
		
		try {
			String bodyString = req.getReader().lines().collect(Collectors.joining());
			
			if(bodyString.length() == 0) {
				return ContextBFF.buildResponseError(400, "No content body");
			}
			
			menu = new Menu(null, bodyString, comune);
			
			repository.save(menu);
			
			return ContextBFF.buildResponse(201, menu.getBody());
		} catch (IOException e) {
			return ContextBFF.buildResponseError(500, "Internal server error");
		}
	}
	
	public Response updateMenu(HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non presente");
		}
		
		Cms cms = cmsService.getCms(comune);
		
		if(cms != null && cms.getUrlMenu() != null) {
			return ContextBFF.buildResponseError(400, "Url Menu presente");
		}
		
		Menu menu = repository.findByComune(comune);
		
		if(menu == null) {
			return ContextBFF.buildResponseError(404, "Menu non presente");
		}
		
		try {
			String bodyString = req.getReader().lines().collect(Collectors.joining());
			
			if(bodyString.length() == 0) {
				return ContextBFF.buildResponseError(400, "No content body");
			}
			
			menu.setBody(bodyString);
			
			repository.save(menu);
			
			return ContextBFF.buildResponse(200, menu.getBody());
		} catch (IOException e) {
			return ContextBFF.buildResponseError(500, "Internal server error");
		}
	}
}
