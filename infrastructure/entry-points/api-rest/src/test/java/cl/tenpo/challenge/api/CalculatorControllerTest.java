package cl.tenpo.challenge.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.SumResponseDTO;
import cl.tenpo.challenge.model.exception.NotFoundException;
import cl.tenpo.challenge.usecase.SumOperationUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CalculatorControllerTest {

	
    @Mock
    private SumOperationUseCase sumOperationUseCase;

    @InjectMocks
    private CalculatorController calculatorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSum() throws NotFoundException { 
        // Arrange
        BigDecimal firstNumber = BigDecimal.valueOf(10);
        BigDecimal secondNumber = BigDecimal.valueOf(20);
        MockHttpServletRequest request = new MockHttpServletRequest();

        SumResponseDTO responseDTO = new SumResponseDTO();
        responseDTO.setResult(BigDecimal.valueOf(30));

        when(sumOperationUseCase.sum(eq(firstNumber), eq(secondNumber), any()))
                .thenReturn(Mono.just(responseDTO));

        // Act
        Mono<ResponseEntity<SumResponseDTO>> result = calculatorController.sum(firstNumber, secondNumber, request);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                        response.getStatusCode() == HttpStatus.CREATED &&
                        response.getBody() != null &&
                        response.getBody().getResult().equals(BigDecimal.valueOf(30))
                )
                .verifyComplete();
    }

    @Test
    void testHistory() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        MainServiceHistoryDTO historyDTO = new MainServiceHistoryDTO();

        when(sumOperationUseCase.history(eq(pageable)))  
                .thenReturn(Flux.just(historyDTO));

        // Act
        ResponseEntity<Flux<MainServiceHistoryDTO>> result = calculatorController.history(pageable);

        // Assert
        assert result.getStatusCode() == HttpStatus.OK;
        assert result.getBody() != null;

    }
    
     
    
}
