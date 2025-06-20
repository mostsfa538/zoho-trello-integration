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
    
    /**
     * Pointcut that matches all methods in the service layer of the application.
     * This includes all methods in classes within the package com.integration.zoho_trello_integration.services.
     */
    @Pointcut("execution(* com.integration.zoho_trello_integration.services..*(..))")
    void serviceLayerExecution() {}

    /**
     * Advice that runs before the execution of any method matched by the serviceLayerExecution pointcut.
     * Logs the method name being entered.
     */
    @Before("serviceLayerExecution()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {}", joinPoint.getSignature().getName());
    }

    /**
     * Advice that runs after the execution of any method matched by the serviceLayerExecution pointcut.
     * Logs the method name being exited.
     */
    @After("serviceLayerExecution()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature().getName());
    }

    /**
     * Advice that runs around the execution of any method matched by the serviceLayerExecution pointcut.
     * Logs the method name before and after execution, and returns the result of the method.
     *
     * @param proceedingJoinPoint the join point representing the method execution
     * @return the result of the method execution
     * @throws Throwable if any exception occurs during method execution
     */
    @Around("serviceLayerExecution()")
    public Object logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("Before method: {}", proceedingJoinPoint.getSignature().getName());
        Object result = proceedingJoinPoint.proceed();
        log.info("After method: {}", proceedingJoinPoint.getSignature().getName());
        return result;
    }

}
