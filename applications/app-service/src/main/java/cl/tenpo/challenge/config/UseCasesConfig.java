package cl.tenpo.challenge.config;

import org.springframework.beans.factory.annotation.Value;  
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;   
import org.springframework.scheduling.annotation.EnableAsync;   
import org.springframework.web.reactive.function.client.WebClient;

import cl.tenpo.challenge.api.filter.RequestFilter;
import cl.tenpo.challenge.model.gateway.ExternalServiceGateway;
import cl.tenpo.challenge.model.gateway.ExternalServiceHistoryGateway;
import cl.tenpo.challenge.model.gateway.MainServiceHistoryGateway;
import cl.tenpo.challenge.model.properties.ApiRateLimitProperty;
import cl.tenpo.challenge.model.properties.ExternalServiceProperty;
import cl.tenpo.challenge.usecase.SumOperationUseCase;

@Configuration
@EnableAsync
public class UseCasesConfig {   
    
    
	@Bean
	public ExternalServiceProperty externalServiceProperty(
			@Value("${external-service.endpoint}") String endpoint,
			@Value("${external-service.num-attemps}") String numAttemps ) {

		return new ExternalServiceProperty( endpoint, numAttemps );
	}

	@Bean
	public ApiRateLimitProperty apiRateLimitProperty(
			@Value("${api-rate-limit.num-requests}") String numRequests,
			@Value("${api-rate-limit.num-minutes}") String numMinutes ) {

		return new ApiRateLimitProperty( numRequests, numMinutes );
	}

	@Bean
	public RequestFilter requestFilter( ApiRateLimitProperty apiRateLimitProperty ) {
		return new RequestFilter( apiRateLimitProperty );
	}

	@Bean
	public SumOperationUseCase sumOperationUseCase( 
			ExternalServiceHistoryGateway externalServiceHistoryGateway,
			MainServiceHistoryGateway mainServiceHistoryGateway,	
			ExternalServiceGateway externalServiceGateway,
			ExternalServiceProperty externalServiceProperty ){

		return new SumOperationUseCase( externalServiceHistoryGateway, 
				mainServiceHistoryGateway, externalServiceGateway, 
				externalServiceProperty );  
	}

	@Bean
	public WebClient webClient(WebClient.Builder builder) {       
		return builder
				.defaultHeader("Accept", "application/json")
				.defaultHeader("Content-Type", "application/json")
				.build(); 
	}


}