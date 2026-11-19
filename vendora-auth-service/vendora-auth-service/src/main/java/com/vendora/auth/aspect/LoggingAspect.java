package com.vendora.auth.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AOP Aspect for centralized logging across Auth Service controller and service layers,
 * tracking authentication entry, execution duration, and exceptions with SLF4J.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.vendora.auth.controller..*) || within(com.vendora.auth.service..*)")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        if (logger.isDebugEnabled()) {
            logger.debug("[ENTER] {}.{}() with argument[s] = {}", className, methodName, Arrays.toString(args));
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;

            logger.info("[SUCCESS] {}.{}() executed in {} ms", className, methodName, elapsedTime);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("[ILLEGAL ARG] Illegal argument in {}.{}(): {}", className, methodName, Arrays.toString(args));
            throw e;
        }
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        logger.error("[EXCEPTION] Exception in {}.{}() with cause = '{}' and message = '{}'",
                className, methodName, e.getCause() != null ? e.getCause() : "NULL", e.getMessage());
    }
}
