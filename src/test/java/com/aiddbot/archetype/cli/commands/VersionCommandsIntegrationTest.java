package com.aiddbot.archetype.cli.commands;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.shell.command.CommandCatalog;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class VersionCommandsIntegrationTest {

  @Autowired(required = false)
  private CommandCatalog commandCatalog;

  @Test
  void helpAndVersionCommandsAreExposed() {
    // Spring Shell auto-config registers a command catalog; verify key commands.
    assertThat(commandCatalog).as("CommandCatalog should be available").isNotNull();
    assertThat(commandCatalog.getRegistrations().keySet())
        .anyMatch(name -> name.equalsIgnoreCase("help"))
        .anyMatch(name -> name.equalsIgnoreCase("version"));
  }
}
