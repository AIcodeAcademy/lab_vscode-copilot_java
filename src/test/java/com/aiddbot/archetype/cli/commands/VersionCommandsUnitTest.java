package com.aiddbot.archetype.cli.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

class VersionCommandsUnitTest {

  @Test
  void version_returnsConfiguredVersion_whenPresent() {
    Environment env = Mockito.mock(Environment.class);
    Mockito.when(env.getProperty("app.version", "dev")).thenReturn("1.2.3");

    VersionCommands cmd = new VersionCommands(env);
    assertEquals("1.2.3", cmd.version());
  }

  @Test
  void version_returnsDev_whenBlankOrMissing() {
    Environment env = Mockito.mock(Environment.class);
    Mockito.when(env.getProperty("app.version", "dev")).thenReturn(" ");

    VersionCommands cmd = new VersionCommands(env);
    assertEquals("dev", cmd.version());
  }
}
