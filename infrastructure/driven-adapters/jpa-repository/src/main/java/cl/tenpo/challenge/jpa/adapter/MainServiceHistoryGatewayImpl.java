package cl.tenpo.challenge.jpa.adapter;
  
import java.time.LocalDateTime;
   
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;  
import org.springframework.stereotype.Service;

import cl.tenpo.challenge.jpa.entity.ExternalServiceHistory;
import cl.tenpo.challenge.jpa.entity.MainServiceHistory;
import cl.tenpo.challenge.jpa.repository.ExternalServiceHistoryRepository;
import cl.tenpo.challenge.jpa.repository.MainServiceHistoryRepository;
import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import cl.tenpo.challenge.model.gateway.MainServiceHistoryGateway;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class MainServiceHistoryGatewayImpl implements MainServiceHistoryGateway { 

	private MainServiceHistoryRepository mainServiceHistoryRepository;   
	private ExternalServiceHistoryRepository externalServiceHistoryRepository; 

	@Autowired
	public MainServiceHistoryGatewayImpl( MainServiceHistoryRepository mainServiceHistoryRepository,
			ExternalServiceHistoryRepository externalServiceHistoryRepository) { 
		this.mainServiceHistoryRepository = mainServiceHistoryRepository;
		this.externalServiceHistoryRepository = externalServiceHistoryRepository; 
	} 

	@Override
	public Flux<MainServiceHistoryDTO> getSumHistory( Pageable pageable ) {       

		return mainServiceHistoryRepository.findAllBy(pageable)
				.flatMap(mainServiceHistory -> {

					return externalServiceHistoryRepository.findById(mainServiceHistory.getExternalServiceId())
							.map(externalServiceHistory -> {  
								var externalServiceHistoryDTO = ExternalServiceHistoryDTO.builder()
										.id(externalServiceHistory.getId())
										.date(externalServiceHistory.getDate())
										.endpoint(externalServiceHistory.getEndpoint())
										.percentage(externalServiceHistory.getPercentage())
										.build(); 

								return MainServiceHistoryDTO.builder() 
										.id(mainServiceHistory.getId())
										.date(mainServiceHistory.getDate())
										.endpoint(mainServiceHistory.getEndpoint())
										.firstNumber(mainServiceHistory.getFirstNumber())
										.secondNumber(mainServiceHistory.getSecondNumber())
										.result(mainServiceHistory.getResult())
										.externalServiceHistory(externalServiceHistoryDTO)
										.build();
							});
				});

	}

	@Override
	public void saveMainServiceHistory( MainServiceHistoryDTO mainServiceHistoryDTO ) {     

		var externalServiceHistory = ExternalServiceHistory.builder()
				.id( mainServiceHistoryDTO.getExternalServiceHistory().getId() )
				.date( LocalDateTime.now() )
				.endpoint( mainServiceHistoryDTO.getExternalServiceHistory().getEndpoint() )			
				.percentage( mainServiceHistoryDTO.getExternalServiceHistory().getPercentage() )
				.build();	

		var mainServiceHistory = MainServiceHistory.builder()      
				.date( LocalDateTime.now() ) 
				.endpoint( mainServiceHistoryDTO.getEndpoint() )
				.firstNumber( mainServiceHistoryDTO.getFirstNumber() )
				.secondNumber( mainServiceHistoryDTO.getSecondNumber() ) 
				.result( mainServiceHistoryDTO.getResult() )
				.externalServiceId(externalServiceHistory.getId())
				.build();		   
		
		mainServiceHistoryRepository.save( mainServiceHistory ) 
		.subscribe(savedEntity -> log.info("Entity saved: {}", savedEntity)); 

	}

}
