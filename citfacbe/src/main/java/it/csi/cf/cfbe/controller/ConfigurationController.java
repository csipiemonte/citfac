/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.CategoriaServizio;
import it.csi.cf.cfbe.entity.Cms;
import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.FirebaseCredentials;
import it.csi.cf.cfbe.entity.JwtCredentials;
import it.csi.cf.cfbe.entity.ServiceDetail;
import it.csi.cf.cfbe.entity.Servizio;
import it.csi.cf.cfbe.enumeration.TypeFeedbackStats;
import it.csi.cf.cfbe.exception.TokenException;
import it.csi.cf.cfbe.filter.IrideIdAdapterFilter;
import it.csi.cf.cfbe.model.dto.CredenzialiDto;
import it.csi.cf.cfbe.model.dto.MaintenanceDto;
import it.csi.cf.cfbe.model.dto.comune.ConfigComuneFullDto;
import it.csi.cf.cfbe.model.dto.utenti.UtenteAdmin;
import it.csi.cf.cfbe.model.identity.Utente;
import it.csi.cf.cfbe.repository.CategoriaServizioRepository;
import it.csi.cf.cfbe.repository.JwtRepository;
import it.csi.cf.cfbe.services.CmsService;
import it.csi.cf.cfbe.services.ConfigComuniService;
import it.csi.cf.cfbe.services.ConfigurationService;
import it.csi.cf.cfbe.services.FeedbackService;
import it.csi.cf.cfbe.services.MenuService;
import it.csi.cf.cfbe.services.NotificatoreService;
import it.csi.cf.cfbe.services.ServiceDetailService;
import it.csi.cf.cfbe.services.ServiziService;
import it.csi.cf.cfbe.services.proxy.MoonProxyService;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;

