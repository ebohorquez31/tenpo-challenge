package cl.tenpo.challenge.jpa.adapter;

import static org.mockito.ArgumentMatchers.any;    
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cl.tenpo.challenge.jpa.entity.ExternalServiceHistory;
import cl.tenpo.challenge.jpa.repository.ExternalServiceHistoryRepository;
import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ExternalServiceHistoryGatewayImplTest {

    @Mock
    private ExternalServiceHistoryRepository externalServiceHistoryRepository;

    @InjectMocks
    private ExternalServiceHistoryGatewayImpl externalServiceHistoryGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveExternalServiceHistory() {
        // Arrange
        ExternalServiceHistoryDTO externalServiceHistoryDTO = ExternalServiceHistoryDTO.builder()
                .endpoint("http://test-endpoint.com")
                .percentage(BigDecimal.valueOf(12.5))
                .build();

        ExternalServiceHistory externalServiceHistory = ExternalServiceHistory.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .endpoint("http://test-endpoint.com")
                .percentage(BigDecimal.valueOf(12.5))
                .build();

        when(externalServiceHistoryRepository.save(any(ExternalServiceHistory.class)))
                .thenReturn(Mono.just(externalServiceHistory));

        // Act
        Mono<ExternalServiceHistoryDTO> result = externalServiceHistoryGateway.saveExternalServiceHistory(externalServiceHistoryDTO);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedDTO -> 
                        savedDTO.getId() == 1L &&
                        savedDTO.getEndpoint().equals("http://test-endpoint.com") &&
                        savedDTO.getPercentage().equals(BigDecimal.valueOf(12.5))
                )
                .verifyComplete();
    }

    @Test
    void testGetLastPercentageDifferentFromNull() {
        // Arrange
        BigDecimal expectedPercentage = BigDecimal.valueOf(15.5);

        when(externalServiceHistoryRepository.findTopByPercentageNotNullOrderByIdDesc())
                .thenReturn(Mono.just(expectedPercentage));

        // Act
        Mono<BigDecimal> result = externalServiceHistoryGateway.getLastPercentageDifferentFromNull();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(percentage -> percentage.equals(BigDecimal.valueOf(15.5)))
                .verifyComplete();  
    }
    
}
