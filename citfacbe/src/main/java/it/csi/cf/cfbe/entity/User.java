/*******************************************************************************
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 *******************************************************************************/
package it.csi.cf.cfbe.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.validator.constraints.UniqueElements;

import com.vladmihalcea.hibernate.type.array.StringArrayType;

import it.csi.cf.cfbe.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "users")
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({
    @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String codiceComune;	
	
	@UniqueElements
	private String codiceFiscale;
	
	private String nome;
	
	private String cognome;
	
	private String email;
	
	private Role ruolo;
	
	@Type(type = "string-array")
	@Column(columnDefinition = "varchar[]")
    private String[] enti;
}