@Service
@Path("api-admin/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class ConfigurationController {
	
	@Autowired
	private ConfigComuniService configComuniService;
	
	@Autowired
	private ConfigurationService configurationService; 
	
	@Autowired
	private ServiziService serviziService;
	
	@Autowired
	private ServiceDetailService serviceDetailService;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private NotificatoreService notificatoreService;
	
	@Autowired
	private MoonProxyService moonService;
	
	@Autowired
	private FeedbackService feedbackService;
	
	@Autowired
	private CmsService cmsService;
	
	@Autowired
	private JwtRepository jwtRepository;
	
	@Autowired 
	private CategoriaServizioRepository categoriaServizioRepository;
	
	//GESTIONE CONFIG COMUNI
	
	@GET
	@Path("/user/me")
	public Response getUser(@Context HttpServletRequest req) {
		Utente utente = (Utente) req.getAttribute(IrideIdAdapterFilter.UTENTE_REQ_ATTR);
    	
    	if(utente == null) {
    		return ContextBFF.buildResponseError(401, "Accesso non effettuato");
    	}
    	
    	UtenteAdmin dto = new UtenteAdmin();
    	
    	BeanUtils.copyProperties(utente, dto);
    	
        return ContextBFF.buildResponse(200, dto);
	}
	
	@GET
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request, @Context UriInfo info ) {
    	String tenant = ContextBFF.getTenant(request);
    	ConfigComune configComune = ContextBFF.getConfigComune(request);
    	URI uri = URI.create("https://" + tenant + "/" + configComune.getLogoutUriBo());
    	return Response.status(302).location(uri).build();
    }
	
	@GET
	@Path("/tenants")
	public Response GetAllConfigComuni(@Context HttpServletRequest req) {
		List<ConfigComuneFullDto> comuni = configComuniService.getAllConfig(req);
		
		return ContextBFF.buildResponse(200, comuni);
	}
	
	@GET
	@Path("/tenant/{id}")
	public Response getConfigComune(@PathParam("id") Integer id, @Context HttpServletRequest req) {
		ConfigComuneFullDto comune = configComuniService.getConfigFull(req, id);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Ente non presente");
		}
		
		return ContextBFF.buildResponse(200, comune);
	}
	
	@POST
	@Path("/tenant")
	public Response addNewConfig(ConfigComune comune) {

		return configComuniService.addNewConfig(comune);
	}
	
	@PUT
	@Path("/tenant/{id}")
	public Response updateConfigFull(@PathParam("id") Integer id, ConfigComune comune, @Context HttpServletRequest req) {
		return configComuniService.updateConfigFull(comune, id, req);
	}
	
	@PATCH
	@Path("/tenant/{id}")
	public Response updateConfig(@PathParam("id") Integer id, ConfigComune comune, @Context HttpServletRequest req) {
		return configComuniService.updateConfig(comune, id, req);
	}
	
	@POST
	@Path("/tenant/{id}/maintenance")
	public Response setMaintenance(@PathParam("id") Integer tenantId, MaintenanceDto dto, @Context HttpServletRequest req) {
		return configComuniService.updateMaintenance(req, tenantId, dto);
	}
	
	//GESTIONE CREDENZIALI
	
	@GET
	@Path("/tenant/{id}/credentials")
	public Response getCredentials(@PathParam("id") Integer id, @Context HttpServletRequest req) {
		return configurationService.getCredenziali(req, id);
	}
	
	@GET
	@Path("/tenant/{id}/credential/{asset}")
	public Response getCredential(@PathParam("id") Integer id, @PathParam("asset") Integer assetId, @Context HttpServletRequest req) {
		return configurationService.getCredential(req, id, assetId);
	}
	
	@POST
	@Path("/tenant/{id}/credential/{asset}")
	public Response addCredential(@PathParam("id") Integer id, @PathParam("asset") Integer assetId, @Context HttpServletRequest req, CredenzialiDto dto) {
		return configurationService.addCredentials(req, dto, id, assetId);
	}
	
	@PUT
	@Path("/tenant/{id}/credential/{asset}")
	public Response updateCredential(@PathParam("id") Integer id, @PathParam("asset") Integer assetId, @Context HttpServletRequest req, CredenzialiDto dto) {
		return configurationService.updateCredentials(req, dto, id, assetId);
	}
	
	@DELETE
	@Path("/tenant/{id}/credential/{asset}")
	public Response deleteCredential(@PathParam("id") Integer id, @PathParam("asset") Integer asssetId, @Context HttpServletRequest req) {
		return configurationService.deleteCredentials(req, id, asssetId);
	}
	
	//GESTIONE SERVIZI
	
	@GET
	@Path("/tenant/{tenantId}/services")
	public Response getServices(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return serviziService.getServices(req, tenantId);
	}
	
	@DELETE
	@Path("/tenant/{tenantId}/services")
	public Response deleteAllServices(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return serviziService.deleteAll(req, tenantId);
	}
	
	@GET
	@Path("/tenant/{tenantId}/service/{id}")
	public Response getService(@Context HttpServletRequest req, @PathParam("id") Integer id, @PathParam("tenantId") Integer tenantId) {
		return serviziService.getService(id, tenantId, req);
	}
	
	@POST
	@Path("/tenant/{tenantId}/service")
	public Response addService(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, Servizio servizio) {
		return serviziService.addService(req, tenantId, servizio);
	}
	
	@PUT
	@Path("/tenant/{tenantId}/service/{serviceId}")
	public Response updateService(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, @PathParam("serviceId") Integer serviceId, Servizio servizio) {
		return serviziService.updateService(req, tenantId, serviceId, servizio);
	}
	
	@DELETE
	@Path("/tenant/{tenantId}/service/{serviceId}")
	public Response deleteService(@PathParam("serviceId") Integer serviceId, @Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return serviziService.deleteService(serviceId, tenantId, req);
	}
	
	//GESTIONE DETTAGLIO SERVIZI
	
	@GET
	@Path("/tenant/{tenantId}/details")
	public Response getServicesDetails(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return serviceDetailService.getServicesDetails(req, tenantId);
	}
	
	@GET
	@Path("/tenant/{tenantId}/detail/{detailId}")
	public Response getServiceDetail(@Context HttpServletRequest req, @PathParam("detailId") Integer id, @PathParam("tenantId") Integer tenantId) {
		return serviceDetailService.getServiceDetail(id, tenantId, req);
	}
	
	@POST
	@Path("/tenant/{tenantId}/detail")
	public Response addServiceDetail(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, ServiceDetail detail) {
		return serviceDetailService.addServiceDetail(req, tenantId, detail);
	}
	
	@PUT
	@Path("/tenant/{tenantId}/detail/{detailId}")
	public Response updateServiceDetail(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, @PathParam("detailId") Integer serviceId, ServiceDetail detail) {
		return serviceDetailService.updateServiceDetail(req, tenantId, serviceId, detail);
	}
	
	@DELETE
	@Path("/tenant/{tenantId}/detail/{detailId}")
	public Response deleteServiceDetail(@PathParam("detailId") Integer serviceId, @Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return serviceDetailService.deleteServiceDetail(serviceId, tenantId, req);
	}
	
	//GESTIONE CREDENZIALI FIREBASE
	@GET
	@Path("/tenant/{tenantId}/firebase")
	public Response getFirebaseCredentials(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return configurationService.getFirebaseCredentials(req, tenantId);
	}
	
	@POST
	@Path("/tenant/{tenantId}/firebase")
	public Response addFirebaseCredentials(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, FirebaseCredentials credentials) {
		return configurationService.addFirebaseCredentials(req, tenantId, credentials);
	}
	
	@PUT
	@Path("/tenant/{tenantId}/firebase")
	public Response updateFirebaseCredentials(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, FirebaseCredentials credentials) {
		return configurationService.updateFirebaseCredentials(req, tenantId, credentials);
	}
	
	@DELETE
	@Path("/tenant/{tenantId}/firebase")
	public Response deleteFirebaseCredentials(@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return configurationService.deleteFirebaseCredentials(req, tenantId);
	}
	
	//GESTIOEN MENU
	
	@GET
	@Path("/tenant/{tenantId}/menu")
	public Response getMenu (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
		return menuService.getMenu(req, tenantId);
	}
	
	@POST
	@Path("/tenant/{tenantId}/menu")
	public Response addMenu (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
		return menuService.addMenu(req, tenantId);
	}
	
	@PUT
	@Path("/tenant/{tenantId}/menu")
	public Response updateMenu (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
		return menuService.updateMenu(req, tenantId);
	}
	
	//GESTIOEN MANIFEST
	
