/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services.utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Credenziali;
import it.csi.cf.cfbe.filter.IrideIdAdapterFilter;
import it.csi.cf.cfbe.model.dto.ErrorDto;
import it.csi.cf.cfbe.model.identity.Utente;
import it.csi.cf.cfbe.repository.ConfigComuneRepository;
import it.csi.cf.cfbe.repository.CredenzialiRepository;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ContextBFF {

  private static Validator validator;
  private static ConfigComuneRepository comuneRepository;
  private static CredenzialiRepository credenzialiRepository;

  @Autowired
  private void setValidator(Validator validator) {
    ContextBFF.validator = validator;
  }

  @Autowired
  private void setConfigComuneRepository(ConfigComuneRepository comuneRepository) {
    ContextBFF.comuneRepository = comuneRepository;
  }
  
  @Autowired
  private void setCredenzialiRepository(CredenzialiRepository credenzialiRepository) {
	  ContextBFF.credenzialiRepository = credenzialiRepository;
  }

  public static String getTenant(HttpServletRequest req) {
    String portale =  req.getHeader("x-forwarded-host");

    if(portale == null || portale.isBlank()) {
      //In modalità DEV uso Pavia.
      portale =  "pavia-poc-cittafacile.csi.it";
    }

    return portale;
  }

  public static ConfigComune getConfigComune(HttpServletRequest req) {
    String tenant = ContextBFF.getTenant(req);
    ConfigComune configComune = comuneRepository.findByTenant(tenant);
    return configComune;
  }
  
  public static ConfigComune getConfigComune(HttpServletRequest req, Integer id) {

	  if(id == 0) {
		  return getConfigComune(req);
	  }
	  
	  Optional<ConfigComune> configComune = comuneRepository.findById(id);
    
	  if(configComune.isEmpty())
		  return null;
    
	  return configComune.get();
  }

  public static String getCodiceFiscaleShib(HttpServletRequest req) {
    Utente utente = (Utente) req.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
    return utente.getCodFisc();
  }

  public static Response buildResponse(int status, Object entity) {
    ResponseBuilder builder = Response.noContent();

    return builder.status(status).entity(entity).build();
  }
  
 public static Response buildResponseError(int status, String title) {
	  
	 ErrorDto error = new ErrorDto(status, title, null);
	  
	  return buildResponse(status, error);
  }
  
  public static Response buildResponseError(int status, String title, List<String> message) {
	  
	  ErrorDto error = new ErrorDto(status, title , message);
	  
	  return buildResponse(status, error);
  }

  public static <T> List<String> validateObject(T object) {
    List<String> errors = new ArrayList<String>();

    Set<ConstraintViolation<T>> violations = validator.validate(object);
    if (!violations.isEmpty()) {
      for (ConstraintViolation<T> violation : violations) {
        errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
      }
      return errors;
    }

    return null;
  }
  
  public static <T> Response validateAndSave(T obj, JpaRepository<T, Integer> repository, boolean isUpdate) {
	List<String> validationError = validateObject(obj);
	    
    if(validationError != null) {
    	return buildResponseError(400,"Errore di validazione" , validationError);
	}
    
    try {
    	obj = (T) repository.save(obj); 	
    } catch(DataIntegrityViolationException e) {
    	List<String> details = new ArrayList<String>();
    	details.add(e.getCause().getCause().getLocalizedMessage());
    	return buildResponseError(422, e.getCause().getMessage(), details );
    }
	    
    if(isUpdate) {
		return ContextBFF.buildResponse(200, obj);
	}
	
	return ContextBFF.buildResponse(201, obj);    
	
  }
  
  private static String[] extractAndDecodeHeader(String header) throws UnsupportedEncodingException {
      byte[] base64Token = header.substring(6).getBytes("UTF-8");
      byte[] decoded;
      try {
          decoded = Base64.getDecoder().decode(base64Token);
      } catch (Exception e) {
          throw new RuntimeException("Failed to decode basic authentication token");
      }

      String token  = "";
		try {
			token = new String(decoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return token.split(":");
  }
  
  public static boolean isValidMaintenanceCredential (HttpServletRequest req, ConfigComune comune) throws UnsupportedEncodingException {
		String requestCredential = req.getHeader("Authorization");
		
		if(requestCredential == null) {
			return false;
		}
		
		String[] decodedCredential = extractAndDecodeHeader(requestCredential);
		
		Credenziali credenziali = credenzialiRepository.findByComuneAndApi(comune, "MANUTENZIONE");
		
		return credenziali.getUsername().equals(decodedCredential[0]) && credenziali.getPassword().equals(decodedCredential[1]);
	}
}
