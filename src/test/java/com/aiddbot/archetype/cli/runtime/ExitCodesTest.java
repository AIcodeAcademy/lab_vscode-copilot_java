package com.aiddbot.archetype.cli.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ExitCodesTest {
  @Test
  void codesAreStable() {
    assertThat(ExitCodes.SUCCESS.code()).isEqualTo(0);
    assertThat(ExitCodes.UNKNOWN.code()).isEqualTo(1);
    assertThat(ExitCodes.VALIDATION.code()).isEqualTo(2);
    assertThat(ExitCodes.RUNTIME.code()).isEqualTo(3);
    assertThat(ExitCodes.IO.code()).isEqualTo(4);
    assertThat(ExitCodes.NETWORK.code()).isEqualTo(5);
  }
}
