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
public class ExternalServiceHistoryDTO {

	private Long id;	
	private LocalDateTime date; 
	private String endpoint;
	private BigDecimal percentage;

}
