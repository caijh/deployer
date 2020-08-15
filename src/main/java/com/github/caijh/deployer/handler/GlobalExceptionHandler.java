package com.github.caijh.deployer.handler;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.github.caijh.commons.base.exception.BizRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizRuntimeException.class)
    @ResponseBody
    public ResponseEntity<String> handleRancherException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        BizRuntimeException exception = (BizRuntimeException) ex;
        JSONObject message = new JSONObject()
            .fluentPut("message", Optional.ofNullable(exception.getMessage()).orElseGet(() -> exception.getCause().toString()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message.toJSONString());
    }

}
