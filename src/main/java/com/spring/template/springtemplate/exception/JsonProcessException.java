package com.spring.template.springtemplate.exception;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Ali Mojahed on 6/3/2021
 * @project spring-template
 **/

public class JsonProcessException extends ProjectException {

    public JsonProcessException(Exception exception, String simpleClassName) {
        super(ExceptionStatus.INVALID_JSON);
        generateMessage(exception, simpleClassName);
    }

    public JsonProcessException(Exception exception) {
        super(ExceptionStatus.INVALID_JSON);
        generateMessage(exception, "");
    }

    private void generateMessage(Exception exception, String simpleClassName) {
        String exceptionMessage = exception.getMessage();

        message = "Invalid json format";

        if (exception instanceof JsonMappingException && exceptionMessage.startsWith("Can not deserialize instance")) {
            message += " - ";
            int index = exceptionMessage.indexOf("line:");
            message += exceptionMessage.substring(index, exceptionMessage.indexOf("]", index));
            message += " - " + simpleClassName;
        }

        super.initCause(exception);

    }
}
