package cl.tenpo.challenge.model.gateway;

import org.springframework.data.domain.Pageable;

import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import reactor.core.publisher.Flux; 

public interface MainServiceHistoryGateway {
	
	public void saveMainServiceHistory( MainServiceHistoryDTO mainServiceHistoryDTO);
	
	public Flux<MainServiceHistoryDTO> getSumHistory( Pageable pageable ); 
	
}   
 