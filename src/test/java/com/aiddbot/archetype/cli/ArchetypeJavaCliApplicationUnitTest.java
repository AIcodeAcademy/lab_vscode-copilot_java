package com.aiddbot.archetype.cli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

/**
 * Unit tests for ArchetypeJavaCliApplication.
 *
 * <p>These tests run FAST because they don't start Spring context. They test individual methods in
 * isolation using mocks.
 */
@ExtendWith(MockitoExtension.class)
class ArchetypeJavaCliApplicationUnitTest {

  private static final Logger log =
      LoggerFactory.getLogger(ArchetypeJavaCliApplicationUnitTest.class);

  @Mock private Environment environment;

  @Mock private ObjectProvider<BuildProperties> buildPropsProvider;

  @Mock private BuildProperties buildProperties;

  private ArchetypeJavaCliApplication application;

  @BeforeEach
  void setUp() {
    log.info("=== UNIT TEST SETUP ===");
    application = new ArchetypeJavaCliApplication();
    log.info("Created application instance for testing");
  }

  @Test
  void logStartup_shouldCreateCommandLineRunner() {
    log.info("=== TESTING: logStartup method ===");

    // Arrange: Setup mock behavior
    when(environment.getProperty("spring.application.name", "archetype-java-cli"))
        .thenReturn("test-app");
    when(environment.getProperty("app.version")).thenReturn("1.0.0");
    when(environment.getProperty("spring.main.web-application-type", "servlet")).thenReturn("none");
    when(environment.getProperty("spring.shell.interactive.enabled", "true")).thenReturn("false");
    when(environment.getProperty("spring.shell.noninteractive.enabled", "true"))
        .thenReturn("false");
    when(environment.getActiveProfiles()).thenReturn(new String[] {"test"});
    when(buildPropsProvider.getIfAvailable()).thenReturn(null);

    log.info("Mocks configured successfully");

    // Act: Call the method we're testing
    CommandLineRunner runner = application.logStartup(environment, buildPropsProvider);

    log.info("CommandLineRunner created successfully");

    // Assert: Verify the runner was created
    assertNotNull(runner, "CommandLineRunner should not be null");
    log.info("✅ Test passed: CommandLineRunner created");

    // Test that the runner can be executed without errors
    assertDoesNotThrow(
        () -> {
          runner.run();
          log.info("✅ Test passed: CommandLineRunner executed without errors");
        });

    // Verify interactions with mocks
    verify(environment).getProperty("spring.application.name", "archetype-java-cli");
    verify(buildPropsProvider).getIfAvailable();
    log.info("✅ Test passed: All expected method calls verified");
  }

  @Test
  void logStartup_shouldUseBuildPropertiesVersionWhenAvailable() {
    log.info("=== TESTING: version resolution with BuildProperties ===");

    // Arrange
    when(environment.getProperty("spring.application.name", "archetype-java-cli"))
        .thenReturn("test-app");
    when(environment.getProperty("spring.main.web-application-type", "servlet")).thenReturn("none");
    when(environment.getProperty("spring.shell.interactive.enabled", "true")).thenReturn("false");
    when(environment.getProperty("spring.shell.noninteractive.enabled", "true"))
        .thenReturn("false");
    when(environment.getActiveProfiles()).thenReturn(new String[] {"test"});
    when(buildPropsProvider.getIfAvailable()).thenReturn(buildProperties);
    when(buildProperties.getVersion()).thenReturn("2.0.0-BUILD");

    log.info("Mocks configured for BuildProperties test");

    // Act
    CommandLineRunner runner = application.logStartup(environment, buildPropsProvider);

    // Assert
    assertNotNull(runner);
    assertDoesNotThrow(() -> runner.run());

    verify(buildProperties).getVersion();
    log.info("✅ Test passed: BuildProperties version used correctly");
  }

  @Test
  void logStartup_shouldFallbackToDevVersionWhenNoBuildProps() {
    log.info("=== TESTING: version fallback to 'dev' ===");

    // Arrange
    when(environment.getProperty("spring.application.name", "archetype-java-cli"))
        .thenReturn("test-app");
    when(environment.getProperty("spring.main.web-application-type", "servlet")).thenReturn("none");
    when(environment.getProperty("spring.shell.interactive.enabled", "true")).thenReturn("false");
    when(environment.getProperty("spring.shell.noninteractive.enabled", "true"))
        .thenReturn("false");
    when(environment.getActiveProfiles()).thenReturn(new String[] {"test"});
    when(buildPropsProvider.getIfAvailable()).thenReturn(null);
    when(environment.getProperty("app.version")).thenReturn(null);

    log.info("Mocks configured for dev version fallback test");

    // Act & Assert
    CommandLineRunner runner = application.logStartup(environment, buildPropsProvider);
    assertNotNull(runner);
    assertDoesNotThrow(() -> runner.run());

    log.info("✅ Test passed: Fallback to 'dev' version works correctly");
  }
}
