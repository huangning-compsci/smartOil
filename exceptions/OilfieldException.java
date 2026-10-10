package exceptions;

public class OilfieldException extends Exception {   //为什么要继承Exception类？

public OilfieldException(String message) {
        super(message);
    }

public OilfieldException(String message, Throwable cause) {
        super(message, cause);
}
}
