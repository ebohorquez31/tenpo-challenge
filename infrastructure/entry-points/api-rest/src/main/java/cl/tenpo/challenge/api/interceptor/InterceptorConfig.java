package cl.tenpo.challenge.api.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class InterceptorConfig implements WebMvcConfigurer {
    
	@Autowired
    Interceptor interceptor;  
  
    @Override
    public void addInterceptors(InterceptorRegistry registry) {    
        registry.addInterceptor(interceptor);   
    }
}
