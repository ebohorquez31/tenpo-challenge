package cl.tenpo.challenge.usecase;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import cl.tenpo.challenge.model.constant.Constant;
import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.SumResponseDTO;
import cl.tenpo.challenge.model.exception.NotFoundException;
import cl.tenpo.challenge.model.gateway.ExternalServiceGateway;
import cl.tenpo.challenge.model.gateway.ExternalServiceHistoryGateway;
import cl.tenpo.challenge.model.gateway.MainServiceHistoryGateway;
import cl.tenpo.challenge.model.properties.ExternalServiceProperty;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SumOperationUseCase {

	private ExternalServiceHistoryGateway externalServiceHistoryGateway;
	private MainServiceHistoryGateway mainServiceHistoryGateway;
	private ExternalServiceGateway externalServiceGateway;
	private ExternalServiceProperty externalServiceProperty;

	public SumOperationUseCase( ExternalServiceHistoryGateway externalServiceHistoryGateway, 
			MainServiceHistoryGateway mainServiceHistoryGateway,  
			ExternalServiceGateway externalServiceGateway, 
			ExternalServiceProperty externalServiceProperty ) {

		this.externalServiceHistoryGateway = externalServiceHistoryGateway;
		this.externalServiceGateway = externalServiceGateway;
		this.mainServiceHistoryGateway = mainServiceHistoryGateway;
		this.externalServiceProperty = externalServiceProperty;
	}

	public Mono<SumResponseDTO> sum( BigDecimal firstNumber, BigDecimal secondNumber, 
			HttpServletRequest httpServlet ) throws NotFoundException {               	

        return externalServiceGateway.consumeService(
        		Integer.parseInt(externalServiceProperty.getNumAttemps()),
                externalServiceProperty.getEndpoint())
                .flatMap(response -> {   
                    var percentage = response.getPercentage();  
                    httpServlet.setAttribute(Constant.RESPONSE_PERCENTAGE, percentage);
                    return Mono.just(percentage); 
                })
                .switchIfEmpty(
                        externalServiceHistoryGateway.getLastPercentageDifferentFromNull()
                                .switchIfEmpty(Mono.error(new NotFoundException("No percentage found")))
                )
                .flatMap(percentage -> {
                    var result = (firstNumber.add(secondNumber))
                            .multiply((Constant.ONE_HUNDRED.add(percentage)).divide(Constant.ONE_HUNDRED));
                    httpServlet.setAttribute(Constant.FINAL_RESULT, result);
                    return Mono.just(new SumResponseDTO(result));
                });   
				
	}  

	public Flux<MainServiceHistoryDTO> history( Pageable pageable ) { 

		return mainServiceHistoryGateway.getSumHistory(pageable); 

	}  

	@Async
	public void saveHistoryAsync( MainServiceHistoryDTO mainServiceHistoryDTOParam ) {   				

		var externalServiceHistory = new ExternalServiceHistoryDTO();      
		externalServiceHistory.setEndpoint( externalServiceProperty.getEndpoint() );
		externalServiceHistory.setPercentage( 
				mainServiceHistoryDTOParam.getExternalServiceHistory().getPercentage() );

		var externalServiceHistoryDTO = 
				externalServiceHistoryGateway.saveExternalServiceHistory( externalServiceHistory );

		var mainServiceHistoryDTO = new MainServiceHistoryDTO();
		mainServiceHistoryDTO.setEndpoint( mainServiceHistoryDTOParam.getEndpoint() );  
		mainServiceHistoryDTO.setFirstNumber( mainServiceHistoryDTOParam.getFirstNumber() );      
		mainServiceHistoryDTO.setSecondNumber( mainServiceHistoryDTOParam.getSecondNumber() );  
		mainServiceHistoryDTO.setResult( mainServiceHistoryDTOParam.getResult() );
		mainServiceHistoryDTO.setExternalServiceHistory( externalServiceHistoryDTO.block() );   
		mainServiceHistoryGateway.saveMainServiceHistory(mainServiceHistoryDTO);

	}

}
