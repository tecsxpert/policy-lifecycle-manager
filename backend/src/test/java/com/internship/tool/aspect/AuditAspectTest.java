package com.internship.tool.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.dto.PolicyResponse;
import com.internship.tool.entity.AuditLog;
import com.internship.tool.entity.Policy;
import com.internship.tool.repository.AuditRepository;
import com.internship.tool.repository.PolicyRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuditAspect to achieve better coverage.
 */
class AuditAspectTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private AuditAspect auditAspect;

@BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        auditAspect = new AuditAspect();
        
        // Use reflection to set private fields
        Field auditRepoField = AuditAspect.class.getDeclaredField("auditRepository");
        auditRepoField.setAccessible(true);
        auditRepoField.set(auditAspect, auditRepository);
        
        Field policyRepoField = AuditAspect.class.getDeclaredField("policyRepository");
        policyRepoField.setAccessible(true);
        policyRepoField.set(auditAspect, policyRepository);
        
        Field objectMapperField = AuditAspect.class.getDeclaredField("objectMapper");
        objectMapperField.setAccessible(true);
        objectMapperField.set(auditAspect, objectMapper);
        
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAuditPolicyAction_CreatePolicy() throws Throwable {
        // Set up for createPolicy method
        when(joinPoint.getSignature()).thenReturn(
            mock(org.aspectj.lang.reflect.MethodSignature.class));
        when(((org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature()).getName())
            .thenReturn("createPolicy");
        
        PolicyResponse response = new PolicyResponse();
        response.setId(1L);
        
        // Mock ResponseEntity wrapping the response
        ResponseEntity<PolicyResponse> responseEntity = ResponseEntity.ok(response);
        when(joinPoint.proceed()).thenReturn(responseEntity);
        
        // Set up object mapper to serialize
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        
        // Set up authentication
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Set up policy repository for fetch
        Policy policy = new Policy();
        policy.setId(1L);
        policy.setPolicyName("Test Policy");
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // Execute
        Object result = auditAspect.auditPolicyAction(joinPoint);

        // Verify audit was saved
        verify(auditRepository, times(1)).save(any(AuditLog.class));
        assertNotNull(result);
    }

@Test
    void testAuditPolicyAction_UpdatePolicy() throws Throwable {
        // Set up method signature mock properly
        org.aspectj.lang.reflect.MethodSignature methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(methodSignature.getName()).thenReturn("updatePolicy");
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        // Args: (Long id, PolicyRequest request)
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L, mock(Object.class)});
        
        Policy existingPolicy = new Policy();
        existingPolicy.setId(1L);
        existingPolicy.setPolicyName("Old Name");
        when(policyRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingPolicy));
        
        PolicyResponse response = new PolicyResponse();
        response.setId(1L);
        when(joinPoint.proceed()).thenReturn(ResponseEntity.ok(response));
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1}");
        
        // Set up authentication
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Policy updatedPolicy = new Policy();
        updatedPolicy.setId(1L);
        updatedPolicy.setPolicyName("New Name");
        when(policyRepository.findById(1L)).thenReturn(Optional.of(updatedPolicy));

        Object result = auditAspect.auditPolicyAction(joinPoint);

        verify(auditRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testAuditPolicyAction_SoftDeletePolicy() throws Throwable {
        // Set up method signature mock properly
        org.aspectj.lang.reflect.MethodSignature methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(methodSignature.getName()).thenReturn("softDeletePolicy");
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        
        Policy existingPolicy = new Policy();
        existingPolicy.setId(1L);
        existingPolicy.setStatus("DELETED");
        when(policyRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(existingPolicy));
        
        when(joinPoint.proceed()).thenReturn(ResponseEntity.ok().build());
        
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"id\":1,\"status\":\"DELETED\"}");
        
        // Set up authentication
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("admin");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(policyRepository.findById(1L)).thenReturn(Optional.of(existingPolicy));

        Object result = auditAspect.auditPolicyAction(joinPoint);

        verify(auditRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testAuditPolicyAction_SerializationFails() throws Throwable {
        // Set up method signature mock properly
        org.aspectj.lang.reflect.MethodSignature methodSignature = mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(methodSignature.getName()).thenReturn("createPolicy");
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        
        PolicyResponse response = new PolicyResponse();
        response.setId(1L);
        when(joinPoint.proceed()).thenReturn(ResponseEntity.ok(response));
        
        // Make serialization fail
        when(objectMapper.writeValueAsString(any()))
            .thenThrow(new RuntimeException("Serialization failed"));
        
        // Set up authentication
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Policy policy = new Policy();
        policy.setId(1L);
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        // Should still work, just with error placeholder
        Object result = auditAspect.auditPolicyAction(joinPoint);

        verify(auditRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testGetCurrentUsername_Authenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Use reflection to test private method
        String username = invokeGetCurrentUsername();
        assertEquals("testuser", username);
    }

    @Test
    void testGetCurrentUsername_NotAuthenticated() {
        // No authentication set
        String username = invokeGetCurrentUsername();
        assertEquals("anonymous", username);
    }

    @Test
    void testExtractIdFromResult_WithResponseEntity() {
        PolicyResponse response = new PolicyResponse();
        response.setId(42L);
        
        ResponseEntity<PolicyResponse> entity = ResponseEntity.ok(response);
        
        Long id = invokeExtractIdFromResult(entity);
        assertEquals(42L, id);
    }

    @Test
    void testExtractIdFromResult_NullResult() {
        Long id = invokeExtractIdFromResult(null);
        assertNull(id);
    }

    // Helper methods to access private methods via reflection
    private String invokeGetCurrentUsername() {
        try {
            var method = AuditAspect.class.getDeclaredMethod("getCurrentUsername");
            method.setAccessible(true);
            return (String) method.invoke(auditAspect);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Long invokeExtractIdFromResult(Object result) {
        try {
            var method = AuditAspect.class.getDeclaredMethod("extractIdFromResult", Object.class);
            method.setAccessible(true);
            return (Long) method.invoke(auditAspect, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
