package com.aiddbot.archetype.cli.integrations.ipapi;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.aiddbot.archetype.cli.config.CliProperties;
import com.aiddbot.archetype.cli.runtime.CodedException;
import com.aiddbot.archetype.cli.runtime.ExitCodes;

import reactor.core.Exceptions;

/**
 * Client for the IP geolocation service (ip-api.com).
 *
 * <p>This component belongs to feature F3.1 "Resolve location via IP" and uses the shared,
 * preconfigured {@link WebClient} to call the external endpoint provided by {@link
 * CliProperties#getEndpoints()}.
 *
 * <p>All network and upstream errors are translated to a {@link CodedException} using {@link
 * ExitCodes#NETWORK} so the CLI can exit gracefully with consistent codes (F3.4).
 */
@Component
public class IpGeoClient {

  private static final Logger log = LoggerFactory.getLogger(IpGeoClient.class);

  private final WebClient webClient;
  private final URI baseUri;

  /**
   * Create the client with the shared WebClient and configuration properties.
   *
   * @param webClient shared, timeout-configured HTTP client (see F2.1)
   * @param props CLI properties holding the ip-api base URL (see F2.2)
   */
  public IpGeoClient(WebClient webClient, CliProperties props) {
    this.webClient = webClient;
    this.baseUri = props.getEndpoints().getIpGeoBaseUrl();
  }

  /**
   * Resolve the current public IP location into approximate coordinates.
   *
   * @return parsed {@link IpGeoResponse} with latitude and longitude
   * @throws CodedException when HTTP/network errors occur or the response is invalid
   */
  public IpGeoResponse resolve() {
    try {
      IpGeoResponse resp =
          webClient.get().uri(baseUri).retrieve().bodyToMono(IpGeoResponse.class).block();

      if (resp == null) {
        throw new CodedException(ExitCodes.UNKNOWN, "ip-geo returned empty response");
      }

      if (!"success".equalsIgnoreCase(resp.getStatus())) {
        String msg = resp.getMessage() != null ? resp.getMessage() : "ip-geo returned failure";
        throw new CodedException(ExitCodes.NETWORK, "ip-geo failed: " + msg);
      }

      if (resp.getLat() == null || resp.getLon() == null) {
        throw new CodedException(ExitCodes.UNKNOWN, "ip-geo returned invalid coordinates");
      }

      return resp;
    } catch (WebClientResponseException wcre) {
      log.error("ip-geo HTTP error: {}", wcre.getMessage());
      throw new CodedException(ExitCodes.NETWORK, "ip-geo HTTP error: " + wcre.getMessage(), wcre);
    } catch (RuntimeException re) {
      // treat reactor / timeout / other network issues as network failures
      log.error("ip-geo network/runtime error: {}", re.toString());
      Throwable unwrapped = Exceptions.unwrap(re);
      throw new CodedException(
          ExitCodes.NETWORK, "ip-geo network error: " + unwrapped.getMessage(), unwrapped);
    }
  }
}
