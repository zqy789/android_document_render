

package com.document.render.office.fc.openxml4j.exceptions;


@SuppressWarnings("serial")
public class OpenXML4JRuntimeException extends RuntimeException {

    public OpenXML4JRuntimeException(String msg) {
        super(msg);
    }

    public OpenXML4JRuntimeException(String msg, Throwable reason) {
        super(msg, reason);
    }
}
