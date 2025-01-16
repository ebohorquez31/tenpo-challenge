package cl.tenpo.challenge.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class MainServiceHistoryDTO {

	private Long id;
	private LocalDateTime date; 
	private String endpoint;	    
	private BigDecimal firstNumber;	    	    
	private BigDecimal secondNumber;
	private BigDecimal result;
	private ExternalServiceHistoryDTO externalServiceHistory;
	
}
