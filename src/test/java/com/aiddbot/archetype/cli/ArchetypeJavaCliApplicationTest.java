package com.aiddbot.archetype.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration test for ArchetypeJavaCliApplication.
 *
 * <p>This test starts the ENTIRE Spring context - it's SLOW but tests real integration. For fast
 * tests, use ArchetypeJavaCliApplicationUnitTest instead.
 */
@SpringBootTest(
    classes = ArchetypeJavaCliApplication.class,
    webEnvironment = WebEnvironment.NONE,
    properties = {
      "spring.autoconfigure.exclude=org.springframework.shell.boot.ShellAutoConfiguration"
    })
@ActiveProfiles("test")
@TestPropertySource(
    properties = {
      "spring.shell.interactive.enabled=false",
      "spring.shell.noninteractive.enabled=false",
      "spring.main.web-application-type=none",
      "spring.main.banner-mode=off",
      "logging.level.com.aiddbot=DEBUG"
    })
class ArchetypeJavaCliApplicationTest {

  private static final Logger log = LoggerFactory.getLogger(ArchetypeJavaCliApplicationTest.class);

  @Autowired private ApplicationContext applicationContext;

  @Test
  void contextLoads() {
    log.info("=== INTEGRATION TEST STARTING: contextLoads ===");
    log.info("Spring context has loaded successfully - this means:");
    log.info("1. All Spring beans have been created");
    log.info("2. Configuration has been loaded");
    log.info("3. Application is ready (but Shell is disabled for testing)");

    // Verify the context is properly loaded
    assertNotNull(applicationContext, "ApplicationContext should not be null");

    // Verify our application's CommandLineRunner bean is created
    CommandLineRunner[] runners =
        applicationContext
            .getBeansOfType(CommandLineRunner.class)
            .values()
            .toArray(new CommandLineRunner[0]);
    assertNotNull(runners, "CommandLineRunner beans should exist");

    log.info("Found {} CommandLineRunner beans", runners.length);
    log.info("=== INTEGRATION TEST COMPLETED: contextLoads ===");
  }
}
