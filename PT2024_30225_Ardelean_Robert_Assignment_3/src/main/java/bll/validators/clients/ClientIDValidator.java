package bll.validators.clients;

import bll.validators.ValidationException;
import bll.validators.Validator;
import dao.ClientDAO;
import model.Client;

import java.util.List;

public class ClientIDValidator implements Validator<Client> {

    private final ClientDAO clientDAO;

    public ClientIDValidator() {
        this.clientDAO = new ClientDAO();
    }

    @Override
    public void validate(Client client) {
        List<Client> clients = clientDAO.findAll();
        for (Client existingClient : clients) {
            if (existingClient.getClient_id() == client.getClient_id()) {
                throw new ValidationException("Client ID must be unique. ID " + client.getClient_id() + " already exists.");
            }
        }
    }
}
