package cl.tenpo.challenge.usecase.mapper;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import cl.tenpo.challenge.model.constant.Constant;
import cl.tenpo.challenge.model.dto.ExternalServiceHistoryDTO;
import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;

public class MainServiceHistoryMapperDTO {

    private MainServiceHistoryMapperDTO() {}   
    
    public static MainServiceHistoryDTO mapperToMainServiceHistoryDTO( HttpServletRequest request ) {

		BigDecimal firstNumber = null;
		if( request.getParameter(Constant.FIRST_NUMBER) != null ) {
			firstNumber = new BigDecimal( request.getParameter(Constant.FIRST_NUMBER) );
		}    

		BigDecimal secondNumber = null;
		if( request.getParameter(Constant.SECOND_NUMBER) != null ) {  
			secondNumber = new BigDecimal( request.getParameter(Constant.SECOND_NUMBER) );
		}

		BigDecimal result = null;
		if( request.getAttribute(Constant.FINAL_RESULT) != null ) {  
			result = new BigDecimal( request.getAttribute(Constant.FINAL_RESULT).toString() );
		}  

		BigDecimal percentage = null;
		if( request.getAttribute(Constant.RESPONSE_PERCENTAGE) != null ) {			
			percentage = new BigDecimal( request.getAttribute(Constant.RESPONSE_PERCENTAGE).toString() );
		}

		ExternalServiceHistoryDTO externalServiceHistoryDTO = ExternalServiceHistoryDTO.builder()
				.percentage(percentage)
				.build();

		return MainServiceHistoryDTO.builder()
				.endpoint( request.getRequestURL().toString() )
				.firstNumber( firstNumber )
				.secondNumber( secondNumber )
				.result( result )
				.externalServiceHistory( externalServiceHistoryDTO )
				.build();

	}

    
}
