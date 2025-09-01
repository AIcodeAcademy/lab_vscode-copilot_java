package com.aiddbot.archetype.cli.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CodedExceptionTest {
  @Test
  void carriesExitCodeAndMessage() {
    CodedException ex = new CodedException(ExitCodes.IO, "failed");
    assertThat(ex.getExitCode()).isEqualTo(ExitCodes.IO);
    assertThat(ex.getMessage()).isEqualTo("failed");
  }

  @Test
  void supportsCause() {
    IllegalStateException cause = new IllegalStateException("boom");
    CodedException ex = new CodedException(ExitCodes.UNKNOWN, "wrap", cause);
    assertThat(ex.getExitCode()).isEqualTo(ExitCodes.UNKNOWN);
    assertThat(ex.getCause()).isSameAs(cause);
  }
}
