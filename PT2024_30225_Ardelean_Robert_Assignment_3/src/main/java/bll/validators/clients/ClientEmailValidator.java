package bll.validators.clients;

import bll.validators.ValidationException;
import bll.validators.Validator;
import model.Client;

public class ClientEmailValidator implements Validator<Client> {

	private static final String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
	@Override
	public void validate(Client client) {
		if (!client.getEmail().matches(emailRegex)) {
			throw new ValidationException("The Email format is not respected!");
		}
	}
}
