package com.aiddbot.archetype.cli.commands;

import org.springframework.core.env.Environment;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.util.StringUtils;

@ShellComponent
public class VersionCommands {

  private final Environment environment;

  public VersionCommands(Environment environment) {
    this.environment = environment;
  }

  @ShellMethod(key = "version", value = "Prints application version")
  public String version() {
    String version = environment.getProperty("app.version", "dev");
    return StringUtils.hasText(version) ? version : "dev";
  }
}
