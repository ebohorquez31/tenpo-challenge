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
@Table("main_service_history")
public class MainServiceHistory implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
    @Column("id") 
    private Long id;

	private LocalDateTime date;  
	
    private String endpoint;
    
    @Column("first_number")
    private BigDecimal firstNumber;
    
    @Column("second_number")  
    private BigDecimal secondNumber;
    
    private BigDecimal result;
    
    @Column("external_service_id")
    private Long externalServiceId; 
	
}
