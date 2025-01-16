package cl.tenpo.challenge.model.gateway;

import java.math.BigDecimal;

import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import reactor.core.publisher.Mono;

public interface ExternalServiceHistoryGateway {

	public Mono<ExternalServiceHistoryDTO> saveExternalServiceHistory(           
			ExternalServiceHistoryDTO externalServiceHistoryDTO );
	
	public Mono<BigDecimal> getLastPercentageDifferentFromNull();  
	
}
