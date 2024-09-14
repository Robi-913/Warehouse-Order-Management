package presentation;

public class Controller {
    private final View view;

    public Controller(View view) {
        this.view = view;
    }

    public void showView() {
        view.setVisible(true);
    }
}
