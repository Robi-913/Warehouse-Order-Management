package bll;

import bll.validators.ValidationException;
import bll.validators.Validator;
import bll.validators.clients.ClientAgeValidator;
import bll.validators.clients.ClientEmailValidator;
import bll.validators.clients.ClientIDValidator;
import dao.ClientDAO;
import model.Client;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ClientBLL {
	private final ClientDAO clientDAO;
	private final List<Validator<Client>> validators;


	public ClientBLL() {
		clientDAO = new ClientDAO();
		validators = new ArrayList<>();
		validators.add(new ClientEmailValidator());
		validators.add(new ClientAgeValidator());
	}

	public Client findClientById(int id) {
		return clientDAO.findById(id);
	}

	public List<Client> findAllClients() {
		return clientDAO.findAll();
	}

	public void addClient(Client client) {
		validators.add(new ClientIDValidator());
		for (Validator<Client> validator : validators) {
			validator.validate(client);
		}
		validators.remove(new ClientIDValidator());
		clientDAO.insert(client);
	}

	public void updateClient(Client client) {
		for (Validator<Client> validator : validators) {
			validator.validate(client);
		}
		clientDAO.update(client);
	}

	public void deleteClient(Client client) throws ValidationException, SQLException {
		clientDAO.delete(client);
	}

}
