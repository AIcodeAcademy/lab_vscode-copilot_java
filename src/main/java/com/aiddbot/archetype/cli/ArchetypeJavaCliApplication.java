package com.aiddbot.archetype.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.aiddbot.archetype.cli.runtime.DefaultExitCodeExceptionMapper;
import com.aiddbot.archetype.cli.runtime.ExitCodeExceptionMapper;

/**
 * Spring Boot application entrypoint for the Archetype Java CLI.
 *
 * <p>Provides a minimal CLI-ready bootstrap with a startup log line including application name and
 * version.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ArchetypeJavaCliApplication {

  private static final Logger log = LoggerFactory.getLogger(ArchetypeJavaCliApplication.class);

  /**
   * Application entrypoint for the Archetype Java CLI.
   *
   * <p>Boots a Spring Boot application configured for CLI usage (non-web) and prints a simple
   * banner to the console.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(ArchetypeJavaCliApplication.class);
    app.setBannerMode(Banner.Mode.CONSOLE);
    ExitCodeExceptionMapper mapper = new DefaultExitCodeExceptionMapper();
    try {
      app.run(args);
      log.info("=== APPLICATION SHUTDOWN ===");
    } catch (Throwable t) {
      int code = mapper.map(t);
      log.error("Fatal error: {} (code={})", mapper.message(t), code, t);
      System.exit(code);
    }
  }

  /**
   * Logs a concise startup line with application name and version once the Spring context has
   * initialized.
   *
   * <p>Version is resolved from {@link BuildProperties} when available (packaged builds), otherwise
   * falls back to the optional property {@code app.version} or {@code dev}.
   *
   * @param env Spring {@link Environment} to resolve config properties
   * @param buildProps lazy provider for build metadata
   * @return a runner executed at startup
   */
  @Bean
  CommandLineRunner logStartup(Environment env, ObjectProvider<BuildProperties> buildProps) {
    return args -> {
      log.info("=== APPLICATION STARTUP BEGINS ===");

      String appName = env.getProperty("spring.application.name", "archetype-java-cli");
      BuildProperties bp = buildProps.getIfAvailable();
      String version = resolveVersion(env, bp);

      // Log application configuration
      log.info("Application Name: {}", appName);
      log.info("Application Version: {}", version);
      log.info(
          "Web Application Type: {}",
          env.getProperty("spring.main.web-application-type", "servlet"));
      log.info(
          "Shell Interactive Enabled: {}",
          env.getProperty("spring.shell.interactive.enabled", "true"));
      log.info(
          "Shell Non-Interactive Enabled: {}",
          env.getProperty("spring.shell.noninteractive.enabled", "true"));
      log.info("Active Profiles: {}", String.join(", ", env.getActiveProfiles()));

      log.info("=== {} v{} READY ===", appName, version);
      log.info("=== APPLICATION STARTUP COMPLETE ===");
    };
  }

  @Bean
  ExitCodeExceptionMapper exitCodeExceptionMapper() {
    return new DefaultExitCodeExceptionMapper();
  }

  private static String resolveVersion(Environment env, BuildProperties bp) {
    if (bp != null) {
      return bp.getVersion();
    }

    String configured = env.getProperty("app.version");
    if (configured != null && !configured.isBlank()) {
      return configured;
    }

    return "dev";
  }
}
