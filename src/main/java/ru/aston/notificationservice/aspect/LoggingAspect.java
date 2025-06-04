package ru.aston.notificationservice.aspect;

import brave.Tracer;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

  @Around(value = "@within(ru.aston.notificationservice.aspect.Loggable) || @annotation(ru.aston.notificationservice.aspect.Loggable)")
  public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    String args = Arrays.toString(joinPoint.getArgs());
    log.info("Calling method: " + methodName + " with arguments: " + args);
    try {
      Object result = joinPoint.proceed();
      log.info(
          "Method " + methodName + " executed successfully." + ". Result: " + result.toString());
      return result;
    } catch (Throwable e) {
      log.error("Error in method " + methodName + ": " + e.getMessage());
      throw e;
    }
  }
}
