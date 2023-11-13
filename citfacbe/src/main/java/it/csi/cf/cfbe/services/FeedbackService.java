/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.csi.cf.cfbe.entity.ConfigComune;
import it.csi.cf.cfbe.entity.Feedback;
import it.csi.cf.cfbe.entity.ServiceDetail;
import it.csi.cf.cfbe.enumeration.TypeFeedbackStats;
import it.csi.cf.cfbe.model.FeedbackMoonDto;
import it.csi.cf.cfbe.model.dto.FeedbackDto;
import it.csi.cf.cfbe.model.dto.FeedbackStatsDto;
import it.csi.cf.cfbe.model.dto.feedback.FeedbackStatsGeneralDto;
import it.csi.cf.cfbe.model.dto.feedback.FeedbackStatsOptionDto;
import it.csi.cf.cfbe.model.dto.feedback.FeedbackStatsPaginaDto;
import it.csi.cf.cfbe.model.dto.feedback.FeedbackStatsRatingDto;
import it.csi.cf.cfbe.repository.FeedbackRepository;
import it.csi.cf.cfbe.repository.ServiceDetailRepository;
import it.csi.cf.cfbe.services.utility.ContextBFF;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FeedbackService {
	
	@Autowired
	private FeedbackRepository feedbackRepository;
	
	@Autowired
	private ServiceDetailRepository detailRepository;
	
	
	
	public List<FeedbackDto> getComuneFeedback (String codiceComune) {	
		List<Feedback> feedbacks = feedbackRepository.findByCodiceComune(codiceComune);
		
		List<FeedbackDto> feedbacksDto = new ArrayList<>();
		
		for(Feedback feedback : feedbacks) {
			FeedbackDto dto = new FeedbackDto();
			
			BeanUtils.copyProperties(feedback, dto);
			
			feedbacksDto.add(dto);
		}
		
		return feedbacksDto;
	}
	
	public Response addFeedback (Feedback feedback, HttpServletRequest req) {
		
		if(feedback.getBind() != null && !feedback.getBind().equals("")) {
			return ContextBFF.buildResponse(200, "{\"status\":200, \"title\": \"success\"}");
		}
		
		ConfigComune comune = ContextBFF.getConfigComune(req);
		
		feedback.setCodiceComune(comune.getCodiceComune());
		
		if(feedback.getDetails().equals("")) {
			feedback.setDetails(null);
		}
		
		return ContextBFF.validateAndSave(feedback, feedbackRepository, false);
		
		
	}
	
	public Response addFeedbackMoon (FeedbackMoonDto dto, HttpServletRequest req) {
		if(dto.getCode() == null || dto.getCode().equals("")) {
			return ContextBFF.buildResponseError(400, "Code non presente");
		}
		
		ConfigComune comune = ContextBFF.getConfigComune(req);
		
		if(comune == null) {
			return ContextBFF.buildResponseError(404, "Comune non presente");
		}
		
		ServiceDetail serviceDetail = detailRepository.findByComuneAndModuloMoon(comune, dto.getCode());
		
		if(serviceDetail == null) {
			return ContextBFF.buildResponseError(422, "Service detail non presente");
		}
		Feedback feedback = new Feedback();
		
		BeanUtils.copyProperties(dto, feedback);
		
		String pagePath = "https://" + comune.getTenant() + "/area-personale/servizi/" + serviceDetail.getIdServizio();

		feedback.setPageTitle(dto.getSubject());
		feedback.setServiceID(serviceDetail.getIdServizio());
		feedback.setPagePath(pagePath);
		feedback.setCodiceComune(comune.getCodiceComune());
		
		if(feedback.getDetails().equals("")) {
			feedback.setDetails(null);
		}
		
		return ContextBFF.validateAndSave(feedback, feedbackRepository, false);
	}
	
	public Response getFeedbacksStats(HttpServletRequest req, Integer tenantId, Timestamp from, Timestamp to, TypeFeedbackStats type) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		
		if(type == TypeFeedbackStats.page) {
			return getFeedbackPageStats(comune, from, to);
		} else {
			return getFeedbackServiceStats(comune, from, to);
		}		
	}
	
	private Response getFeedbackPageStats (ConfigComune comune, Timestamp from, Timestamp to) {
		List<Object[]> statsPagine;
		
		if(from == null || to == null) {
			statsPagine = feedbackRepository.getFeedbackStatsPagine(comune.getCodiceComune());
		} else {
			statsPagine = feedbackRepository.getFeedbackStatsPagineFromDate(comune.getCodiceComune(), from, to);
		}
		
		List<FeedbackStatsPaginaDto> feedbackPagine = new ArrayList<>();
        for (Object[] stat : statsPagine) {
            String pagePath = (String) stat[0];
            long totalFeedback = (long) stat[1];
            double averageRating = (double) stat[2];
            
            Feedback feedback = feedbackRepository.findFirstByPagePathAndCodiceComuneOrderByCreatedAtDesc(pagePath, comune.getCodiceComune());
            
            String pageTitle = "";
            
            if(feedback != null)
            	pageTitle = feedback.getPageTitle();
            
            FeedbackStatsPaginaDto feedbackStatsDto = new FeedbackStatsPaginaDto(pageTitle ,pagePath, totalFeedback, averageRating);
            feedbackPagine.add(feedbackStatsDto);
        }
		
		return ContextBFF.buildResponse(200, feedbackPagine);
	}
	
	private Response getFeedbackServiceStats (ConfigComune comune, Timestamp from, Timestamp to) {
		List<Object[]> statsServizi;
				
		if(from == null || to == null) {
			statsServizi = feedbackRepository.getFeedbackStats(comune.getCodiceComune());
		} else {
			statsServizi = feedbackRepository.getFeedbackStatsFromDate(comune.getCodiceComune(), from, to);
		}
		
		List<FeedbackStatsDto> feedbackServizi = new ArrayList<>();
        for (Object[] stat : statsServizi) {
            int serviceId = (int) stat[0];
            long totalFeedback = (long) stat[1];
            double averageRating = (double) stat[2];
            
            Feedback feedback = feedbackRepository.findFirstByServiceIDAndCodiceComuneOrderByCreatedAtDesc(serviceId, comune.getCodiceComune());
            
            String pageTitle = "";
            
            if(feedback != null)
            	pageTitle = feedback.getPageTitle();
            
            FeedbackStatsDto feedbackStatsDto = new FeedbackStatsDto(pageTitle, serviceId, totalFeedback, averageRating);
            feedbackServizi.add(feedbackStatsDto);
        }
        
        return ContextBFF.buildResponse(200, feedbackServizi);
	}
	
	public Response getFeedbackServiceStatsDetail(HttpServletRequest req, Integer tenantId, Integer serviceId, Timestamp from, Timestamp to) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		List<Object[]> ratingStats;
		List<Object[]> optionStats;
		Object generalInfo;
				
		if(from == null || to == null) {
			ratingStats = feedbackRepository.getFeedbacksServiceStatsRatingDetail(serviceId, comune.getCodiceComune());
			optionStats = feedbackRepository.getFeedbacksServiceStatsOptionsDetail(serviceId, comune.getCodiceComune());
			generalInfo = feedbackRepository.getFeedbacksServiceStatsOptionsDetailGeneral(serviceId, comune.getCodiceComune());
		} else {
			ratingStats = feedbackRepository.getFeedbacksServiceStatsRatingDetailFromDate(serviceId, comune.getCodiceComune(), from, to);
			optionStats = feedbackRepository.getFeedbacksServiceStatsOptionsDetailFromDate(serviceId, comune.getCodiceComune(), from, to);
			generalInfo = feedbackRepository.getFeedbacksServiceStatsOptionsDetailGeneralFromDate(serviceId, comune.getCodiceComune(),  from, to);
		}
		
		List<FeedbackStatsRatingDto> feedbackStatsRatingList = new ArrayList<>();
        for (Object[] stat : ratingStats) {
            int rating = (int) stat[0];
            long total = (long) stat[1];
            
            FeedbackStatsRatingDto feedbackStatsDto = new FeedbackStatsRatingDto(rating, total);
            feedbackStatsRatingList.add(feedbackStatsDto);
        }
        
        List<FeedbackStatsOptionDto> feedbackStatsOptionList = new ArrayList<>();
        for (Object[] stat : optionStats) {
            String option = (String) stat[0];
            long total = (long) stat[1];
            
            FeedbackStatsOptionDto feedbackStatsDto = new FeedbackStatsOptionDto(option, total);
            feedbackStatsOptionList.add(feedbackStatsDto);
        }
        
        Long total = (long) ((Object[]) generalInfo)[0];
        
        Object avgString = ((Object[]) generalInfo)[1];
        double avg = avgString == null ? 0.0 : (double) avgString;  
        
        FeedbackStatsGeneralDto dto = new FeedbackStatsGeneralDto(total,  avg, feedbackStatsRatingList, feedbackStatsOptionList);
		
		return ContextBFF.buildResponse(200, dto);
		
	}
	
	public Response getFeedbackPageStatsDetail(HttpServletRequest req, Integer tenantId, String page, Timestamp from, Timestamp to) {
		ConfigComune comune = ContextBFF.getConfigComune(req, tenantId);
		List<Object[]> ratingStats;
		List<Object[]> optionStats;
		Object generalInfo;
				
		if(from == null || to == null) {
			ratingStats = feedbackRepository.getFeedbacksPageStatsRatingDetail(page, comune.getCodiceComune());
			optionStats = feedbackRepository.getFeedbacksPageStatsOptionsDetail(page, comune.getCodiceComune());
			generalInfo = feedbackRepository.getFeedbacksPageStatsOptionsDetailGeneral(page, comune.getCodiceComune());
		} else {
			ratingStats = feedbackRepository.getFeedbacksPageStatsRatingDetailFromDate(page, comune.getCodiceComune(), from, to);
			optionStats = feedbackRepository.getFeedbacksPageStatsOptionsDetailFromDate(page, comune.getCodiceComune(), from, to);
			generalInfo = feedbackRepository.getFeedbacksPageStatsOptionsDetailGeneralFromDate(page, comune.getCodiceComune(), from, to);
		}
		
		List<FeedbackStatsRatingDto> feedbackStatsRatingList = new ArrayList<>();
        for (Object[] stat : ratingStats) {
            int rating = (int) stat[0];
            long total = (long) stat[1];
            
            FeedbackStatsRatingDto feedbackStatsDto = new FeedbackStatsRatingDto(rating, total);
            feedbackStatsRatingList.add(feedbackStatsDto);
        }
        
        List<FeedbackStatsOptionDto> feedbackStatsOptionList = new ArrayList<>();
        for (Object[] stat : optionStats) {
            String option = (String) stat[0];
            long total = (long) stat[1];
            
            FeedbackStatsOptionDto feedbackStatsDto = new FeedbackStatsOptionDto(option, total);
            feedbackStatsOptionList.add(feedbackStatsDto);
        }
       
        Long total = (long) ((Object[]) generalInfo)[0];
        
        Object avgString = ((Object[]) generalInfo)[1];
        double avg = avgString == null ? 0.0 : (double) avgString;  
        
        FeedbackStatsGeneralDto dto = new FeedbackStatsGeneralDto(total, avg, feedbackStatsRatingList, feedbackStatsOptionList);
		
		return ContextBFF.buildResponse(200, dto);
		
	}
	
	public Response getServiceFeedbackWithDetails(HttpServletRequest req, Integer tenantId, Integer serviceId, Timestamp from, Timestamp to, Integer limit, Integer startPoint) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		String codiceComune = comune.getCodiceComune();
		
		List<Feedback> feedbacks;
		
		Pageable page = null; 
		
		if(limit != null && startPoint != null)
			page = PageRequest.of(startPoint, limit);
		
		if(from == null || to == null) {
			feedbacks = feedbackRepository.findServiceFeedbackWithDetails(serviceId, codiceComune, page);
		} else {
			feedbacks = feedbackRepository.findServiceFeedbackWithDetailsFromDate(from, to, serviceId, codiceComune, page);
		}
		
		return ContextBFF.buildResponse(200, feedbacks);
	}
	
	public Response getPageFeedbackWithDetails(HttpServletRequest req, Integer tenantId, String pagePath, Timestamp from, Timestamp to, Integer limit, Integer startPoint) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		String codiceComune = comune.getCodiceComune();
		
		List<Feedback> feedbacks;
		
		Pageable page = null; 
		
		if(limit != null && startPoint != null)
			page = PageRequest.of(startPoint, limit);
		
		if(from == null || to == null) {
			feedbacks = feedbackRepository.findPageFeedbackWithDetails(pagePath, codiceComune, page);
		} else {
			feedbacks = feedbackRepository.findPageFeedbackWithDetailsFromDate(from, to, pagePath, codiceComune, page);
		}

		return ContextBFF.buildResponse(200, feedbacks);
	}
	
	public Response getAllFeedbackWithDetails(HttpServletRequest req, Integer tenantId, Timestamp from, Timestamp to, Integer limit, Integer startPoint) {
		ConfigComune comune = ContextBFF.getConfigComune(req);
		String codiceComune = comune.getCodiceComune();
		
		List<Feedback> feedbacks;
		
		Pageable page = null; 
		
		if(limit != null && startPoint != null)
			page = PageRequest.of(startPoint, limit);
		
		if(from == null || to == null) {
			feedbacks = feedbackRepository.findFeedbackWithDetails(codiceComune, page);
		} else {
			feedbacks = feedbackRepository.findFeedbackWithDetailsFromDate(from, to, codiceComune, page);
		}

		return ContextBFF.buildResponse(200, feedbacks);
	}
}