//	@GET
//	@Path("/tenant/{tenantId}/manifest")
//	public Response getManifest (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
//		return manifestService.getManifest(req, tenantId);
//	}
//	
//	@POST
//	@Path("/tenant/{tenantId}/manifest")
//	public Response addManifest (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
//		return manifestService.addManifest(req, tenantId);
//	}
//	
//	@PUT
//	@Path("/tenant/{tenantId}/manifest")
//	public Response updateManifest (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
//		return manifestService.updateManifest(req, tenantId);
//	}
//	
//	//GESTIOEN MANIFEST
//	
//	@GET
//	@Produces("text/javascript")
//	@Path("/tenant/{tenantId}/serviceworker")
//	public Response getServiceWorker (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
//		return serviceWorkerService.getServiceWorker(req, tenantId);
//	}
//	
//	@POST
//	@Consumes("text/javascript")
//	@Path("/tenant/{tenantId}/serviceworker")
//	public Response addServiceWorker (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
//		return serviceWorkerService.addServiceWorker(req, tenantId);
//	}
//	
//	@PUT
//	@Consumes("text/javascript")
//	@Path("/tenant/{tenantId}/serviceworker")
//	public Response updateServiceWorker (@PathParam("tenantId") Integer tenantId, @Context HttpServletRequest req) {
//		return serviceWorkerService.updateServiceWorker(req, tenantId);
//	}
//	
	//informazioni notificatore e moon
	
	@GET
	@Path("/notification/services")
	public Response notificatoreServicce(@Context HttpServletRequest req) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		JwtCredentials jwt = jwtRepository.findByComuneAndApi(comune, "NOTIFICATIONS");
		String baseUrl = notificatoreService.getUriPreferenze() + "services?filter={ \"tags\": { \"match\": \""+ comune.getCodiceComune() + " citcaf\" } }";
		try {
			return notificatoreService.doGet(req, baseUrl, jwt.getJwt());
		} catch (IOException e) {
			return ContextBFF.buildResponseError(500, "Internal Server Error");
		}
	}
	
	@GET
	@Path("/moon/moduli")
	public Response moduliMoon(@Context HttpServletRequest req) {
		try {
			return moonService.getModuliWrapped(req);
		} catch (TokenException e) {
			return ContextBFF.buildResponse(500, "Impossibile ottenere token API Manager");
		}
	}
	
	//FEEDBACK
	
	@GET
	@Path("/tenant/{tenantId}/feedbacks/stats")
	public Response getFeedbacksStats(@PathParam("tenantId") Integer tenantId,
	                                  @QueryParam("from") @DefaultValue("") String fromDateStr,
	                                  @QueryParam("to") @DefaultValue("") String toDateStr,
	                                  @QueryParam("type") TypeFeedbackStats type,
	                                  @Context HttpServletRequest req) {
		
		if(type == null) {
			return ContextBFF.buildResponseError(400, "Type assente");
		}
	    
		if(!fromDateStr.isEmpty() && toDateStr.isEmpty()) {
			return ContextBFF.buildResponseError(400, "To date assente");
		}
		
		if(fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
			return ContextBFF.buildResponseError(400, "From date assente");
		}

		Timestamp sqlFromDate = null;
		Timestamp sqlToDate = null;
		
		if(!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
			LocalDate fromDate = null;
		    LocalDate toDate = null;
		    
	        fromDate = LocalDate.parse(fromDateStr);
	    
	        toDate = LocalDate.parse(toDateStr);
	        
	        LocalTime maxTime = LocalTime.of(23, 59, 59, 999_000_000);;
	        LocalTime minTime = LocalTime.of(0, 0);
	
		    sqlFromDate = (fromDate != null) ? Timestamp.valueOf(fromDate.atTime(minTime)) : null;
		    sqlToDate = (toDate != null) ? Timestamp.valueOf(toDate.atTime(maxTime)) : null;
		}

	    return feedbackService.getFeedbacksStats(req, tenantId, sqlFromDate, sqlToDate, type);
	}
	
	@GET
	@Path("/tenant/{tenantId}/feedback")
	public Response getFeedbacksStatsDetails(@PathParam("tenantId") Integer tenantId,
									  @QueryParam("serviceId") Integer serviceId,
									  @QueryParam("page") String page,
	                                  @QueryParam("from") @DefaultValue("") String fromDateStr,
	                                  @QueryParam("to") @DefaultValue("") String toDateStr,
	                                  @Context HttpServletRequest req) {
	    
		if(!fromDateStr.isEmpty() && toDateStr.isEmpty()) {
			return ContextBFF.buildResponseError(400, "To date assente");
		}
		
		if(fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
			return ContextBFF.buildResponseError(400, "From date assente");
		}

		Timestamp sqlFromDate = null;
		Timestamp sqlToDate = null;
		
		if(!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
			LocalDate fromDate = null;
		    LocalDate toDate = null;
		    
	        fromDate = LocalDate.parse(fromDateStr);
	    
	        toDate = LocalDate.parse(toDateStr);
	        
	        LocalTime maxTime = LocalTime.of(23, 59, 59, 999_000_000);;
	        LocalTime minTime = LocalTime.of(0, 0);
	
		    sqlFromDate = (fromDate != null) ? Timestamp.valueOf(fromDate.atTime(minTime)) : null;
		    sqlToDate = (toDate != null) ? Timestamp.valueOf(toDate.atTime(maxTime)) : null;
		}
		
		if(serviceId != null) {
			return feedbackService.getFeedbackServiceStatsDetail(req, tenantId, serviceId, sqlFromDate, sqlToDate);
		} else if (page != null) {
			return feedbackService.getFeedbackPageStatsDetail(req, tenantId, page, sqlFromDate, sqlToDate);
		} else {
			return ContextBFF.buildResponseError(400, "Page or ServiceId query param missing");
		}
	}
	
	@GET
	@Path("/tenant/{tenantId}/feedback/messages")
	public Response getFeedbacksMessages(@PathParam("tenantId") Integer tenantId,
										  @QueryParam("serviceId") Integer serviceId,
										  @QueryParam("page") String page,
										  @QueryParam("from") @DefaultValue("") String fromDateStr,
										  @QueryParam("to") @DefaultValue("") String toDateStr,
										  @QueryParam("limit") Integer limit,
										  @QueryParam("startPage") Integer startPoint,
										  @Context HttpServletRequest req) {
		
		if(!fromDateStr.isEmpty() && toDateStr.isEmpty()) {
			return ContextBFF.buildResponseError(400, "To date assente");
		}
		
		if(fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
			return ContextBFF.buildResponseError(400, "From date assente");
		}

		Timestamp sqlFromDate = null;
		Timestamp sqlToDate = null;
		
		if(!fromDateStr.isEmpty() && !toDateStr.isEmpty()) {
			LocalDate fromDate = null;
		    LocalDate toDate = null;
		    
	        fromDate = LocalDate.parse(fromDateStr);
	    
	        toDate = LocalDate.parse(toDateStr);
	        
	        LocalTime maxTime = LocalTime.of(23, 59, 59, 999_000_000);;
	        LocalTime minTime = LocalTime.of(0, 0);
	
		    sqlFromDate = (fromDate != null) ? Timestamp.valueOf(fromDate.atTime(minTime)) : null;
		    sqlToDate = (toDate != null) ? Timestamp.valueOf(toDate.atTime(maxTime)) : null;
		}
		
		if(serviceId != null) {
			return feedbackService.getServiceFeedbackWithDetails(req, tenantId, serviceId, sqlFromDate, sqlToDate, limit, startPoint);
		} else if (page != null) {
			return feedbackService.getPageFeedbackWithDetails(req, tenantId, page, sqlFromDate, sqlToDate, limit, startPoint);
		} else {
			return feedbackService.getAllFeedbackWithDetails(req, tenantId, sqlFromDate, sqlToDate, limit, startPoint);
		}
		
	}
	
	//GESTION CMS
	@GET
	@Path("/tenant/{tenantId}/cms")
	public Response getCms (@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return cmsService.getCms(req, tenantId);
	}
	
	@POST
	@Path("/tenant/{tenantId}/cms")
	public Response addCms (@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, Cms cms) {
		return cmsService.addCms(req, tenantId, cms);
	}
	
	@PUT
	@Path("/tenant/{tenantId}/cms")
	public Response updateCms (@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId, Cms cms) {
		return cmsService.updateCms(req, tenantId, cms);
	}
	
	@DELETE
	@Path("/tenant/{tenantId}/cms")
	public Response deleteCms (@Context HttpServletRequest req, @PathParam("tenantId") Integer tenantId) {
		return cmsService.deleteCms(req, tenantId);
	}
	
	//INFORMAZIONI BASE
	
	@GET
	@Path("/assets")
	public Response getAllAsset() {
		return configurationService.getAllAsset();
	}
	
	@GET
	@Path("/regions")
	public Response getAllRegions() {
		return configurationService.getAllRegioni();
	}
	
	@GET
	@Path("/services/categories")
	public Response getAllServiceCategories () {
		List<CategoriaServizio> categorie = categoriaServizioRepository.findAll();
		return ContextBFF.buildResponse(200, categorie);
	}

}
