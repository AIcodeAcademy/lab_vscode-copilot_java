package com.aiddbot.archetype.cli.presenter;

import org.springframework.stereotype.Component;

import com.aiddbot.archetype.cli.integrations.openmeteo.WeatherObservation;

/**
 * Presenter that composes a concise, human‑readable weather line.
 *
 * <p>Implements feature F3.3 using a minimal contract to decouple domain retrieval (F3.2) from
 * formatting/display. Null or blank locations are rendered as a generic reference to the user's
 * location.
 */
@Component
public class WeatherPresenter {

  /**
   * Format a concise one-line weather summary.
   *
   * @param locationText optional friendly location label (e.g., "Madrid, ES")
   * @param obs current observation to render
   * @return one line string like: "City: 23.4°C, wind 3.1 m/s (code 0)"
   */
  public String present(String locationText, WeatherObservation obs) {
    String loc = (locationText == null || locationText.isBlank()) ? "your location" : locationText;
    return String.format(
        "%s: %.1f°C, wind %.1f m/s (code %d)",
        loc, obs.getTemperatureCelsius(), obs.getWindSpeed(), obs.getWeatherCode());
  }
}
