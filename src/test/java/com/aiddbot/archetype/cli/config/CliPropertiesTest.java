package com.aiddbot.archetype.cli.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

class CliPropertiesTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(TestConfig.class);

  @Configuration
  @EnableConfigurationProperties(CliProperties.class)
  static class TestConfig {}

  @Test
  void defaults_areApplied_whenNoOverrides() {
    contextRunner.run(
        context -> {
          CliProperties props = context.getBean(CliProperties.class);
          assertThat(props.getNetwork().getConnectTimeoutMs()).isEqualTo(2000);
          assertThat(props.getNetwork().getReadTimeoutMs()).isEqualTo(2000);
          assertThat(props.getEndpoints().getIpGeoBaseUrl().toString())
              .isEqualTo("http://ip-api.com/json");
          assertThat(props.getEndpoints().getOpenMeteoBaseUrl().toString())
              .isEqualTo("https://api.open-meteo.com/v1/forecast");
        });
  }

  @Test
  void overrides_bindFromProperties() {
    contextRunner
        .withPropertyValues(
            "cli.network.connect-timeout-ms=5000",
            "cli.network.read-timeout-ms=7000",
            "cli.endpoints.ip-geo-base-url=https://example.com/ip",
            "cli.endpoints.open-meteo-base-url=https://example.com/weather")
        .run(
            context -> {
              CliProperties props = context.getBean(CliProperties.class);
              assertThat(props.getNetwork().getConnectTimeoutMs()).isEqualTo(5000);
              assertThat(props.getNetwork().getReadTimeoutMs()).isEqualTo(7000);
              assertThat(props.getEndpoints().getIpGeoBaseUrl().toString())
                  .isEqualTo("https://example.com/ip");
              assertThat(props.getEndpoints().getOpenMeteoBaseUrl().toString())
                  .isEqualTo("https://example.com/weather");
            });
  }

  @Nested
  class Validation {
    @Test
    void negativeTimeouts_failValidation() {
      contextRunner
          .withPropertyValues("cli.network.connect-timeout-ms=0", "cli.network.read-timeout-ms=-1")
          .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void invalidEndpointUri_failValidation() {
      contextRunner
          .withPropertyValues(
              "cli.endpoints.ip-geo-base-url=:://bad-uri",
              "cli.endpoints.open-meteo-base-url=htt p://bad")
          .run(context -> assertThat(context).hasFailed());
    }
  }
}
