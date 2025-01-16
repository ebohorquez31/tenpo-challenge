package cl.tenpo.challenge.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.SumResponseDTO;
import cl.tenpo.challenge.model.exception.NotFoundException;
import cl.tenpo.challenge.model.gateway.ExternalServiceGateway;
import cl.tenpo.challenge.model.gateway.ExternalServiceHistoryGateway;
import cl.tenpo.challenge.model.gateway.MainServiceHistoryGateway;
import cl.tenpo.challenge.model.properties.ExternalServiceProperty;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class SumOperationUseCaseTest {

    @Mock
    private ExternalServiceHistoryGateway externalServiceHistoryGateway;

    @Mock
    private MainServiceHistoryGateway mainServiceHistoryGateway;

    @Mock
    private ExternalServiceGateway externalServiceGateway;

    @Mock
    private ExternalServiceProperty externalServiceProperty;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private SumOperationUseCase sumOperationUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sumOperationUseCase = new SumOperationUseCase(
                externalServiceHistoryGateway,
                mainServiceHistoryGateway,
                externalServiceGateway,
                externalServiceProperty
        );
    }


    @Test
    void testSum_FallbackToLastPercentage() throws NotFoundException {
        // Arrange
        BigDecimal firstNumber = BigDecimal.valueOf(100);
        BigDecimal secondNumber = BigDecimal.valueOf(50);
        BigDecimal fallbackPercentage = BigDecimal.valueOf(5);
        BigDecimal expectedResult = BigDecimal.valueOf(157.5);

        when(externalServiceProperty.getNumAttemps()).thenReturn("3");
        when(externalServiceProperty.getEndpoint()).thenReturn("http://mock-endpoint.com");
        when(externalServiceGateway.consumeService(3, "http://mock-endpoint.com"))
                .thenReturn(Mono.empty());
        when(externalServiceHistoryGateway.getLastPercentageDifferentFromNull())
                .thenReturn(Mono.just(fallbackPercentage));

        // Act
        Mono<SumResponseDTO> result = sumOperationUseCase.sum(firstNumber, secondNumber, httpServletRequest);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getResult().compareTo(expectedResult) == 0)
                .verifyComplete();
    }

    @Test
    void testSum_PercentageNotFound() throws NotFoundException {
        // Arrange
        BigDecimal firstNumber = BigDecimal.valueOf(100);
        BigDecimal secondNumber = BigDecimal.valueOf(50);

        when(externalServiceProperty.getNumAttemps()).thenReturn("3");
        when(externalServiceProperty.getEndpoint()).thenReturn("http://mock-endpoint.com");
        when(externalServiceGateway.consumeService(3, "http://mock-endpoint.com"))
                .thenReturn(Mono.empty());
        when(externalServiceHistoryGateway.getLastPercentageDifferentFromNull())
                .thenReturn(Mono.empty());
 
        // Act
        Mono<SumResponseDTO> result = sumOperationUseCase.sum(firstNumber, secondNumber, httpServletRequest);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof NotFoundException
                        && "No percentage found".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    void testHistory() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        MainServiceHistoryDTO historyDTO = MainServiceHistoryDTO.builder()
                .id(1L)
                .result(BigDecimal.valueOf(150))
                .build();

        when(mainServiceHistoryGateway.getSumHistory(pageable)).thenReturn(Flux.just(historyDTO));

        // Act
        Flux<MainServiceHistoryDTO> result = sumOperationUseCase.history(pageable);

        // Assert
        StepVerifier.create(result)
                .expectNext(historyDTO)
                .verifyComplete();
    }

    @Test
    void testSaveHistoryAsync() {
        // Arrange
        ExternalServiceHistoryDTO externalServiceHistoryDTO = ExternalServiceHistoryDTO.builder()
                .id(1L)
                .percentage(BigDecimal.valueOf(10))
                .build();
        MainServiceHistoryDTO mainServiceHistoryDTO = MainServiceHistoryDTO.builder()
                .endpoint("http://mock-endpoint.com")
                .firstNumber(BigDecimal.valueOf(100))
                .secondNumber(BigDecimal.valueOf(50))
                .result(BigDecimal.valueOf(165))
                .externalServiceHistory(externalServiceHistoryDTO)
                .build();

        when(externalServiceHistoryGateway.saveExternalServiceHistory(any()))
                .thenReturn(Mono.just(externalServiceHistoryDTO));

        // Act
        sumOperationUseCase.saveHistoryAsync(mainServiceHistoryDTO);

        // Assert
        verify(externalServiceHistoryGateway).saveExternalServiceHistory(any(ExternalServiceHistoryDTO.class));
        verify(mainServiceHistoryGateway).saveMainServiceHistory(any(MainServiceHistoryDTO.class));
    }
}
