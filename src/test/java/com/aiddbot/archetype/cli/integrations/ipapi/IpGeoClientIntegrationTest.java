package com.aiddbot.archetype.cli.integrations.ipapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.aiddbot.archetype.cli.config.CliProperties;
import com.aiddbot.archetype.cli.runtime.CodedException;
import com.aiddbot.archetype.cli.runtime.ExitCodes;

import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

/** Integration tests for {@link IpGeoClient}. */
@SpringBootTest
class IpGeoClientIntegrationTest {

  @Autowired WebClient webClient;

  private DisposableServer server;

  @BeforeEach
  void setUp() {
    server = HttpServer.create().port(0).bindNow();
  }

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.disposeNow(Duration.ofSeconds(1));
    }
  }

  private IpGeoClient newClientWithRoute(String path, String body, int statusCode) {
    // Restart server with route
    if (server != null) server.disposeNow();
    server =
        HttpServer.create()
            .port(0)
            .route(
                routes ->
                    routes.get(
                        path,
                        (req, res) ->
                            res.status(statusCode)
                                .header("Content-Type", "application/json")
                                .sendString(Mono.just(body))))
            .bindNow();

    CliProperties props = new CliProperties();
    props
        .getEndpoints()
        .setIpGeoBaseUrl(java.net.URI.create("http://localhost:" + server.port() + path));
    return new IpGeoClient(webClient, props);
  }

  @Test
  void resolve_success_returnsCoordinates() {
    String json = "{\"status\":\"success\",\"lat\":40.4,\"lon\":-3.7}";
    IpGeoClient client = newClientWithRoute("/json", json, 200);

    IpGeoResponse resp = client.resolve();

    assertThat(resp).isNotNull();
    assertThat(resp.getLat()).isEqualTo(40.4);
    assertThat(resp.getLon()).isEqualTo(-3.7);
  }

  @Test
  void resolve_failureStatus_throwsNetworkCodedException() {
    String json = "{\"status\":\"fail\",\"message\":\"bad\"}";
    IpGeoClient client = newClientWithRoute("/json", json, 200);

    assertThatThrownBy(client::resolve)
        .isInstanceOf(CodedException.class)
        .satisfies(
            ex ->
                assertThat(((CodedException) ex).getExitCode().code())
                    .isEqualTo(ExitCodes.NETWORK.code()))
        .hasMessageContaining("ip-geo failed");
  }

  @Test
  void resolve_invalidPayload_throwsUnknownCodedException() {
    String json = "{\"status\":\"success\"}"; // missing lat/lon
    IpGeoClient client = newClientWithRoute("/json", json, 200);

    assertThatThrownBy(client::resolve)
        .isInstanceOf(CodedException.class)
        .satisfies(
            ex ->
                assertThat(((CodedException) ex).getExitCode().code())
                    .isEqualTo(ExitCodes.UNKNOWN.code()))
        .hasMessageContaining("invalid coordinates");
  }

  @Test
  void resolve_httpError_throwsNetworkCodedException() {
    String json = "{\"error\":\"boom\"}";
    IpGeoClient client = newClientWithRoute("/json", json, 500);

    assertThatThrownBy(client::resolve)
        .isInstanceOf(CodedException.class)
        .satisfies(
            ex ->
                assertThat(((CodedException) ex).getExitCode().code())
                    .isEqualTo(ExitCodes.NETWORK.code()))
        .hasMessageContaining("HTTP error");
  }
}
