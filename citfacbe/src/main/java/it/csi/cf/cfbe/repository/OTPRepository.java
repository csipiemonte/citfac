/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import it.csi.cf.cfbe.entity.OTP;
import it.csi.cf.cfbe.enumeration.TipoRichiestaOTP;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long>{
	
	OTP findByCodiceOTPAndCodiceFiscaleAndTipoRichiesta(String codiceOTP, String CodiceFiscale, TipoRichiestaOTP tipoRichiesta);
	
	//tipo_richiesta = 0 dove 0 indica SMS
	@Query("SELECT COUNT(ID) "
			+ "FROM otps "
			+ "WHERE codice_fiscale = ?1 AND "
			+ "tipo_richiesta = 0")
	Integer countOTPSmsGenereted(String codiceFiscale);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM otps "
			//+ "WHERE expiration_date < NOW() AND "
			+ "WHERE EXTRACT(DAY FROM (NOW() - expiration_date)) >= 1")
	int deleteExpiredOTP();

}
