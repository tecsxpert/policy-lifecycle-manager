# Test Fix Tracking

## Steps
- [ ] 1. Update JaCoCo to 0.8.12 in pom.xml
- [ ] 2. Fix V3__roles.sql H2 compatibility
- [ ] 3. Add @ActiveProfiles("test") to PolicyRepositoryTest
- [ ] 4. Add @ActiveProfiles("test") to SecurityAuditTest
- [ ] 5. Add @WithMockUser to GlobalExceptionHandlerTest
- [ ] 6. Fix PolicySchedulerServiceTest Mockito stubs
- [ ] 7. Fix InputSanitizerTest expectation
- [ ] 8. Skip FullSystemIntegrationTest when Docker unavailable
- [ ] 9. Run mvn clean compile
- [ ] 10. Run mvn clean test
- [ ] 11. Verify coverage ≥ 80%

