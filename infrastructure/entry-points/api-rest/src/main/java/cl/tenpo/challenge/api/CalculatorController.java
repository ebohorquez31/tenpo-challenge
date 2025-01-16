package cl.tenpo.challenge.api;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.challenge.model.constant.Constant;
import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.SumResponseDTO;
import cl.tenpo.challenge.model.exception.NotFoundException;
import cl.tenpo.challenge.usecase.SumOperationUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/calculator", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") 
public class CalculatorController {      

	private SumOperationUseCase sumOperationUseCase;	

	@PostMapping(path = "/sum")
	public Mono<ResponseEntity<SumResponseDTO>> sum(       
			@RequestParam(name = Constant.FIRST_NUMBER) BigDecimal firstNumber,
			@RequestParam(name = Constant.SECOND_NUMBER) BigDecimal secondNumber, 
			HttpServletRequest request ) throws NotFoundException {         
		  
		log.info("Request: firstNumber {}, secondNumber {}", firstNumber, secondNumber);    

        return sumOperationUseCase.sum(firstNumber, secondNumber, request)  
                .doOnNext(sumResponseDTO -> log.info("sumResponseDTO: {}", sumResponseDTO))    
                .map(sumResponseDTO -> new ResponseEntity<>(sumResponseDTO, HttpStatus.CREATED));
	}
 
	@GetMapping(path = "/history")  
	public ResponseEntity<Flux<MainServiceHistoryDTO>> history( Pageable pageable ) {       		

		var mainServiceHistoryDTOList = sumOperationUseCase.history(pageable);
	    mainServiceHistoryDTOList.doOnNext(
	    		mainServiceHistoryDTO -> log.info("history response: {}", mainServiceHistoryDTO));

	    return ResponseEntity.ok(mainServiceHistoryDTOList);  
		
	} 

}
