package com.aiddbot.archetype.cli.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.aiddbot.archetype.cli.errors.UserFacingErrors;
import com.aiddbot.archetype.cli.integrations.ipapi.IpGeoClient;
import com.aiddbot.archetype.cli.integrations.openmeteo.OpenMeteoClient;
import com.aiddbot.archetype.cli.integrations.openmeteo.WeatherObservation;
import com.aiddbot.archetype.cli.presenter.WeatherPresenter;
import com.aiddbot.archetype.cli.runtime.CodedException;

/**
 * Spring Shell command group exposing the {@code weather} command.
 *
 * <p>
 * Orchestrates Epic 3 features:
 *
 * <ul>
 * <li>F3.1 – Resolve approximate location via IP when coordinates are not
 * provided.
 * <li>F3.2 – Fetch current weather from Open‑Meteo given lat/lon.
 * <li>F3.3 – Present a concise, human‑readable summary.
 * <li>F3.4 – Propagate coded errors for graceful exit codes.
 * </ul>
 */
@ShellComponent
public class WeatherCommands {

  private final IpGeoClient ipGeoClient;
  private final OpenMeteoClient openMeteoClient;
  private final WeatherPresenter presenter;

  public WeatherCommands(
      IpGeoClient ipGeoClient, OpenMeteoClient openMeteoClient, WeatherPresenter presenter) {
    this.ipGeoClient = ipGeoClient;
    this.openMeteoClient = openMeteoClient;
    this.presenter = presenter;
  }

  /**
   * Show current weather for explicit coordinates or approximate IP location.
   *
   * @param lat optional latitude in decimal degrees
   * @param lon optional longitude in decimal degrees
   * @return formatted one-line summary
   */
  @ShellMethod(key = "weather", value = "Show current weather for coordinates or detected IP location")
  public String weather(
      @ShellOption(help = "latitude", defaultValue = ShellOption.NULL) Double lat,
      @ShellOption(help = "longitude", defaultValue = ShellOption.NULL) Double lon) {

    double useLat = 0.0;
    double useLon = 0.0;
    String locationText = null;

    if (lat == null || lon == null) {
      var ipResp = ipGeoClient.resolve();
      useLat = ipResp.getLat();
      useLon = ipResp.getLon();
      // Prefer human-friendly place name when available
      if (ipResp.getCity() != null && ipResp.getCountry() != null) {
        locationText = String.format(
            "approx. location: %s, %s (lat %.4f, lon %.4f)",
            ipResp.getCity(), ipResp.getCountry(), useLat, useLon);
      } else {
        locationText = String.format("approx. location (lat %.4f, lon %.4f)", useLat, useLon);
      }
    } else {
      useLat = lat;
      useLon = lon;
      locationText = String.format("selected location (lat %.4f, lon %.4f)", useLat, useLon);
    }

    try {
      WeatherObservation obs = openMeteoClient.fetchCurrent(useLat, useLon);
      return presenter.present(locationText, obs);
    } catch (CodedException ce) {
      String msg = UserFacingErrors.format(ce);
      System.err.println(msg);
      throw ce; // rethrow so application-level mapper can set exit code
    }
  }
}
