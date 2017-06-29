package cz.vutbr.fit.ateam.web.commons;

import java.io.Serializable;

/**
 * Wrapper for HTTP responses.
 */
public class Response implements Serializable {
    private int error;
    private String message;
    private Object data;

    protected Response(Code code, Object data) {
        this(code, null, data);
    }

    protected Response(Code code, String message) {
        this(code, message, null);
    }

    protected Response(Code code) {
        this(code, null, null);
    }

    protected Response(Code error, String message, Object data) {
        this.error = error.value();
        this.message = message;
        this.data = data;
    }

    public static Response ok() {return new Response(Code.OK, null, null);}

    public static Response ok(String message) {return new Response(Code.OK, message, null);}

    public static Response ok(Object data) {return new Response(Code.OK, null, data);}

    public static Response ok(Object data, String message) {return new Response(Code.OK, message, data);}

    public static Response paramMissing() {return new Response(Code.PARAM_MISSING, null, null);}

    public static Response paramMissing(String message) {return new Response(Code.PARAM_MISSING, message, null);}

    public static Response paramMissing(Object data) {return new Response(Code.PARAM_MISSING, null, data);}

    public static Response paramMissing(Object data, String message) {return new Response(Code.PARAM_MISSING, message, data);}

    public static Response hibernateException() {return new Response(Code.HIBERNATE_EXCEPTION, null, null);}

    public static Response hibernateException(String message) {return new Response(Code.HIBERNATE_EXCEPTION, message, null);}

    public static Response hibernateException(Object data) {return new Response(Code.HIBERNATE_EXCEPTION, null, data);}

    public static Response hibernateException(Object data, String message) {return new Response(Code.HIBERNATE_EXCEPTION, message, data);}

    public static Response recordExists() {return new Response(Code.RECORD_EXISTS, null, null);}

    public static Response recordExists(String message) {return new Response(Code.RECORD_EXISTS, message, null);}

    public static Response recordExists(Object data) {return new Response(Code.RECORD_EXISTS, null, data);}

    public static Response recordExists(Object data, String message) {return new Response(Code.RECORD_EXISTS, message, data);}

    public static Response userNotFound() {return new Response(Code.USER_NOT_FOUND, null, null);}

    public static Response userNotFound(String message) {return new Response(Code.USER_NOT_FOUND, message, null);}

    public static Response userNotFound(Object data) {return new Response(Code.USER_NOT_FOUND, null, data);}

    public static Response userNotFound(Object data, String message) {return new Response(Code.USER_NOT_FOUND, message, data);}

    public static Response authError() {return new Response(Code.AUTH_ERROR, null, null);}

    public static Response authError(String message) {return new Response(Code.AUTH_ERROR, message, null);}

    public static Response authError(Object data) {return new Response(Code.AUTH_ERROR, null, data);}

    public static Response authError(Object data, String message) {return new Response(Code.AUTH_ERROR, message, data);}

    public static Response missingRecord() {return new Response(Code.MISSING_RECORD, null, null);}

    public static Response missingRecord(String message) {return new Response(Code.MISSING_RECORD, message, null);}

    public static Response missingRecord(Object data) {return new Response(Code.MISSING_RECORD, null, data);}

    public static Response missingRecord(Object data, String message) {return new Response(Code.MISSING_RECORD, message, data);}

    public static Response actionDenied() {return new Response(Code.ACTION_DENIED, null, null);}

    public static Response actionDenied(String message) {return new Response(Code.ACTION_DENIED, message, null);}

    public static Response actionDenied(Object data) {return new Response(Code.ACTION_DENIED, null, data);}

    public static Response actionDenied(Object data, String message) {return new Response(Code.ACTION_DENIED, message, data);}

    public static Response operationFailed() {return new Response(Code.OPERATION_FAILED, null, null); }

    public static Response operationFailed(String message) {return new Response(Code.OPERATION_FAILED, message, null); }

    public static Response operationFailed(Object data) {return new Response(Code.OPERATION_FAILED, null, data); }

    public static Response operationFailed(Object data, String message) {return new Response(Code.OPERATION_FAILED, message, data); }

    public int getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public enum Code {
        OK(0),
        PARAM_MISSING(1),
        HIBERNATE_EXCEPTION(2),
        RECORD_EXISTS(3),
        USER_NOT_FOUND(4),
        AUTH_ERROR(5),
        MISSING_RECORD(6),
        ACTION_DENIED(7),
        OPERATION_FAILED(8);

        private int errCode;

        Code(int errCode) {
            this.errCode = errCode;
        }

        public int value() {
            return errCode;
        }

    }
}
