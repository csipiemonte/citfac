/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.Asset;
import it.csi.cf.cfbe.entity.BaseCredentials;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Credenziali;
import it.csi.cf.cfbe.entity.FirebaseCredentials;
import it.csi.cf.cfbe.entity.JwtCredentials;
import it.csi.cf.cfbe.entity.Regione;
import it.csi.cf.cfbe.enumeration.CredentialsType;
import it.csi.cf.cfbe.model.dto.CredenzialiDto;
import it.csi.cf.cfbe.repository.AssetRepository;
import it.csi.cf.cfbe.repository.CredenzialiRepository;
import it.csi.cf.cfbe.repository.FireBaseCredentialsRepository;
import it.csi.cf.cfbe.repository.JwtRepository;
import it.csi.cf.cfbe.repository.RegioniRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;

@Service
public class ConfigurationService {
	
	@Autowired
	private RegioniRepository regioniRepository;
	
	@Autowired
	private AssetRepository assetRepository;
	
	@Autowired
	private CredenzialiRepository credenzialiRepository;
	
	@Autowired
	private JwtRepository jwtRepository;
	
	@Autowired
	private FireBaseCredentialsRepository fireBaseRepository;
	
	public Response getCredenziali(HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "comune non trovato");
		
		List<BaseCredentials> credenziali = credenzialiRepository.findAllByComune(comune);	
		
		credenziali.addAll(jwtRepository.findAllByComune(comune));
				
