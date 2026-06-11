package com.martinsdev.solicitation.api.infra.aop;

import com.martinsdev.solicitation.api.model.AuditLog;
import com.martinsdev.solicitation.api.model.User;
import com.martinsdev.solicitation.api.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(audit)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {

        //Dados necessários
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String action = audit.action().isBlank() ? methodName : audit.action();

        //Objeto autenticado
        String userEmail = "anonymous";
        String userRole = "anonymous";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            userEmail = user.getEmail();
            userRole = user.getRole().name();
        }

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.info("[AUDIT] action={} user={} role={} duration={}ms status=SUCCESS", action, userEmail,userRole, duration);

            auditLogRepository.save(AuditLog.builder()
                            .action(action)
                            .userEmail(userEmail)
                            .userRole(userRole)
                            .durationMs(duration)
                            .success(true)
                            .createdAt(LocalDateTime.now())
                    .build());
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - start;
            log.error("[AUDIT] action={} user={} role={} duration={}ms status=ERROR", action, userEmail, userRole, duration, e.getMessage());

            auditLogRepository.save(AuditLog.builder()
                    .action(action)
                    .userEmail(userEmail)
                    .userRole(userRole)
                    .durationMs(duration)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build());
            throw e;
        }
    }
}
