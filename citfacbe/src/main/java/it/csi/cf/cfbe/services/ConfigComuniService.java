/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.Asset;
import it.csi.cf.cfbe.entity.Cms;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Credenziali;
import it.csi.cf.cfbe.entity.FirebaseCredentials;
import it.csi.cf.cfbe.entity.Menu;
import it.csi.cf.cfbe.entity.Servizio;
import it.csi.cf.cfbe.enumeration.Role;
import it.csi.cf.cfbe.filter.IrideIdAdapterFilter;
import it.csi.cf.cfbe.model.dto.AssetDto;
import it.csi.cf.cfbe.model.dto.ConfigComuneDto;
import it.csi.cf.cfbe.model.dto.MaintenanceDto;
import it.csi.cf.cfbe.model.dto.ServiziDto;
import it.csi.cf.cfbe.model.dto.comune.ConfigComuneFullDto;
import it.csi.cf.cfbe.model.dto.comune.ConfigComuneFullWithApiKeyDto;
import it.csi.cf.cfbe.model.identity.Utente;
import it.csi.cf.cfbe.repository.ConfigComuneRepository;
import it.csi.cf.cfbe.repository.CredenzialiRepository;
import it.csi.cf.cfbe.repository.FireBaseCredentialsRepository;
import it.csi.cf.cfbe.repository.MenuRepository;
import it.csi.cf.cfbe.repository.ServiziRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigComuniService {
	
	@Autowired
	private ConfigComuneRepository configComuneRepository;
	
	@Autowired
	private ServiziRepository serviziRepository;
	
	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private FireBaseCredentialsRepository fireBaseRepository;
	
	@Autowired
	private CredenzialiRepository credenzialiRepository;
	
	@Autowired
	private CmsService cmsService;
	
	public ConfigComuneDto getConfig(HttpServletRequest req) {
		String tenant = ContextBFF.getTenant(req);
		
		ConfigComune configComune = configComuneRepository.findByTenant(tenant);
		
		if(configComune == null) {
			return null;
		}
		
		FirebaseCredentials firebase = fireBaseRepository.findByComune(configComune);
		Cms cms = cmsService.getCms(configComune);
		
		ConfigComuneDto dto = new ConfigComuneDto();
		
		BeanUtils.copyProperties(configComune, dto);
		
		List<AssetDto> assetsDto = new ArrayList<>();
		
		for(Asset a : configComune.getAssets()) {
			AssetDto assetDto = new AssetDto();
			
			BeanUtils.copyProperties(a, assetDto);
			
			assetsDto.add(assetDto);
		}
		
		dto.setFirebase(firebase);
		
		dto.setAssets(assetsDto);
		
		dto.setCms(cms != null);
		
		return dto;
	}
	
	public Response getMenu (HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		Cms cms = cmsService.getCms(comune);
		
		String urlMenu = cms != null ? cms.getUrlMenu() : null;
		
		if(urlMenu != null) {
	    	URI uri = URI.create(urlMenu);
	    	return Response.status(302).location(uri).build();
		} else {
			Menu menu = menuRepository.findByComune(comune);
			
			if(menu == null) {
				return ContextBFF.buildResponseError(404, "menu non trovato");
			}
			
			return ContextBFF.buildResponse(200, menu.getBody());
		}
	}
	
	public Response getService (HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		Cms cms = cmsService.getCms(comune);
		
		
		String urlServizi = cms != null ? cms.getUrlServizi() : null;
		
		if(urlServizi != null) {
	    	URI uri = URI.create(urlServizi);
	    	return Response.status(302).location(uri).build();
		} else {
			List<Servizio> servizi = serviziRepository.findByComune(comune);
			List<ServiziDto> serviziDto = new ArrayList<>();
			
			for(Servizio servizio : servizi) {
				ServiziDto dto = new ServiziDto();
				
				BeanUtils.copyProperties(servizio, dto);
				
				serviziDto.add(dto);
			}
			
			return ContextBFF.buildResponse(200, serviziDto);
		}
	}
	
	//per gestione configuratore
	
	public ConfigComuneFullDto getConfigFull(HttpServletRequest req, Integer id) {
		
		Utente utente = (Utente) req.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
		
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune != null) {
			ConfigComuneFullDto dto = utente.getRuolo() == Role.SUPER_USER ? new ConfigComuneFullWithApiKeyDto() : new ConfigComuneFullDto();
			
			Cms cms = cmsService.getCms(comune);
			
			BeanUtils.copyProperties(comune, dto);
			
			dto.setCms(cms);
			
			return dto;
		}
		
		return null;
	}
	
	public List<ConfigComuneFullDto> getAllConfig (HttpServletRequest req) {
		List<ConfigComune> comuni = configComuneRepository.findAll();
		List<ConfigComuneFullDto> comuniDto = new ArrayList<>();
		
		Utente utente = (Utente) req.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
		
		for(ConfigComune comune : comuni) {
			Cms cms = cmsService.getCms(comune);
			
			ConfigComuneFullDto dto = utente.getRuolo() == Role.SUPER_USER ? new ConfigComuneFullWithApiKeyDto() : new ConfigComuneFullDto();
			
			BeanUtils.copyProperties(comune, dto);
			
			dto.setCms(cms);
			
			comuniDto.add(dto);
		}
		
		return comuniDto;
	}
	
	
	public Response addNewConfig(ConfigComune newComune) {
		ConfigComune comune = configComuneRepository.findByCodiceComune(newComune.getCodiceComune());

	    if (comune != null) {
	        return ContextBFF.buildResponseError(400, "Comune già presente");
	    }
	    
	    if(newComune.getId() != null) newComune.setId(null); 

	    return ContextBFF.validateAndSave(newComune, configComuneRepository, false);
	}
	
	public Response updateConfigFull(ConfigComune comune, Integer id, HttpServletRequest req) {
		ConfigComune oldComune = ContextBFF.getConfigComune(req, id); 
		
		if (oldComune == null) {
	        return ContextBFF.buildResponseError(404, "comune non trovato");
	    }
		
		comune.setId(oldComune.getId());
		
		return ContextBFF.validateAndSave(comune, configComuneRepository, true);
	}
	
	public Response updateConfig(ConfigComune comune, Integer id, HttpServletRequest req) {
		ConfigComune oldComune = ContextBFF.getConfigComune(req, id); 
		
		if (oldComune == null) {
	        return ContextBFF.buildResponseError(404, "comune non trovato");
	    }
		
		ConfigComune newConfig;
		
		try {
			newConfig = mergeObjects(oldComune, comune);
		} catch (IllegalAccessException | NoSuchFieldException e) {
			return ContextBFF.buildResponseError(422, e.getMessage());
		}
		
		return ContextBFF.validateAndSave(newConfig, configComuneRepository, true);
	}
	
	public Response deleteConfig(String codiceComune) {
		ConfigComune comune = configComuneRepository.findByCodiceComune(codiceComune);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "comune non trovato");
		}
		
		configComuneRepository.deleteById(comune.getId());
		
		return ContextBFF.buildResponse(200, "{\"status:\":\"deleted\"}");
	}
	
	public Response updateMaintenance (HttpServletRequest req, Integer tenantId, MaintenanceDto dto) {
		boolean status = dto.isStatus();
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "comune non trovato");
		}
		
		if(comune.isMaintenance() == status) {
			return ContextBFF.buildResponseError(400, "Stato manutenzione già impostato");
		}
		
		if(status) {
			
			log.debug("vado in manutenzione {}", status);
		} else {
			log.debug("rimuovo manutenzione");;
		}
		
		if(status) {
			Credenziali credenziali = credenzialiRepository.findByComuneAndApi(comune, "MANUTENZIONE");
			
			if(credenziali == null) {
				return ContextBFF.buildResponseError(500, "Credenziali manutenzione non impostate");
			}			
		}
		
		comune.setMaintenance(status);
		
		try {			
			ContextBFF.validateAndSave(comune, configComuneRepository, true);
		} catch (Exception e) {
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
		return ContextBFF.buildResponse(200, "Stato manutenzione aggiornato");
	}
	
	private ConfigComune mergeObjects(ConfigComune target, ConfigComune source) throws
	    IllegalAccessException, NoSuchFieldException {
		
		for (Field field : source.getClass().getDeclaredFields()) {
		    field.setAccessible(true);
		    String name = field.getName();
		    Object value = field.get(source);
		
		    if (null != value) {
		
		        Field destField = target.getClass().getDeclaredField(name);
		        destField.setAccessible(true);
		
		        destField.set(target, value);
		    }
		}
		
		return target;
	}
	
}
