package cl.tenpo.challenge.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import cl.tenpo.challenge.jpa.entity.MainServiceHistory; 
import reactor.core.publisher.Flux;  

@Repository
public interface MainServiceHistoryRepository extends R2dbcRepository<MainServiceHistory, Long> {
 
	Flux<MainServiceHistory> findAllBy(Pageable pageable); 
	 
}   
 