package com.aiddbot.archetype.cli.integrations.openmeteo;

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
 * Client for Open‑Meteo current weather endpoint.
 *
 * <p>This component implements feature F3.2 "Fetch current weather from Open‑Meteo". It uses the
 * shared {@link WebClient} and the base URL from {@link CliProperties} (F2.2). Errors are
 * normalized to {@link CodedException} with {@link ExitCodes#NETWORK} for consistent CLI behavior
 * (F3.4).
 */
@Component
public class OpenMeteoClient {

  private static final Logger log = LoggerFactory.getLogger(OpenMeteoClient.class);

  private final WebClient webClient;
  private final URI baseUri;

  /**
   * Constructs a client using the shared WebClient and configuration.
   *
   * @param webClient shared WebClient with timeouts (F2.1)
   * @param props CLI properties containing Open‑Meteo base URL (F2.2)
   */
  public OpenMeteoClient(WebClient webClient, CliProperties props) {
    this.webClient = webClient;
    this.baseUri = props.getEndpoints().getOpenMeteoBaseUrl();
  }

  /**
   * Fetch current weather for the given coordinates.
   *
   * @param lat latitude in decimal degrees
   * @param lon longitude in decimal degrees
   * @return {@link WeatherObservation} with temperature, windspeed and code
   * @throws CodedException when HTTP/network errors occur or the payload is incomplete
   */
  public WeatherObservation fetchCurrent(double lat, double lon) {
    try {
      // Build URI like: {base}?latitude={lat}&longitude={lon}&current_weather=true
      String uri =
          String.format(
              "%s?latitude=%s&longitude=%s&current_weather=true", baseUri.toString(), lat, lon);

      var wrapper =
          webClient.get().uri(uri).retrieve().bodyToMono(OpenMeteoResponseWrapper.class).block();

      if (wrapper == null || wrapper.current_weather == null) {
        throw new CodedException(ExitCodes.UNKNOWN, "open-meteo returned empty current_weather");
      }

      OpenMeteoCurrent cur = wrapper.current_weather;
      if (cur.getTemperature() == null
          || cur.getWindspeed() == null
          || cur.getWeathercode() == null) {
        throw new CodedException(
            ExitCodes.UNKNOWN, "open-meteo returned incomplete current_weather");
      }

      return new WeatherObservation(cur.getTemperature(), cur.getWindspeed(), cur.getWeathercode());
    } catch (WebClientResponseException wcre) {
      log.error("open-meteo HTTP error: {}", wcre.getMessage());
      throw new CodedException(
          ExitCodes.NETWORK, "open-meteo HTTP error: " + wcre.getMessage(), wcre);
    } catch (RuntimeException re) {
      log.error("open-meteo network/runtime error: {}", re.toString());
      Throwable unwrapped = Exceptions.unwrap(re);
      throw new CodedException(
          ExitCodes.NETWORK, "open-meteo network error: " + unwrapped.getMessage(), unwrapped);
    }
  }

  // Internal wrapper to map root JSON containing current_weather
  private static class OpenMeteoResponseWrapper {
    public OpenMeteoCurrent current_weather;
  }
}
