package ijmo.demo.springboard;

public class CannotCreateException extends Exception {
    public CannotCreateException() {
        super();
    }

    public CannotCreateException(String message) {
        super(message);
    }
}
