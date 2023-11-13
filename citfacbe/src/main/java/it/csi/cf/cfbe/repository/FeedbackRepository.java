/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import it.csi.cf.cfbe.entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
	List<Feedback> findByCodiceComune(String codiceComune);
	
	Feedback findByCodiceComuneAndServiceID(String codiceComune, Integer serviceID);
	
	Feedback findFirstByPagePathAndCodiceComuneOrderByCreatedAtDesc(String pagePath, String codiceComune);
	
	Feedback findFirstByServiceIDAndCodiceComuneOrderByCreatedAtDesc(Integer serviceID, String codiceComune);
	
	@Query("SELECT f.serviceID, COUNT(f) AS totalFeedback, AVG(f.rating) AS averageRating " +
            "FROM Feedback f " +
            "WHERE f.codiceComune = :codiceComune " +
            "AND f.serviceID != null " +
            "GROUP BY f.serviceID")
    List<Object[]> getFeedbackStats(@Param("codiceComune") String codiceComune);
    
    @Query("SELECT f.serviceID, COUNT(f) AS totalFeedback, AVG(f.rating) AS averageRating " +
            "FROM Feedback f " +
            "WHERE f.codiceComune = :codiceComune " +
            "AND f.createdAt BETWEEN :fromDate AND :toDate " +
            "AND f.serviceID != null " +
            "GROUP BY f.serviceID")
    List<Object[]> getFeedbackStatsFromDate(@Param("codiceComune") String codiceComune,
                                    @Param("fromDate") Timestamp fromDate,
                                    @Param("toDate") Timestamp toDate);
    
    @Query("SELECT f.pagePath, COUNT(f) AS totalFeedback, AVG(f.rating) AS averageRating " +
            "FROM Feedback f " +
            "WHERE f.codiceComune = :codiceComune " +
            "AND f.serviceID = null " + 
            "GROUP BY f.pagePath")
    List<Object[]> getFeedbackStatsPagine(@Param("codiceComune") String codiceComune);
    
    @Query("SELECT f.pagePath, COUNT(f) AS totalFeedback, AVG(f.rating) AS averageRating " +
            "FROM Feedback f " +
            "WHERE f.codiceComune = :codiceComune " +
            "AND f.createdAt BETWEEN :fromDate AND :toDate " +
            "AND f.serviceID = null " + 
            "GROUP BY f.pagePath")
    List<Object[]> getFeedbackStatsPagineFromDate(@Param("codiceComune") String codiceComune,
                                    @Param("fromDate") Timestamp fromDate,
                                    @Param("toDate") Timestamp toDate);
    
    @Query("SELECT f.rating, COUNT(f) AS totalFeedback " +
    	       "FROM Feedback f " +
    	       "WHERE f.serviceID = :serviceId " +
    	       "AND f.codiceComune = :codiceComune " +
    	       "GROUP BY f.rating")
    List<Object[]> getFeedbacksServiceStatsRatingDetail(@Param("serviceId") int serviceId, @Param("codiceComune") String codiceComune);
    
    @Query("SELECT f.rating, COUNT(f) AS totalFeedback " +
    	       "FROM Feedback f " +
    	       "WHERE f.serviceID = :serviceId " +
    	       "AND f.codiceComune = :codiceComune " +
    	       "AND f.createdAt >= :fromDate " +
    	       "AND f.createdAt <= :toDate " +
    	       "GROUP BY f.rating")
	List<Object[]> getFeedbacksServiceStatsRatingDetailFromDate(@Param("serviceId") int serviceId, 
	                                               @Param("codiceComune") String codiceComune, 
	                                               @Param("fromDate") Timestamp fromDate, 
	                                               @Param("toDate") Timestamp toDate);
	
	@Query("SELECT f.option, COUNT(f) AS totalFeedback " +
 	       "FROM Feedback f " +
 	       "WHERE f.serviceID = :serviceId " +
 	       "AND f.codiceComune = :codiceComune " +
 	       "GROUP BY f.option")
	List<Object[]> getFeedbacksServiceStatsOptionsDetail(@Param("serviceId") int serviceId, @Param("codiceComune") String codiceComune);
 
 	@Query("SELECT f.option, COUNT(f) AS totalFeedback " +
 	       "FROM Feedback f " +
 	       "WHERE f.serviceID = :serviceId " +
 	       "AND f.codiceComune = :codiceComune " +
 	       "AND f.createdAt >= :fromDate " +
 	       "AND f.createdAt <= :toDate " +
 	       "GROUP BY f.option")
	List<Object[]> getFeedbacksServiceStatsOptionsDetailFromDate(@Param("serviceId") int serviceId, 
	                                               @Param("codiceComune") String codiceComune, 
	                                               @Param("fromDate") Timestamp fromDate, 
	                                               @Param("toDate") Timestamp toDate);
	
	@Query("SELECT f.rating, COUNT(f) AS totalFeedback " +
 	       "FROM Feedback f " +
 	      "WHERE f.pagePath = :page " +
 	       "AND f.codiceComune = :codiceComune " +
 	       "GROUP BY f.rating")
	List<Object[]> getFeedbacksPageStatsRatingDetail(@Param("page") String page, @Param("codiceComune") String codiceComune);
 
 	@Query("SELECT f.rating, COUNT(f) AS totalFeedback " +
 	       "FROM Feedback f " +
 	      "WHERE f.pagePath = :page " +
 	       "AND f.codiceComune = :codiceComune " +
 	       "AND f.createdAt >= :fromDate " +
 	       "AND f.createdAt <= :toDate " +
 	       "GROUP BY f.rating")
	List<Object[]> getFeedbacksPageStatsRatingDetailFromDate(@Param("page") String page, 
	                                               @Param("codiceComune") String codiceComune, 
	                                               @Param("fromDate") Timestamp fromDate, 
	                                               @Param("toDate") Timestamp toDate);
	
	@Query("SELECT f.option, COUNT(f) AS totalFeedback " +
	       "FROM Feedback f " +
	       "WHERE f.pagePath = :page " +
	       "AND f.codiceComune = :codiceComune " +
	       "GROUP BY f.option")
	List<Object[]> getFeedbacksPageStatsOptionsDetail(@Param("page") String page, @Param("codiceComune") String codiceComune);

	@Query("SELECT f.option, COUNT(f) AS totalFeedback " +
	       "FROM Feedback f " +
	       "WHERE f.pagePath = :page " +
	       "AND f.codiceComune = :codiceComune " +
	       "AND f.createdAt >= :fromDate " +
	       "AND f.createdAt <= :toDate " +
	       "GROUP BY f.option")
	List<Object[]> getFeedbacksPageStatsOptionsDetailFromDate(@Param("page") String page, 
	                                               @Param("codiceComune") String codiceComune, 
	                                               @Param("fromDate") Timestamp fromDate, 
	                                               @Param("toDate") Timestamp toDate);
	
	@Query("SELECT COUNT(f) AS totalFeedback, AVG(f.rating) " +
		       "FROM Feedback f " +
		       "WHERE f.pagePath = :page " +
		       "AND f.codiceComune = :codiceComune ")
	Object getFeedbacksPageStatsOptionsDetailGeneral(@Param("page") String page, 
		                                               @Param("codiceComune") String codiceComune);
	
	@Query("SELECT COUNT(f) AS totalFeedback, AVG(f.rating) " +
		       "FROM Feedback f " +
		       "WHERE f.pagePath = :page " +
		       "AND f.codiceComune = :codiceComune " +
		       "AND f.createdAt >= :fromDate " +
		       "AND f.createdAt <= :toDate ")
	Object getFeedbacksPageStatsOptionsDetailGeneralFromDate(@Param("page") String page, 
		                                               @Param("codiceComune") String codiceComune, 
		                                               @Param("fromDate") Timestamp fromDate, 
		                                               @Param("toDate") Timestamp toDate);
	
	@Query("SELECT COUNT(f) AS totalFeedback, AVG(f.rating) " +
		       "FROM Feedback f " +
		       "WHERE f.serviceID = :serviceId " +
		       "AND f.codiceComune = :codiceComune ")
	Object getFeedbacksServiceStatsOptionsDetailGeneral(@Param("serviceId") Integer serviceId, 
		                                               @Param("codiceComune") String codiceComune);
	
	@Query("SELECT COUNT(f) AS totalFeedback, AVG(f.rating) " +
		       "FROM Feedback f " +
		       "WHERE f.serviceID = :serviceId " +
		       "AND f.codiceComune = :codiceComune " +
		       "AND f.createdAt >= :fromDate " +
		       "AND f.createdAt <= :toDate ")
	Object getFeedbacksServiceStatsOptionsDetailGeneralFromDate(@Param("serviceId") Integer serviceId, 
		                                               @Param("codiceComune") String codiceComune, 
		                                               @Param("fromDate") Timestamp fromDate, 
		                                               @Param("toDate") Timestamp toDate);
	
	//QUERY PER FEEDBACK CON COMMENTO 	
	@Query("SELECT f FROM Feedback f " +
		       "WHERE f.createdAt BETWEEN :fromDate AND :toDate " +
		       "AND f.serviceID = :serviceID " +
		       "AND f.details IS NOT NULL " +
		       "AND f.codiceComune = :codiceComune " +
		       "ORDER BY f.createdAt DESC")
	List<Feedback> findServiceFeedbackWithDetailsFromDate(@Param("fromDate") Timestamp fromDate, 
															@Param("toDate") Timestamp toDate, 
															@Param("serviceID") Integer serviceID,
															@Param("codiceComune") String codiceComune,
															@Nullable Pageable pageable);
	
	@Query("SELECT f FROM Feedback f " +
		       "WHERE f.serviceID = :serviceID " +
		       "AND f.details IS NOT NULL " +
		       "AND f.codiceComune = :codiceComune " +
		       "ORDER BY f.createdAt DESC")
	List<Feedback> findServiceFeedbackWithDetails(@Param("serviceID") Integer serviceID, 
															@Param("codiceComune") String codiceComune,
															@Nullable Pageable pageable);
	
	@Query("SELECT f FROM Feedback f " +
		       "WHERE f.createdAt BETWEEN :fromDate AND :toDate " +
		       "AND f.pagePath = :pagePath " +
		       "AND f.details IS NOT NULL " +
		       "AND f.codiceComune = :codiceComune " +
		       "ORDER BY f.createdAt DESC")
	List<Feedback> findPageFeedbackWithDetailsFromDate(@Param("fromDate") Timestamp fromDate, 
															@Param("toDate") Timestamp toDate, 
															@Param("pagePath") String pagePath,
															@Param("codiceComune") String codiceComune,
															@Nullable Pageable pageable);
	
	@Query("SELECT f FROM Feedback f " +
		       "WHERE f.pagePath = :pagePath " +
		       "AND f.details IS NOT NULL " +
		       "AND f.codiceComune = :codiceComune " +
		       "ORDER BY f.createdAt DESC")
	List<Feedback> findPageFeedbackWithDetails(@Param("pagePath") String pagePath,
															@Param("codiceComune") String codiceComune,
															@Nullable Pageable pageable);
	
	@Query("SELECT f FROM Feedback f " +
		       "WHERE f.createdAt BETWEEN :fromDate AND :toDate " +
		       "AND f.details IS NOT NULL " +
		       "AND f.codiceComune = :codiceComune " +
		       "ORDER BY f.createdAt DESC")
	List<Feedback> findFeedbackWithDetailsFromDate(@Param("fromDate") Timestamp fromDate, 
															@Param("toDate") Timestamp toDate,
															@Param("codiceComune") String codiceComune,
															@Nullable Pageable pageable);
	
	@Query("SELECT f FROM Feedback f " +
		       "WHERE f.details IS NOT NULL " +
		       "AND f.codiceComune = :codiceComune " +
		       "ORDER BY f.createdAt DESC")
	List<Feedback> findFeedbackWithDetails(@Param("codiceComune") String codiceComune,
															@Nullable Pageable pageable);
	
}
