package com.integration.zoho_trello_integration.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.JoinPoint;

@Aspect
@Component
public class LoggingAspect {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.integration.zoho_trello_integration.services..*(..))")
    void serviceLayerExecution() {}

    @Before("serviceLayerExecution()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {}", joinPoint.getSignature().getName());
    }

    @After("serviceLayerExecution()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature().getName());
    }

    @Around("serviceLayerExecution()")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Before method: {}", proceedingJoinPoint.getSignature().getName());
        Object result = proceedingJoinPoint.proceed();
        log.info("After method: {}", proceedingJoinPoint.getSignature().getName());
        return result;
    }

}
