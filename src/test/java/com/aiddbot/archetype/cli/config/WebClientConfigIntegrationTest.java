package com.aiddbot.archetype.cli.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

/** Integration tests for {@link WebClientConfig} bean behavior. */
@SpringBootTest
class WebClientConfigIntegrationTest {

  @Autowired WebClient webClient;

  private DisposableServer server;

  @BeforeEach
  void setUp() {
    // Start a lightweight HTTP server that echoes selected request headers as JSON
    server =
        HttpServer.create()
            .port(0)
            .route(
                routes ->
                    routes.get(
                        "/echo",
                        (req, res) -> {
                          String ua = req.requestHeaders().get("User-Agent");
                          String accept = req.requestHeaders().get("Accept");
                          String body =
                              "{\"userAgent\":\""
                                  + (ua == null ? "" : ua)
                                  + "\",\"accept\":\""
                                  + (accept == null ? "" : accept)
                                  + "\"}";
                          return res.header("Content-Type", "application/json")
                              .sendString(Mono.just(body));
                        }))
            .bindNow();
  }

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.disposeNow();
    }
  }

  @Test
  void webClient_setsDefaultHeaders_userAgentAndAccept() {
    // When
    String responseBody =
        webClient
            .get()
            .uri("http://localhost:" + server.port() + "/echo")
            .retrieve()
            .bodyToMono(String.class)
            .block(Duration.ofSeconds(3));

    // Then
    assertThat(responseBody).isNotNull();
    assertThat(responseBody).contains("\"accept\":\"application/json\"");
    assertThat(responseBody).contains("\"userAgent\":\"ArchetypeJavaCLI/");
  }

  @Test
  void webClient_enforcesReadTimeout_whenServerDelays() {
    // Given a handler that delays sending the response beyond the configured read
    // timeout
    server.disposeNow();
    server =
        HttpServer.create()
            .port(0)
            .route(
                routes ->
                    routes.get(
                        "/slow",
                        (req, res) ->
                            // Delay beyond the default read timeout (2000 ms)
                            res.sendString(
                                Mono.just("OK").delaySubscription(Duration.ofMillis(3000)))))
            .bindNow();

    // When / Then
    assertThatThrownBy(
            () ->
                webClient
                    .get()
                    .uri("http://localhost:" + server.port() + "/slow")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block())
        .isInstanceOf(Exception.class);
  }
}
