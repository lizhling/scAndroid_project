package com.sunrise.micromarketing.exception.http;

/**
 * HTTP StatusCode is not 200
 *  NOT_MODIFIED: 304
 *  BAD_REQUEST: 400 
 *  NOT_FOUND: 404 
 *  NOT_ACCEPTABLE: 406
 */
public class HttpRequestException extends HttpException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9070196375621069060L;

	public HttpRequestException(Exception cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public HttpRequestException(String msg, Exception cause, int statusCode) {
        super(msg, cause, statusCode);
        // TODO Auto-generated constructor stub
    }

    public HttpRequestException(String msg, Exception cause) {
        super(msg, cause);
        // TODO Auto-generated constructor stub
    }

    public HttpRequestException(String msg, int statusCode) {
        super(msg, statusCode);
        // TODO Auto-generated constructor stub
    }

    public HttpRequestException(String msg) {
        super(msg);
        // TODO Auto-generated constructor stub
    }

}
