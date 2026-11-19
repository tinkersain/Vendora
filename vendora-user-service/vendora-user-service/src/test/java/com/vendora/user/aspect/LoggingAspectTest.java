package com.vendora.user.aspect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoggingAspectTest {

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Test
    void pointcutMethod_canBeCalled() {
        assertDoesNotThrow(() -> loggingAspect.applicationPackagePointcut());
    }

    @Test
    void logAround_successfulExecution() throws Throwable {
        when(signature.getDeclaringTypeName()).thenReturn("com.vendora.user.service.UserService");
        when(signature.getName()).thenReturn("getUserById");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(proceedingJoinPoint.proceed()).thenReturn("result");

        Object result = loggingAspect.logAround(proceedingJoinPoint);

        assertEquals("result", result);
        verify(proceedingJoinPoint).proceed();
    }

    @Test
    void logAround_throwsIllegalArgumentException() throws Throwable {
        when(signature.getDeclaringTypeName()).thenReturn("com.vendora.user.service.UserService");
        when(signature.getName()).thenReturn("getUserById");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{-1L});
        when(proceedingJoinPoint.proceed()).thenThrow(new IllegalArgumentException("Invalid ID"));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            loggingAspect.logAround(proceedingJoinPoint);
        });

        assertEquals("Invalid ID", thrown.getMessage());
    }

    @Test
    void logAfterThrowing_withCause() {
        when(signature.getDeclaringTypeName()).thenReturn("com.vendora.user.service.UserService");
        when(signature.getName()).thenReturn("getUserById");
        when(joinPoint.getSignature()).thenReturn(signature);
        Exception cause = new RuntimeException("Root cause");
        Exception ex = new RuntimeException("Outer error", cause);

        assertDoesNotThrow(() -> loggingAspect.logAfterThrowing(joinPoint, ex));
    }

    @Test
    void logAfterThrowing_withoutCause() {
        when(signature.getDeclaringTypeName()).thenReturn("com.vendora.user.service.UserService");
        when(signature.getName()).thenReturn("getUserById");
        when(joinPoint.getSignature()).thenReturn(signature);
        Exception ex = new RuntimeException("Error with no cause");

        assertDoesNotThrow(() -> loggingAspect.logAfterThrowing(joinPoint, ex));
    }
}
