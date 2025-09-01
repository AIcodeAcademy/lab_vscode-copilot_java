package com.aiddbot.archetype.cli.config;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

/**
 * Provides a singleton {@link WebClient} preconfigured for outbound HTTP calls.
 *
 * <p>Behavior:
 *
 * <ul>
 *   <li>Connect timeout and read/response timeout sourced from {@link CliProperties}.
 *   <li>Default headers:
 *       <ul>
 *         <li><code>Accept: application/json</code>
 *         <li><code>User-Agent: ArchetypeJavaCLI/&lt;version&gt;</code> where version comes from
 *             Spring Boot {@link org.springframework.boot.info.BuildProperties} or falls back to
 *             <code>dev</code> in tests/local builds.
 *       </ul>
 * </ul>
 */
@Configuration
public class WebClientConfig {

  private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

  /**
   * Build the application {@link WebClient} instance.
   *
   * @param props app configuration properties providing network timeouts
   * @param buildProps optional Spring Boot build properties to resolve version for User-Agent
   * @return a configured WebClient ready for JSON APIs
   */
  @Bean
  WebClient webClient(CliProperties props, ObjectProvider<BuildProperties> buildProps) {
    int connectMs = props.getNetwork().getConnectTimeoutMs();
    int readMs = props.getNetwork().getReadTimeoutMs();
    var endpoints = props.getEndpoints();

    HttpClient httpClient =
        HttpClient.create()
            // Connect timeout
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectMs)
            // Read/response timeout
            .responseTimeout(Duration.ofMillis(readMs));

    String version = resolveVersion(buildProps);
    String userAgent = "ArchetypeJavaCLI/" + version;

    WebClient client =
        WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultHeaders(
                headers -> {
                  headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                  headers.set(HttpHeaders.USER_AGENT, userAgent);
                })
            .build();
    log.info(
        "WebClient configured: connectTimeoutMs={}, readTimeoutMs={}, userAgent={}, ipGeoBaseUrl={}, openMeteoBaseUrl={}",
        connectMs,
        readMs,
        userAgent,
        endpoints.getIpGeoBaseUrl(),
        endpoints.getOpenMeteoBaseUrl());

    return client;
  }

  /**
   * Resolve the application version for the User-Agent header.
   *
   * <p>If {@link BuildProperties} are available (when built with spring-boot-maven-plugin {@code
   * build-info}), use that version; otherwise return {@code dev}.
   */
  private static String resolveVersion(ObjectProvider<BuildProperties> buildProps) {
    BuildProperties bp = buildProps.getIfAvailable();
    if (bp != null) {
      return bp.getVersion();
    }
    return "dev";
  }
}
