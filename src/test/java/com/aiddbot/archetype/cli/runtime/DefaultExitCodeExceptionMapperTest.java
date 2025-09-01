package com.aiddbot.archetype.cli.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultExitCodeExceptionMapperTest {

  private final DefaultExitCodeExceptionMapper mapper = new DefaultExitCodeExceptionMapper();

  @Test
  @DisplayName("null -> UNKNOWN")
  void nullIsUnknown() {
    assertThat(mapper.map(null)).isEqualTo(ExitCodes.UNKNOWN.code());
    assertThat(mapper.message(null)).isEqualTo("Unknown error");
  }

  @Test
  @DisplayName("CodedException returns its own code")
  void codedExceptionWins() {
    CodedException ex = new CodedException(ExitCodes.VALIDATION, "bad args");
    assertThat(mapper.map(ex)).isEqualTo(ExitCodes.VALIDATION.code());
  }

  @Test
  @DisplayName("IllegalArgumentException -> VALIDATION")
  void illegalArgumentIsValidation() {
    assertThat(mapper.map(new IllegalArgumentException("oops")))
        .isEqualTo(ExitCodes.VALIDATION.code());
  }

  @Test
  @DisplayName("ConstraintViolationException -> VALIDATION")
  void constraintViolationIsValidation() {
    jakarta.validation.ConstraintViolationException ex =
        new jakarta.validation.ConstraintViolationException("cv", java.util.Set.of());
    assertThat(mapper.map(ex)).isEqualTo(ExitCodes.VALIDATION.code());
  }

  @Test
  @DisplayName("IOException/UncheckedIOException -> IO")
  void ioIsIo() {
    assertThat(mapper.map(new IOException("io"))).isEqualTo(ExitCodes.IO.code());
    assertThat(mapper.map(new EOFException("eof"))).isEqualTo(ExitCodes.IO.code());
    assertThat(mapper.map(new UncheckedIOException(new IOException("io"))))
        .isEqualTo(ExitCodes.IO.code());
  }

  @Test
  @DisplayName("Network-related -> NETWORK")
  void networkIsNetwork() {
    assertThat(mapper.map(new SocketTimeoutException("timeout")))
        .isEqualTo(ExitCodes.NETWORK.code());
    assertThat(mapper.map(new TimeoutException("timeout"))).isEqualTo(ExitCodes.NETWORK.code());
    assertThat(mapper.map(new ConnectException("conn"))).isEqualTo(ExitCodes.NETWORK.code());
    assertThat(mapper.map(new UnknownHostException("host"))).isEqualTo(ExitCodes.NETWORK.code());
  }

  @Test
  @DisplayName("Other runtime -> RUNTIME")
  void otherRuntimeIsRuntime() {
    assertThat(mapper.map(new IllegalStateException("state"))).isEqualTo(ExitCodes.RUNTIME.code());
  }

  @Test
  @DisplayName("Other checked -> UNKNOWN")
  void otherCheckedIsUnknown() {
    class Checked extends Exception {}
    assertThat(mapper.map(new Checked())).isEqualTo(ExitCodes.UNKNOWN.code());
  }

  @Test
  @DisplayName("message() falls back to class simple name when blank")
  void messageFallback() {
    RuntimeException ex = new RuntimeException((String) null);
    assertThat(mapper.message(ex)).isEqualTo("RuntimeException");
  }
}
