/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.JwtCredentials;
import it.csi.cf.cfbe.entity.OTP;
import it.csi.cf.cfbe.enumeration.TipoRichiestaOTP;
import it.csi.cf.cfbe.exception.NotificatoreException;
import it.csi.cf.cfbe.model.dto.otp.OTPRequestDto;
import it.csi.cf.cfbe.model.dto.otp.OTPSmsAttemptDto;
import it.csi.cf.cfbe.model.notificatore.RequestInoltroOTP;
import it.csi.cf.cfbe.model.notificatore.email.PayloadEmail;
import it.csi.cf.cfbe.model.notificatore.email.RichiestaEmail;
import it.csi.cf.cfbe.model.notificatore.sms.PayloadSms;
import it.csi.cf.cfbe.model.notificatore.sms.RichiestaSms;
import it.csi.cf.cfbe.repository.ConfigComuneRepository;
import it.csi.cf.cfbe.repository.JwtRepository;
import it.csi.cf.cfbe.repository.OTPRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
@Slf4j
public class OTPService {
	
	private static final int DAILY_LIMIT_OTP_REQUEST = 3;
	private static final int OTP_EXP_TIME_MILLISEC =  5 * 60 * 1000;
	
	@Value("${notificatore-api-base-url}")
	private String apiBaseUrl;
	
	@Value("${notificatore-api-message-mb}")
	private String messageMb;
	
	private String uriNotifiche = "topics/messages";
	
	@Autowired
	private OTPRepository repository;
	
	@Autowired
	private ConfigComuneRepository comuneRepository;
	
	@Autowired
	private JwtRepository jwtRepository;
	
	public Response generateRequest(OTPRequestDto dto, HttpServletRequest req) throws IOException {
		//EFFETTUO PULIZIA DB DA OTP SCADUTI DA PIù DI 24 ORE
		repository.deleteExpiredOTP();
		
		//controllo validità dati
		if(!isValidData(dto)) {
			return ContextBFF.buildResponseError(400, "Richiesta non valida");
		}
		
		//effettuo controllo su quanti otp sms sono stati richiesti per l'utente
		if(dto.getTipoRichiesta() == TipoRichiestaOTP.SMS && 
				isExceededSmsOTPRequest(dto.getCodiceFiscale())) {
			return ContextBFF.buildResponseError(403, "Limite richieste giornaliere SMS OTP superato");
		}
		
		OTP otp = generateOTP(dto);
		
		try {
			inoltroRichiestaOTPNotificatore(dto, req, otp.getCodiceOTP());
		} catch (IOException | NotificatoreException e) {
			return ContextBFF.buildResponseError(500, e.getMessage());
		}
		
		repository.save(otp);
		
		return ContextBFF.buildResponse(200, otp);
	}
	
	public Response isValidOTP(String codiceOTP, String codiceFiscale, TipoRichiestaOTP tipoRichiesta) {
		
		OTP otp = repository.findByCodiceOTPAndCodiceFiscaleAndTipoRichiesta(codiceOTP, codiceFiscale, tipoRichiesta);
		
		if(otp != null) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			if(timestamp.after(otp.getExpirationDate()) || otp.isUsed()) {
				
				return ContextBFF.buildResponseError(403, "OTP Scaduto");
			}
			
			otp.setUsed(true);
			repository.save(otp);
			
			return ContextBFF.buildResponse(200, "OTP valido");
		}
		
