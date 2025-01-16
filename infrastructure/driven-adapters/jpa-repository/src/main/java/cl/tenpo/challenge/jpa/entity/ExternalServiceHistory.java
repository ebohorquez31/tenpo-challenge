package cl.tenpo.challenge.jpa.entity;
  
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor; 
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Table("external_service_history")
public class ExternalServiceHistory implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
    @Column("id")
    private Long id; 

	private LocalDateTime date; 
	
    private String endpoint;
    
    private BigDecimal percentage; 
	
}
