package cl.tenpo.challenge.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableR2dbcRepositories
@RequiredArgsConstructor
public class PostgresConfig extends AbstractR2dbcConfiguration {   
 
	@Value("${db-connection-data.host}")
    private String host;

    @Value("${db-connection-data.database}") 
    private String database;

    @Value("${db-connection-data.port}")
    private int port;

    @Value("${spring.r2dbc.username}")  
    private String username;

    @Value("${spring.r2dbc.password}")  
    private String password;
	
	@Override
	public ConnectionFactory connectionFactory() {  
		PostgresqlConnectionConfiguration configuration = PostgresqlConnectionConfiguration
                .builder()
                .host(host)
                .database(database)  
                .username(username)
                .password(password)
                .port(port)
                .build();
    	
        return new PostgresqlConnectionFactory(configuration);  

	}
	
	@Bean
    public ConnectionPool connectionPool() { 
      
        ConnectionPoolConfiguration.Builder builder = 
        		ConnectionPoolConfiguration.builder(connectionFactory())
        		.maxSize(20)                     
                .initialSize(10)                           
                .maxIdleTime(Duration.ofMinutes(10))       
                .maxLifeTime(Duration.ofMinutes(30))        
                .validationQuery("SELECT 1");       

        return new ConnectionPool(builder.build());
    }


}
