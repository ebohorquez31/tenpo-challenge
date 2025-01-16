package cl.tenpo.challenge.api.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import cl.tenpo.challenge.model.ErrorResponse;
import cl.tenpo.challenge.model.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@AllArgsConstructor
@Slf4j
public class ExceptionHandlerController {  

	@ResponseStatus( HttpStatus.NOT_FOUND )
	@ExceptionHandler( NotFoundException.class )
	public ErrorResponse handleNotFoundException( NotFoundException notFoundException,
			WebRequest request ) {

		var errorResponse = ErrorResponse.builder()
				.title( HttpStatus.NOT_FOUND.name() )
				.path( request.getDescription(false) ) 
				.message( notFoundException.getMessage() )
				.date( new Date() )
				.build();

		log.info("Error response: {}", errorResponse );  

		return errorResponse;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST) 
	@ExceptionHandler(NumberFormatException.class)
	public ErrorResponse handleBadRequestException( NumberFormatException exception, 
			WebRequest request ) {

		var errorResponse = ErrorResponse.builder()
				.title( HttpStatus.BAD_REQUEST.name() )
				.path( request.getDescription(false) )
				.message( exception.getMessage() )
				.date( new Date() )
				.build();

		log.info("Error response: {}", errorResponse );  

		return errorResponse;
	}

}
