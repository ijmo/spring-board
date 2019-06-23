package ijmo.demo.springboard;

public class CannotDeleteException extends Exception {
    public CannotDeleteException() {
        super();
    }

    public CannotDeleteException(String message) {
        super(message);
    }
}