		return ContextBFF.buildResponse(200, credenziali);
	}
	
	public Response getCredential(HttpServletRequest req, Integer id, Integer assetId) {
		Asset asset = getAsset(assetId);
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "comune non trovato");
		
		if(asset == null)
			return ContextBFF.buildResponseError(404, "asset non trovato");
		
		if(asset.getTipoCredenziali() == null) {
			return ContextBFF.buildResponseError(400, "assent non supporta credenziali");
		} else if(asset.getTipoCredenziali() == CredentialsType.BASIC) {
			return getCredenziale(comune, asset.getCodice());
		} else {
			return getJwtCredentials(comune, asset.getCodice());	
		}
	}
	
	private Response getCredenziale(ConfigComune comune, String type) {		
		Credenziali credenziale = credenzialiRepository.findByComuneAndApi(comune, type);

		if(credenziale == null) 
			return ContextBFF.buildResponseError(404, "credenziali non presenti");
	
		credenziale.setPassword(null);
			
		CredenzialiDto dto = new CredenzialiDto(); 
		BeanUtils.copyProperties(credenziale, dto);
		
		return ContextBFF.buildResponse(200, dto);
	}
	
	private Response getJwtCredentials(ConfigComune comune, String type) {
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, type);
		
		if(jwt == null) 
			return ContextBFF.buildResponseError(404, "credenziali non presenti");
		
		return ContextBFF.buildResponse(200, jwt);
	}
	
	public Response addCredentials(HttpServletRequest req, CredenzialiDto dto, Integer id, Integer assetId) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		String codice  = "";
		CredentialsType type = CredentialsType.BASIC;

		if(assetId != 0) {
			Asset asset = getAsset(assetId);
			
			if(asset == null)
				return ContextBFF.buildResponseError(404, "asset non trovato");
			
			codice = asset.getCodice();
			type = asset.getTipoCredenziali(); 
		} else {
			codice = "MANUTENZIONE";
		}
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "comune non trovato");
		
		if(type == null) {
			return ContextBFF.buildResponseError(400, "assent non supporta credenziali");
		} else if(type == CredentialsType.BASIC) {
			return addCredenziali(comune, dto, codice);
		} else {
			return addJwtCredentials(comune, dto, codice);
		}
	}
	
	private Response addCredenziali(ConfigComune comune, CredenzialiDto dto, String type) {
		
		Credenziali credenziali = credenzialiRepository.findByComuneAndApi(comune, type);
		
		if(credenziali != null) {			
			return ContextBFF.buildResponseError(400, "credenziali già presenti");
		}
		
		if(dto.getUsername() == null) {
			return ContextBFF.buildResponseError(404, "Username non presente");
		}
		
		if(dto.getPassword() == null) {
			return ContextBFF.buildResponseError(404, "Password non presente");
		}
		
		credenziali = new Credenziali();
		
		BeanUtils.copyProperties(dto, credenziali);
		
		credenziali.setComune(comune);
		credenziali.setApi(type);
		
		return ContextBFF.validateAndSave(credenziali, credenzialiRepository, false);
		
	}
	
	private Response addJwtCredentials(ConfigComune comune, CredenzialiDto dto, String type) {
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, type);
		if(jwt != null) {			
			return ContextBFF.buildResponseError(400, "credenziali già presenti");
		}
		
		if(dto.getJwt() == null) {
			return ContextBFF.buildResponseError(404, "Jwt non presente");
		}
		
		jwt = new JwtCredentials();
		
		jwt.setJwt(dto.getJwt());
		jwt.setComune(comune);
		jwt.setApi(type);
		
		return ContextBFF.validateAndSave(jwt, jwtRepository, false);
	}

	public Response updateCredentials (HttpServletRequest req, CredenzialiDto dto, Integer id, Integer assetId) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		
		String codice  = "";
		CredentialsType type = CredentialsType.BASIC;

		if(assetId != 0) {
			Asset asset = getAsset(assetId);
			
			if(asset == null)
				return ContextBFF.buildResponseError(404, "asset non trovato");
			
			codice = asset.getCodice();
			type = asset.getTipoCredenziali(); 
		} else {
			codice = "MANUTENZIONE";
		}
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "comune non trovato");
		
		if(type == null) {
			return ContextBFF.buildResponseError(400, "assent non supporta credenziali");
		} else if(type == CredentialsType.BASIC) {
			return updateCredenziali(comune, dto, codice);
		} else {
			return updateJwtCredential(comune, dto, codice);
		}
		
	}
	
	private Response updateCredenziali(ConfigComune comune, CredenzialiDto dto, String type) {
		Credenziali credenziali = credenzialiRepository.findByComuneAndApi(comune, type);
		
		if(credenziali == null) 
			return ContextBFF.buildResponseError(404,"credenziali non presenti");
		
		if(dto.getUsername() == null) {
			return ContextBFF.buildResponseError(404, "Username non presente");
		}
		
		if(dto.getPassword() == null) {
			return ContextBFF.buildResponseError(404, "Password non presente");
		}
		
		BeanUtils.copyProperties(dto, credenziali);
		
		credenziali.setComune(comune);
		credenziali.setApi(type);
		
		return ContextBFF.validateAndSave(credenziali, credenzialiRepository, true);
		
	}
	
	private Response updateJwtCredential(ConfigComune comune, CredenzialiDto dto, String type) {
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, type);
		
		if(jwt == null) 
			return ContextBFF.buildResponseError(404,"credenziali non presenti");
		
		if(dto.getJwt() == null) {
			return ContextBFF.buildResponseError(404, "Jwt non presente");
		}
		
		BeanUtils.copyProperties(dto, jwt);
		
		jwt.setComune(comune);
		jwt.setApi(type);
		
		return ContextBFF.validateAndSave(jwt, jwtRepository, true);
		
	}
	
	public Response deleteCredentials (HttpServletRequest req, Integer id, Integer assetId) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		Asset asset = getAsset(assetId);
		
		if(comune == null)
			return ContextBFF.buildResponseError(404, "comune non trovato");
		
		if(asset == null)
			return ContextBFF.buildResponseError(404, "asset non trovato");
		
		if(asset.getTipoCredenziali() == null) {
			return ContextBFF.buildResponseError(400, "assent non supporta credenziali");
		} else if(asset.getTipoCredenziali() == CredentialsType.BASIC) {
			return deleteCredenziali(comune, asset.getCodice());
		} else {
			return deleteJwtCredentials(comune, asset.getCodice());
		}
	}
	
	private Response deleteCredenziali(ConfigComune comune, String type) {		
		Credenziali credenziali = credenzialiRepository.findByComuneAndApi(comune, type);
		
		if(credenziali == null) 
			return ContextBFF.buildResponseError(404, "credenziali non presenti");
		
		credenzialiRepository.delete(credenziali);
		
		return ContextBFF.buildResponse(200, "{\"status\": \"deleted\"}");
		
	}
	
	private Response deleteJwtCredentials(ConfigComune comune, String type) {		
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, type);
		
		if(jwt == null) 
			return ContextBFF.buildResponseError(404, "credenziali non presenti");
		
		jwtRepository.delete(jwt);
		
		return ContextBFF.buildResponse(200, "{\"status\": \"deleted\"}");
		
	}
	
	public Response getFirebaseCredentials (HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		FirebaseCredentials firebase = fireBaseRepository.findByComune(comune);
		
		if(firebase == null) {
			return ContextBFF.buildResponseError(404, "Credentiali Firebase non presenti");
		}
		
		return ContextBFF.buildResponse(200, firebase);
	}
	
	public Response addFirebaseCredentials (HttpServletRequest req, Integer id, FirebaseCredentials credentials) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		FirebaseCredentials firebase = fireBaseRepository.findByComune(comune);
		
		if(firebase != null) {
			return ContextBFF.buildResponseError(400, "Credentiali Firebase già presenti");
		}
		
		credentials.setId(null);
		credentials.setComune(comune);
		
		return ContextBFF.validateAndSave(credentials, fireBaseRepository, false);
	}
	
	public Response updateFirebaseCredentials (HttpServletRequest req, Integer id, FirebaseCredentials credentials) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		FirebaseCredentials firebase = fireBaseRepository.findByComune(comune);
		
		if(firebase == null) {
			return ContextBFF.buildResponseError(400, "Credentiali Firebse non presenti");
		}
		
		credentials.setId(firebase.getId());
		credentials.setComune(comune);
		
		return ContextBFF.validateAndSave(credentials, fireBaseRepository, false);
	}
	
	public Response deleteFirebaseCredentials (HttpServletRequest req, Integer id) {
		ConfigComune comune = ContextBFF.getConfigComune(req, id);
		FirebaseCredentials firebase = fireBaseRepository.findByComune(comune);
		
		if(firebase == null) 
			return ContextBFF.buildResponseError(404, "credenziali non presenti");
		
		fireBaseRepository.delete(firebase);
		
		return ContextBFF.buildResponse(200, "{\"status\": \"deleted\"}");
	}
	
	

	public Response getAllRegioni() {
		List<Regione> regioni = regioniRepository.findAll();
		
		return ContextBFF.buildResponse(200, regioni);
	}
	
	public Response getAllAsset () {
		List<Asset> assets = assetRepository.findAll();
		
		return ContextBFF.buildResponse(200, assets);
	}
	
	public Asset getAsset (Integer id) {
		Optional<Asset> asset = assetRepository.findById(id);
		
		if(asset.isEmpty()) {
			return null;
		}
		
		return asset.get();
	}
	
}
