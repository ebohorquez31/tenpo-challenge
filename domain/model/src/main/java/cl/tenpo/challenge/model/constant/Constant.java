package cl.tenpo.challenge.model.constant;

import java.math.BigDecimal;

public class Constant {

	private Constant() {}

	public static final String RESPONSE_PERCENTAGE = "percentage";
	public static final String FIRST_NUMBER = "firstNumber";
	public static final String SECOND_NUMBER = "secondNumber";
	public static final String FINAL_RESULT = "result";
	public static final String CONTENT_TYPE = "application/json";
	public static final String URL = "URL";
	public static final String MESSAGE_TOO_MANY_REQUESTS = "You have exceeded the maximum request limit";

	public static final BigDecimal ONE = new BigDecimal(1);
	public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);


}
