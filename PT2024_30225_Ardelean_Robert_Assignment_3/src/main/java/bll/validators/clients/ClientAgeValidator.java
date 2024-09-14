package bll.validators.clients;

import bll.validators.ValidationException;
import bll.validators.Validator;
import model.Client;

public class ClientAgeValidator implements Validator<Client> {
	@Override
	public void validate(Client client) {
		if (client.getAge() < 18 || client.getAge() > 60) {
			throw new ValidationException("The Client Age limit is not respected!");
		}
	}
}
