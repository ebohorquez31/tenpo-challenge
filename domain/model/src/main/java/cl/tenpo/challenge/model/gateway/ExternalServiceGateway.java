package cl.tenpo.challenge.model.gateway;

import cl.tenpo.challenge.model.dto.ExternalServiceResponseDTO;
import reactor.core.publisher.Mono;

public interface ExternalServiceGateway {

	Mono<ExternalServiceResponseDTO> consumeService( int numAttempts, String url );
	
}      
