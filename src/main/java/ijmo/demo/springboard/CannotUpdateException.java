package ijmo.demo.springboard;

public class CannotUpdateException extends Exception {
    public CannotUpdateException() {
        super();
    }

    public CannotUpdateException(String message) {
        super(message);
    }
}
