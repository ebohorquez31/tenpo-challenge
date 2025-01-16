package cl.tenpo.challenge.api.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cl.tenpo.challenge.model.dto.MainServiceHistoryDTO;
import cl.tenpo.challenge.usecase.SumOperationUseCase;
import cl.tenpo.challenge.usecase.mapper.MainServiceHistoryMapperDTO;

@Component
public class Interceptor implements HandlerInterceptor {

	@Autowired
	private SumOperationUseCase sumOperationUseCase;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
			Object handler, Exception ex) throws Exception {		
		
		if( request.getRequestURL().toString().contains("sum") ) {	
			
			MainServiceHistoryDTO mainServiceHistoryDTO = 
					MainServiceHistoryMapperDTO.mapperToMainServiceHistoryDTO( request );
			sumOperationUseCase.saveHistoryAsync( mainServiceHistoryDTO );

		}

	}

}
