package model.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException	{

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> errors = new HashMap<>();

	public ValidationException(String msg)	{
		super(msg);
	}
	
	public void addErrors(String name, String erroMessage)	{
		errors.put(name, erroMessage);
	}
	
	public Map<String, String> getErrors() {
		return errors;
	}
}
