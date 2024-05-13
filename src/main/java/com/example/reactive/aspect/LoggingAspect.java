package com.example.reactive.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class LoggingAspect {

    @Pointcut("within(com.example.reactive.utils.ArmtekParserErrorsUtil)")
    public void isArmtekParserErrorsUtilLayer() {

    }

    @Pointcut("execution(public int getAllPaginationLinksGPTV1(..))")
    public void isGPTMethod() {

    }

    @Pointcut("within(com.example.reactive.utils.GoodsLinksUtil) && execution(public int getAllGoodsLinks(..))")
    public void isGetAllGoodsLinksMethod() {

    }

}
