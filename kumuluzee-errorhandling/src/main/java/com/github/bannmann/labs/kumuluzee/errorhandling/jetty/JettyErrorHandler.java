package com.github.bannmann.labs.kumuluzee.errorhandling.jetty;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Dispatcher;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;

public class JettyErrorHandler extends ErrorHandler
{
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String UNKNOWN_ERROR = "Unknown error";

    @Override
    public void handle(
        String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType(CONTENT_TYPE);

        int code = getStatusOrDefault(request);
        response.setStatus(code);

        String message = getMessageOrDefault(baseRequest, request);
        String body = createBody(message);
        response.getWriter()
            .write(body);
    }

    private Integer getStatusOrDefault(HttpServletRequest request)
    {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (code == null)
        {
            code = HttpStatus.INTERNAL_SERVER_ERROR_500;
        }
        return code;
    }

    private String getMessageOrDefault(Request baseRequest, HttpServletRequest request)
    {
        String message = (String) request.getAttribute(Dispatcher.ERROR_MESSAGE);
        if (message == null)
        {
            message = baseRequest.getResponse()
                .getReason();
        }
        if (message == null)
        {
            message = UNKNOWN_ERROR;
        }
        return message;
    }

    private String createBody(String message)
    {
        // TODO to ensure compatibility, make original ErrorDto classes accessible and use JSON-B instead.

        var errorMessage = createObjectBuilder().add("errorId", JettyError.class.getName())
            .add("parameters", createObjectBuilder().add("Message", message));

        var globalErrorList = createArrayBuilder().add(errorMessage);

        var errorMap = createObjectBuilder().add("GLOBAL", globalErrorList);

        return createObjectBuilder().add("errors", errorMap)
            .add("globalParameters", createObjectBuilder())
            .build()
            .toString();
    }

    @Override
    public ByteBuffer badMessageError(int status, String reason, HttpFields fields)
    {
        fields.add(HttpHeader.CONTENT_TYPE, CONTENT_TYPE);
        String message = createBody(reason);
        return ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean errorPageForMethod(String method)
    {
        return true;
    }
}
