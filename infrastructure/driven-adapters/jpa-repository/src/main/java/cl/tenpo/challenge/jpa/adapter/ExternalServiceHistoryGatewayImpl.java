package cl.tenpo.challenge.jpa.adapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.tenpo.challenge.jpa.entity.ExternalServiceHistory;
import cl.tenpo.challenge.jpa.repository.ExternalServiceHistoryRepository;
import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import cl.tenpo.challenge.model.gateway.ExternalServiceHistoryGateway;
import reactor.core.publisher.Mono;

@Service
public class ExternalServiceHistoryGatewayImpl implements ExternalServiceHistoryGateway{

	private ExternalServiceHistoryRepository externalServiceHistoryRepository;

	@Autowired
	public ExternalServiceHistoryGatewayImpl( ExternalServiceHistoryRepository externalServiceHistoryRepository ) {
		this.externalServiceHistoryRepository = externalServiceHistoryRepository;
	}

	@Override
	public Mono<ExternalServiceHistoryDTO> saveExternalServiceHistory( ExternalServiceHistoryDTO externalServiceHistoryDTO ) {

		var externalServiceHistory = ExternalServiceHistory.builder()
				.date( LocalDateTime.now() )  
				.endpoint( externalServiceHistoryDTO.getEndpoint() )
				.percentage( externalServiceHistoryDTO.getPercentage() )				
				.build();		

		return externalServiceHistoryRepository.save(externalServiceHistory)
		        .map(externalServiceHistorySaved -> { 
		            return ExternalServiceHistoryDTO.builder()
		                    .id(externalServiceHistorySaved.getId())
		                    .date(externalServiceHistorySaved.getDate())
		                    .endpoint(externalServiceHistorySaved.getEndpoint()) 
		                    .percentage(externalServiceHistorySaved.getPercentage())
		                    .build();
		        });
	}

	@Override
	public Mono<BigDecimal> getLastPercentageDifferentFromNull() {    

		return externalServiceHistoryRepository.findTopByPercentageNotNullOrderByIdDesc();
	      
	}

}