		return ContextBFF.buildResponseError(404, "OTP non trovato");
	}
	
	public boolean checkValidOTP(String codiceOTP, String codiceFiscale, TipoRichiestaOTP tipoRichiesta) {
		
		OTP otp = repository.findByCodiceOTPAndCodiceFiscaleAndTipoRichiesta(codiceOTP, codiceFiscale, tipoRichiesta);
		
		if(otp != null) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			if(timestamp.after(otp.getExpirationDate()) || otp.isUsed()) {
				
				return false;
			}
			
			otp.setUsed(true);
			repository.save(otp);
			
			return true;
		}
		
		return false;
	}
	
	public OTPSmsAttemptDto smsAttemptsLeft(String codiceFiscale) {
		int attempt = DAILY_LIMIT_OTP_REQUEST - repository.countOTPSmsGenereted(codiceFiscale.toUpperCase());
		return new OTPSmsAttemptDto(attempt);
	}
	
	private OTP generateOTP(OTPRequestDto request) {
		int randomPin = (int) (Math.random()*90000)+10000;
		String codiceOTP = String.valueOf(randomPin);
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis() + OTP_EXP_TIME_MILLISEC);
		
		return new OTP(null, request.getCodiceFiscale().toUpperCase(), codiceOTP, false, timestamp, request.getTipoRichiesta());
	}
	
	private boolean isExceededSmsOTPRequest(String codiceFiscale) {
		return repository.countOTPSmsGenereted(codiceFiscale.toUpperCase()) >= DAILY_LIMIT_OTP_REQUEST ;
	}
	
	private boolean isValidData(OTPRequestDto request) {
		
		if(request.getCodiceFiscale().length() == 16 &&
				request.getContatto() != null) {
			if(request.getTipoRichiesta() == TipoRichiestaOTP.SMS ) {
				return true;
			} else if(request.getTipoRichiesta() == TipoRichiestaOTP.EMAIL) {
				//controllo tipo di contatto email
				return true;
			}
		}
		
		return false;
	}
	
	private void inoltroRichiestaOTPNotificatore(OTPRequestDto dto, HttpServletRequest req, String codiceOTP) throws IOException, NotificatoreException {
		String portale = getNomePortale(req);
		ConfigComune configComune = comuneRepository.findByTenant(portale);
		
		String url = apiBaseUrl + messageMb + uriNotifiche;
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		
		RequestBody requestBody = createRequestBodyNotificatore(dto, codiceOTP, configComune.getTemplateNotificatore(), configComune.getNome());
		
		Request request = new Request.Builder()
				.url(url)
				.method("POST", requestBody)
				.addHeader("x-authentication", getJwt(req))
				.build();
		
		okhttp3.Response response = client.newCall(request).execute();
		
		
		if(response.isSuccessful()) {
			log.info("---------------- OTP: {}", response.body().string());
		} else {
			log.error("---------------- OTP: {}", response.body().string());
			throw new NotificatoreException("Errore durante l'inoltro dell' otp");
		}
		
	}
	
    private String getNomePortale(HttpServletRequest req) {
        String portale =  req.getHeader("x-forwarded-host");
        
        if(portale == null || portale.isBlank()) { 
            portale =  "pavia-poc-cittafacile.csi.it";
        }

        return portale;
    }
    
    private RequestBody createRequestBodyNotificatore(OTPRequestDto dto, String codiceOTP, String templateNotificatore, String nomeComune) throws JsonProcessingException {
    	
		RequestBody requestBody = null;
		
		if(dto.getTipoRichiesta() == TipoRichiestaOTP.SMS) {
			String smsOTPJson = createSmsJson(dto, codiceOTP, nomeComune);
			requestBody = RequestBody.create(smsOTPJson, MediaType.parse("application/json"));	
		} else if (dto.getTipoRichiesta() == TipoRichiestaOTP.EMAIL){
			String emailOTPJson = createEmailJson(dto, codiceOTP, templateNotificatore);
			requestBody = RequestBody.create(emailOTPJson, MediaType.parse("application/json"));	
		}
		
		return requestBody;
    }
    
    private String createSmsJson(OTPRequestDto dto, String codiceOTP, String nomeComune) throws JsonProcessingException {
    	log.debug("-------------------PHONE NUMBER: {}", dto.getContatto());
    	
    	String phoneNumber = dto.getContatto().startsWith("0039") ? "" : "0039" + dto.getContatto();
    	
    	RichiestaSms sms = new RichiestaSms(phoneNumber, "Il tuo codice OTP per i servizi CittàFacile " + nomeComune + " è: " + codiceOTP);
    	PayloadSms payloadSms = new PayloadSms(UUID.randomUUID().toString(), dto.getCodiceFiscale(), sms, true);
    	RequestInoltroOTP inoltoSmsOTP = new RequestInoltroOTP(UUID.randomUUID().toString(), payloadSms);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	return mapper.writeValueAsString(inoltoSmsOTP);
    }
    
    private String createEmailJson(OTPRequestDto dto, String codiceOTP, String template) throws JsonProcessingException {
    	String bodyMail = "il codice di verifica è " + codiceOTP;
    	RichiestaEmail mail = new RichiestaEmail(dto.getContatto(), "conferma indirizzo mail", bodyMail, template);
    	PayloadEmail payloadEmail = new PayloadEmail(UUID.randomUUID().toString(), dto.getCodiceFiscale(), mail, true);
    	RequestInoltroOTP inoltroOTP = new RequestInoltroOTP(bodyMail, payloadEmail);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	
    	return mapper.writeValueAsString(inoltroOTP);
    }
	
	//metodi per test
	
	public List<OTP> getAll() {
		return repository.findAll();
	}
	
	private String getJwt(HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, "NOTIFICATIONS");
		
		return jwt.getJwt();
	}
	
}
