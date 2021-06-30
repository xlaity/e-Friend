package com.tanhua.server.exception;

import com.tanhua.domain.vo.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    /**
     * 工程中抛出异常，最终都会调用该方法统一返回结果
     * @ExceptionHandler
     * value : 指定当前异常处理器处理的异常类型
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handlerException(Exception e) {
        log.error("系统发生异常：", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResult.error());
    }
}
