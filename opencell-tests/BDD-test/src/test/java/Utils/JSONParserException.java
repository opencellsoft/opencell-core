package Utils;

public class JSONParserException extends RuntimeException{

    public JSONParserException() {
    }

    public JSONParserException(String message) {
        super(message);
    }

    public JSONParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONParserException(Throwable cause) {
        super(cause);
    }

    public JSONParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
