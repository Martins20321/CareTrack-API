package com.martinsdev.solicitation.api.infra.aop;

import com.martinsdev.solicitation.api.model.User;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AuditAspect {

    @Around("@annotation(audit)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {

        //Dados necessários
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String action = audit.action().isBlank() ? methodName : audit.action();

        //Objeto autenticado
        String userInfo = "anonymous";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            userInfo = user.getEmail() + " [" + user.getRole() + "]";
        }

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[AUDIT] action={} user={} duration={}ms status=SUCCESS", action, userInfo, duration);
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - start;
            log.info("[AUDIT] action={} user={} duration={}ms status=ERROR", action, userInfo, duration, e.getMessage());
            throw e;
        }
    }
}
