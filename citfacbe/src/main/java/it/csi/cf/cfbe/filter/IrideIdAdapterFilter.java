/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Credenziali;
import it.csi.cf.cfbe.entity.User;
import it.csi.cf.cfbe.enumeration.Role;
import it.csi.cf.cfbe.model.identity.Utente;
import it.csi.cf.cfbe.repository.CredenzialiRepository;
import it.csi.cf.cfbe.services.UserService;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(1)
@Slf4j
public class IrideIdAdapterFilter implements Filter {
	public static final String AUTH_ID_CODICE_FISCALE = "shib-identita-codicefiscale";
	public static final String AUTH_ID_NOME = "shib-identita-nome";
	public static final String AUTH_ID_COGNOME = "shib-identita-cognome";
	public static final String AUTH_ID_EMAIL = "shib-email";
	public static final String AUTH_ID_PHONE = "shib-mobile-phone";
	public static final String UTENTE_REQ_ATTR = "utente";
	public static final String API = "/api/";
	public static final String API_PUBLIC = "/api-public/";
	public static final String API_TEST = "/api-public/test";
	public static final String API_ADMIN = "/api-admin/";
	public static final String API_ADMIN_USER_INFO = API_ADMIN + "user/me";
	public static final String USER_UNAUTHORIZED = "Utente non autorizzato";
	public static final String USER_MISSING = "Utente non presente";
	public static final String TOKEN_MISSING = "Token shibbolet non presente";
	public static final String MAINTENANCE_UNAUTHORIZED = "Non sei autorizzato server in manutenzione";

	@Autowired
	private UserService userService;

	@Autowired
	private CredenzialiRepository credenzialiRepository;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fchn)
			throws IOException, ServletException {

		if (!(req instanceof HttpServletRequest)) {
			fchn.doFilter(req, resp);
			return;
		}

		HttpServletRequest hreq = new MultiReadRequestWrapper((HttpServletRequest) req);
		HttpServletResponse hresp = (HttpServletResponse) resp;

		String requestUrl = hreq.getRequestURI();

		hresp.setHeader("Content-Type", MediaType.APPLICATION_JSON);

		ConfigComune comune = ContextBFF.getConfigComune(hreq);

		if (requestUrl.startsWith(API_PUBLIC) || requestUrl.startsWith(API)) {
			String[] arrayAllowed = { "/api-public/configComune/fromtenant", "/api-public/utenti/menu",
					"/api-public/utenti/me", "/api-public/maintenance", "/api-public/feedback-moon",
					"/api-public/feedback" };
			List<String> allowedPath = Arrays.asList(arrayAllowed);

			if (comune.isMaintenance() && !allowedPath.contains(requestUrl)) {

				if (!ContextBFF.isValidMaintenanceCredential(hreq, comune)) {
					errorResponse(hresp, MAINTENANCE_UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

			}
		}

		if (requestUrl.startsWith(API_PUBLIC)) {
			hresp.setHeader("Access-Control-Allow-Origin", "*");
			hresp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
			hresp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		}

		Utente identita = getIdentità(hreq, requestUrl);

		log.info("doFilter - utente: {}", identita);

		if (identita == null && requestUrl.startsWith(API)) {
			errorResponse(hresp, TOKEN_MISSING, HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		if (requestUrl.startsWith(API_ADMIN) && !requestUrl.matches(API_ADMIN_USER_INFO)) {

			String method = hreq.getMethod();

			if (identita == null || identita.getRuolo() == null) {
				errorResponse(hresp, USER_MISSING, HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			if (method.equals("GET") && identita.getRuolo() == Role.READ_ONLY
					&& isNotAuthorized(requestUrl, comune, identita)) {
				errorResponse(hresp, USER_UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			if (!method.equals("GET") && identita.getRuolo() == Role.READ_ONLY) {
				errorResponse(hresp, USER_UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			if (identita.getRuolo() == Role.OPERATORE && isNotAuthorized(requestUrl, method, comune, identita)) {
				errorResponse(hresp, USER_UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}

		hreq.setAttribute(UTENTE_REQ_ATTR, identita);

		fchn.doFilter(hreq, hresp);
	}

	private Utente getIdentità(HttpServletRequest req, String requestUrl) {
		String cf = req.getHeader(AUTH_ID_CODICE_FISCALE);

		if ((cf == null || cf.equals("")) && !requestUrl.startsWith(API_ADMIN))
			return null;

		Role ruolo = null;
		List<String> enti = null;
		String email = req.getHeader(AUTH_ID_EMAIL);
		String phoneNumber = req.getHeader(AUTH_ID_PHONE);

		if (requestUrl.startsWith(API_ADMIN)) {
			User user = userService.getUser(cf);

			if (user == null) {
				return null;
			}

			email = user.getEmail();
			ruolo = user == null ? null : user.getRuolo();
			enti = user.getEnti() == null ? new ArrayList<>() : Arrays.asList(user.getEnti());
			log.info("DEBUG: {}", user);
		}

		String nome = req.getHeader(AUTH_ID_NOME);
		String cognome = req.getHeader(AUTH_ID_COGNOME);

		if (email != null) {
			byte[] bytes = email.getBytes(StandardCharsets.UTF_8);

			email = new String(bytes, StandardCharsets.UTF_8);
		}

		return Utente.builder().email(email).phoneNumber(phoneNumber).codFisc(cf).nome(nome).cognome(cognome)
				.ruolo(ruolo).enti(enti).build();
	}

	private boolean isNotAuthorized(String requestUrl, String method, ConfigComune comune, Utente utente) {

		if (!utente.getEnti().contains(comune.getCodiceComune())) {
			return true;
		}

		if (method.equals("GET"))
			return !requestUrl.startsWith("/api-admin/tenant/0") || requestUrl.matches("/api-admin/tenants");

		// return !requestUrl.matches("/api-admin/tenant/0/(?!(.*manifest)).*");
		return !requestUrl.matches("/api-admin/tenant/0/.*");
	}

	private boolean isNotAuthorized(String requestUrl, ConfigComune comune, Utente utente) {

		return !utente.getEnti().contains(comune.getCodiceComune()) || !requestUrl.startsWith("/api-admin/tenant/0")
				|| requestUrl.matches("/api-admin/tenants");
	}

	private void errorResponse(HttpServletResponse hresp, String message, int status) throws IOException {
		hresp.setStatus(status);
		PrintWriter pw = hresp.getWriter();
		pw.append("{\"status\": " + status + "," + "\"error\": \"" + message + "\"}");
	}

	@Override
	public void destroy() {
	}

}
