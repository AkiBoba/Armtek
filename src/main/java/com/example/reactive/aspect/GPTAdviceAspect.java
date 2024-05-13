package com.example.reactive.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Aspect
@Component
public class GPTAdviceAspect {
    private static Map<String, Integer> aspectMapResult = new HashMap<>();
    AtomicInteger countOfParser = new AtomicInteger(0);
    AtomicInteger countOfGoodsLinks = new AtomicInteger(0);

    @AfterReturning(value = "com.example.reactive.aspect.LoggingAspect.isGPTMethod()", returning = "result")
    public void loggingReturningGetMethods(JoinPoint joinPoint, Object result) {
       aspectMapResult.put(Arrays.toString(joinPoint.getArgs()), (Integer) result);
       log.info("aspectMapResult {} & {}", joinPoint.getArgs(), result);
       log.info("aspectMapResult Size = {}", aspectMapResult.size());
       log.info("countOfParser = {}", countOfParser.incrementAndGet());
    }

    @AfterReturning(value = "com.example.reactive.aspect.LoggingAspect.isGetAllGoodsLinksMethod()", returning = "result")
    public void loggingReturningGetGoodLinksMethods(JoinPoint joinPoint, Object result) {
       aspectMapResult.put(Arrays.toString(joinPoint.getArgs()), (Integer) result);
       log.info("aspectMapResult {} & {}", joinPoint.getArgs(), result);
       log.info("aspectMapResult Size = {}", aspectMapResult.size());
       log.info("countOfParsed = {}", countOfGoodsLinks.incrementAndGet());
    }

}
