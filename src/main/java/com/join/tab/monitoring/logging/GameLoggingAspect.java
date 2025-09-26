package com.join.tab.monitoring.logging;

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
 * Aspect for logging execution of service methods and exceptions in controller
 * methods.
 * <p>
 *     - Logs entry, exit and execution time of service layer methods.
 *     - Logs exceptions throw by controller layer methods.
 * </p>
 */
@Aspect
@Component
public class GameLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(GameLoggingAspect.class);

    /**
     * Pointcut for all methods in the service layer.
     */
    @Pointcut("execution(* com.join.tab.application.service..*(..))")
    public void serviceLayer() {}

    /**
     * Pointcut for all methods in the controller layer.
     */
    @Pointcut("execution(* com.join.tab.controller..*(..))")
    public void controllerLayer() {}

    /**
     * Logs execution of service methods including method name, class, and
     * execution time.
     * Also logs exception if the service method fails.
     *
     * @param joinPoint the join point providing access to the method being executed
     * @return the result of the method execution
     * @throws Throwable if the underlying method throws an exception
     */
    @Around("serviceLayer()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("Executing service method: {}.{}", className, methodName);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("Service method {}.{} completed in {} ms", className, methodName, executionTime);
            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("Service method {}.{} failed after {} ms: {}",
                    className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    /**
     * Logs exception throws be controller layer methods.
     *
     * @param joinPoint the join point providing access to the method that threw
     *                  the exception
     * @param exception the exception thrown by the method
     */
    @AfterThrowing(pointcut = "controllerLayer()", throwing = "exception")
    public void logControllerException(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.error("Controller {}.{} threw exception: {}",
                className, methodName, exception.getMessage(), exception);
    }

}
