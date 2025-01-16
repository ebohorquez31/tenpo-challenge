package cl.tenpo.challenge.api.filter;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;  
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;  

import org.springframework.http.HttpStatus;

import com.google.gson.Gson;

import cl.tenpo.challenge.model.ErrorResponse;
import cl.tenpo.challenge.model.constant.Constant;
import cl.tenpo.challenge.model.properties.ApiRateLimitProperty;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

public class RequestFilter implements Filter {       

	private final Bucket bucket;

	public RequestFilter( ApiRateLimitProperty apiRateLimitProperty ) {   

		var numRequests = Integer.parseInt( apiRateLimitProperty.getNumRequests() );  
		var numMinutes = Integer.parseInt( apiRateLimitProperty.getNumMinutes() );
		var limit = Bandwidth.classic(numRequests, 
				Refill.intervally(numRequests, Duration.ofMinutes( numMinutes )));
		this.bucket = Bucket4j.builder().addLimit(limit).build();  
	}        


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		if (bucket.tryConsume(1)) {
			chain.doFilter(request, response);
		}else {
			httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
			httpServletResponse.setContentType(Constant.CONTENT_TYPE);
			ErrorResponse error = new ErrorResponse( HttpStatus.TOO_MANY_REQUESTS.name(), 
					httpServletRequest.getRequestURL().toString(), new Date(), 
					Constant.MESSAGE_TOO_MANY_REQUESTS );
			httpServletResponse.getWriter().write(new Gson().toJson(error));
		}
	}   

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {		
	}

	@Override
	public void destroy() {
	}

}
