package start;

import presentation.Controller;
import presentation.View;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Start {
	protected static final Logger LOGGER = Logger.getLogger(Start.class.getName());

	public static void main(String[] args) {
		try {
			View view = new View();
			Controller controller = new Controller(view);
			controller.showView();

		} catch (Exception ex) {
			LOGGER.log(Level.INFO, ex.getMessage());
		}

	}
}
