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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import cl.tenpo.challenge.jpa.entity.ExternalServiceHistory;
import cl.tenpo.challenge.jpa.entity.MainServiceHistory;
import cl.tenpo.challenge.jpa.repository.ExternalServiceHistoryRepository;
import cl.tenpo.challenge.jpa.repository.MainServiceHistoryRepository;
import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MainServiceHistoryGatewayImplTest {

    @Mock
    private MainServiceHistoryRepository mainServiceHistoryRepository;

    @Mock
    private ExternalServiceHistoryRepository externalServiceHistoryRepository;

    @InjectMocks
    private MainServiceHistoryGatewayImpl mainServiceHistoryGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSumHistory() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        MainServiceHistory mainServiceHistory = MainServiceHistory.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .endpoint("/sum")
                .firstNumber(BigDecimal.valueOf(10))
                .secondNumber(BigDecimal.valueOf(5))
                .result(BigDecimal.valueOf(15))
                .externalServiceId(2L)
                .build();

        ExternalServiceHistory externalServiceHistory = ExternalServiceHistory.builder()
                .id(2L)
                .date(LocalDateTime.now())
                .endpoint("/external-service")
                .percentage(BigDecimal.valueOf(10.5))
                .build();

        when(mainServiceHistoryRepository.findAllBy(pageable))
                .thenReturn(Flux.just(mainServiceHistory));

        when(externalServiceHistoryRepository.findById(2L))
                .thenReturn(Mono.just(externalServiceHistory));

        // Act
        Flux<MainServiceHistoryDTO> result = mainServiceHistoryGateway.getSumHistory(pageable);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(historyDTO ->
                        historyDTO.getId() == 1L &&
                        historyDTO.getFirstNumber().equals(BigDecimal.valueOf(10)) &&
                        historyDTO.getSecondNumber().equals(BigDecimal.valueOf(5)) &&
                        historyDTO.getResult().equals(BigDecimal.valueOf(15)) &&
                        historyDTO.getExternalServiceHistory().getId() == 2L &&
                        historyDTO.getExternalServiceHistory().getPercentage().equals(BigDecimal.valueOf(10.5))
                )
                .verifyComplete();
    }

    @Test
    void testSaveMainServiceHistory() {
        // Arrange
        ExternalServiceHistoryDTO externalServiceHistoryDTO = ExternalServiceHistoryDTO.builder()
                .id(2L)
                .endpoint("/external-service")
                .percentage(BigDecimal.valueOf(10.5))
                .build();

        MainServiceHistoryDTO mainServiceHistoryDTO = MainServiceHistoryDTO.builder()
                .date(LocalDateTime.now())
                .endpoint("/sum")
                .firstNumber(BigDecimal.valueOf(10))
                .secondNumber(BigDecimal.valueOf(5))
                .result(BigDecimal.valueOf(15))
                .externalServiceHistory(externalServiceHistoryDTO)
                .build();

        ExternalServiceHistory externalServiceHistory = ExternalServiceHistory.builder()
                .id(2L)
                .date(LocalDateTime.now())
                .endpoint("/external-service")
                .percentage(BigDecimal.valueOf(10.5))
                .build();

        MainServiceHistory mainServiceHistory = MainServiceHistory.builder()
                .date(LocalDateTime.now())
                .endpoint("/sum")
                .firstNumber(BigDecimal.valueOf(10))
                .secondNumber(BigDecimal.valueOf(5))
                .result(BigDecimal.valueOf(15))
                .externalServiceId(2L)
                .build();

        when(mainServiceHistoryRepository.save(any(MainServiceHistory.class)))
                .thenReturn(Mono.just(mainServiceHistory));

        // Act
        mainServiceHistoryGateway.saveMainServiceHistory(mainServiceHistoryDTO);

        // Assert
        StepVerifier.create(mainServiceHistoryRepository.save(mainServiceHistory))
                .expectNextMatches(savedEntity ->
                        savedEntity.getFirstNumber().equals(BigDecimal.valueOf(10)) &&
                        savedEntity.getSecondNumber().equals(BigDecimal.valueOf(5)) &&
                        savedEntity.getResult().equals(BigDecimal.valueOf(15)) &&
                        savedEntity.getExternalServiceId() == 2L
                )
                .verifyComplete();
    }
}
