package com.aiddbot.archetype.cli.presenter;

import org.springframework.stereotype.Component;

import com.aiddbot.archetype.cli.integrations.openmeteo.WeatherObservation;

/**
 * Presenter that composes a concise, human‑readable weather line.
 *
 * <p>
 * Implements feature F3.3 using a minimal contract to decouple domain retrieval
 * (F3.2) from
 * formatting/display. Null or blank locations are rendered as a generic
 * reference to the user's
 * location.
 */
@Component
public final class WeatherPresenter {

  /**
   * Format a concise one-line weather summary.
   *
   * @param locationText optional friendly location label (e.g., "Madrid, ES")
   * @param obs          current observation to render
   * @return one line string like: "City: 23.4°C, wind 3.1 m/s (code 0)"
   */
  public String present(String locationText, WeatherObservation obs) {
    String loc = (locationText == null || locationText.isBlank()) ? "your location" : locationText;
    if (obs == null) {
      return String.format("%s: no weather observation available", loc);
    }

    double temp = obs.getTemperatureCelsius();
    double wind = obs.getWindSpeed();
    int code = obs.getWeatherCode();

    // One data item per line for easier human reading
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%s:%n", loc));
    sb.append(String.format(java.util.Locale.US, "  Temperature: %.1f C%n", temp));
    sb.append(String.format(java.util.Locale.US, "  Wind: %.1f m/s%n", wind));
    // Condition: textual only, do not include numeric code
    String condition = descriptionForCode(code);
    sb.append(String.format("  Condition: %s%n", condition));

    return sb.toString();
  }

  // Map WMO weather codes to short, human-readable descriptions.
  private static String descriptionForCode(int code) {
    if (code == 0)
      return "Clear sky";
    if (code >= 1 && code <= 3)
      return "Mainly clear / partly cloudy";
    if (code >= 45 && code <= 48)
      return "Fog / Depositing rime";
    if ((code >= 51 && code <= 55) || (code >= 61 && code <= 65) || (code >= 80 && code <= 82))
      return "Drizzle / Rain";
    if ((code >= 56 && code <= 67))
      return "Freezing rain / Sleet";
    if ((code >= 71 && code <= 77) || (code >= 85 && code <= 86))
      return "Snow / Snow showers";
    if (code >= 95 && code <= 99)
      return "Thunderstorm";
    return "Unknown";
  }
}
