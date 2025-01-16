package cl.tenpo.challenge.model.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExternalServiceProperty {

	private String endpoint;
	private String numAttemps;

}
