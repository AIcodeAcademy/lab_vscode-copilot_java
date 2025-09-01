package com.aiddbot.archetype.cli.runtime;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import jakarta.validation.ConstraintViolationException;

/**
 * Default implementation for mapping exceptions to exit codes.
 *
 * <p>Part of feature F3.4 "Graceful errors and exit codes".
 *
 * <p>Precedence order is:
 *
 * <ol>
 *   <li>{@link CodedException} explicit mapping
 *   <li>Validation errors → {@link ExitCodes#VALIDATION}
 *   <li>Network errors (timeouts, connect, DNS) → {@link ExitCodes#NETWORK}
 *   <li>Local IO errors → {@link ExitCodes#IO}
 *   <li>Other runtime errors → {@link ExitCodes#RUNTIME}
 *   <li>Unknown/other → {@link ExitCodes#UNKNOWN}
 * </ol>
 */
public class DefaultExitCodeExceptionMapper implements ExitCodeExceptionMapper {
  @Override
  public int map(Throwable t) {
    if (t == null) return ExitCodes.UNKNOWN.code();

    if (t instanceof CodedException ce) {
      return ce.getExitCode().code();
    }

    if (t instanceof IllegalArgumentException || t instanceof ConstraintViolationException) {
      return ExitCodes.VALIDATION.code();
    }

    if (t instanceof SocketTimeoutException
        || t instanceof TimeoutException
        || t instanceof ConnectException
        || t instanceof UnknownHostException) {
      return ExitCodes.NETWORK.code();
    }

    if (t instanceof IOException || t instanceof UncheckedIOException) {
      return ExitCodes.IO.code();
    }

    if (t instanceof RuntimeException) {
      return ExitCodes.RUNTIME.code();
    }

    return ExitCodes.UNKNOWN.code();
  }

  @Override
  public String message(Throwable t) {
    if (t == null) return "Unknown error";
    String msg = t.getMessage();
    if (msg == null || msg.isBlank()) {
      return t.getClass().getSimpleName();
    }
    return msg;
  }
}
