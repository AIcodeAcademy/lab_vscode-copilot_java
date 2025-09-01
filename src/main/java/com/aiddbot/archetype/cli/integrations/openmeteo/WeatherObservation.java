package com.aiddbot.archetype.cli.integrations.openmeteo;

/**
 * Domain object representing a current weather observation.
 *
 * <p>Produced by F3.2 (Open‑Meteo client) and consumed by F3.3 (presentation).
 */
public class WeatherObservation {

  /** Air temperature in Celsius. */
  private final double temperatureCelsius;

  /** Wind speed in meters per second. */
  private final double windSpeed;

  /** Open‑Meteo weather code (WMO). */
  private final int weatherCode;

  public WeatherObservation(double temperatureCelsius, double windSpeed, int weatherCode) {
    this.temperatureCelsius = temperatureCelsius;
    this.windSpeed = windSpeed;
    this.weatherCode = weatherCode;
  }

  /**
   * @return temperature in Celsius
   */
  public double getTemperatureCelsius() {
    return temperatureCelsius;
  }

  /**
   * @return wind speed in m/s
   */
  public double getWindSpeed() {
    return windSpeed;
  }

  /**
   * @return WMO weather code
   */
  public int getWeatherCode() {
    return weatherCode;
  }
}
