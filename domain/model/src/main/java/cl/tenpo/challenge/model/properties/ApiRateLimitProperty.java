package cl.tenpo.challenge.model.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiRateLimitProperty {

	private String numRequests;
	private String numMinutes;
	
}
