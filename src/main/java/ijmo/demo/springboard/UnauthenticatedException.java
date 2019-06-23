package ijmo.demo.springboard;


public class UnauthenticatedException extends Exception {
    public UnauthenticatedException() {
        super();
    }

    public UnauthenticatedException(String message) {
        super(message);
    }
}