package com.aiddbot.archetype.cli.integrations.ipapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Minimal mapping of ip-api.com JSON response used by the CLI.
 *
 * <p>Part of feature F3.1. Only the fields required to obtain coordinates are mapped; unknown
 * fields are ignored for forward-compatibility.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpGeoResponse {

  /** API status field (e.g., "success" or "fail"). */
  private String status;

  /** Optional failure message from the API when status != success. */
  private String message;

  @JsonProperty("lat")
  /** Latitude in decimal degrees. */
  private Double lat;

  @JsonProperty("lon")
  /** Longitude in decimal degrees. */
  private Double lon;

  @JsonProperty("city")
  /** Optional city name returned by the IP geolocation service. */
  private String city;

  @JsonProperty("country")
  /** Optional country name returned by the IP geolocation service. */
  private String country;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public Double getLon() {
    return lon;
  }

  public void setLon(Double lon) {
    this.lon = lon;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
