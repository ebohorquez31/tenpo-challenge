package cl.tenpo.challenge.consumer;

import cl.tenpo.challenge.model.dto.ExternalServiceResponseDTO;
import cl.tenpo.challenge.model.gateway.ExternalServiceGateway;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; 

@Service
public class ExternalServiceAdapter implements ExternalServiceGateway {

	private WebClient webClient;

	private static final Logger LOGGER = LogManager.getLogger(ExternalServiceAdapter.class);

	@Autowired
	public ExternalServiceAdapter(WebClient webClient) {    
		this.webClient = webClient;     
	} 

	@Override
	public Mono<ExternalServiceResponseDTO> consumeService(int numAttempts, String url) {   

		return webClient.get()
				.uri(url)
				.retrieve()
				.onStatus(HttpStatus::isError, response -> { 
					LOGGER.error("Error status code received: {}", response.statusCode());
					return Mono.error(new RuntimeException("Error response from external service"));
				})
				.bodyToMono(ExternalServiceResponseDTO.class)
				.doOnNext(response -> LOGGER.info("External service response: {}", response))
				.retryWhen(Retry.fixedDelay(numAttempts, Duration.ofSeconds(1))   
						.doBeforeRetry(retrySignal -> 
						LOGGER.info("Retrying external service - Attempt number: {}", retrySignal.totalRetries() + 1))
						)
				.onErrorResume(Exception.class, e -> {
					LOGGER.error("Exception during external service call: {}", e.getMessage());
					return Mono.empty(); 
				});   

	}   

}