package cl.tenpo.challenge.jpa.repository;

import java.math.BigDecimal;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import cl.tenpo.challenge.jpa.entity.ExternalServiceHistory;
import reactor.core.publisher.Mono;

@Repository
public interface ExternalServiceHistoryRepository extends R2dbcRepository<ExternalServiceHistory, Long> {

	Mono<BigDecimal> findTopByPercentageNotNullOrderByIdDesc();             
	  
} 