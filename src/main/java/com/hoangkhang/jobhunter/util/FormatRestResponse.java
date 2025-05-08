package com.hoangkhang.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.hoangkhang.jobhunter.domain.RestResponse;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int statusCode = servletResponse.getStatus();

        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(statusCode);

        if (body instanceof String) {
            return body;
        }

        if (statusCode >= 400) {
            return body;
        } else {
            ApiMessage apiMessage = returnType.getMethodAnnotation(ApiMessage.class);
            String message = apiMessage != null ? apiMessage.value() : "CALL API SUCCESS";
            restResponse.setMessage(message);
            restResponse.setData(body);
        }

        return restResponse;
    }

}
