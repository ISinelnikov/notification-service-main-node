package notification.service.backend.repository.base;

public class ModelModificationException extends Throwable {
    public ModelModificationException() {
    }

    public ModelModificationException(String message) {
        super(message);
    }

    public ModelModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
