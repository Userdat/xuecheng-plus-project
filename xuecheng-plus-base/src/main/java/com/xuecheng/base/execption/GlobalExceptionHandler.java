package com.xuecheng.base.execption;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuenchen
 * @description TODO
 * @date 2024-04-30 14:58
 */
@Slf4j
@ControllerAdvice
//@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e){
        //记录日志
        log.error("系统异常{}",e.getErrMessage(),e);
        return new RestErrorResponse(e.getErrMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException (MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<String> msgList = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item->{
            msgList.add(item.getDefaultMessage());
        });
        //拼接错误信息
        String msg = StringUtils.join(msgList, ",");

        //记录日志
        log.error("系统异常{}",msg);
        return new RestErrorResponse(msg);
    }
}
