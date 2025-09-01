package com.aiddbot.archetype.cli.runtime;

/**
 * Exception carrying an {@link ExitCodes} value to signal a specific failure category at the
 * process boundary.
 *
 * <p>Throw this for anticipated error conditions where the exit code is part of the contract (e.g.,
 * user misuse or domain-specific failures). Unknown/coding errors should be allowed to bubble up
 * and be categorized by the mapper.
 */
public final class CodedException extends RuntimeException {
  private final ExitCodes exitCode;

  public CodedException(ExitCodes exitCode, String message) {
    super(message);
    this.exitCode = exitCode;
  }

  public CodedException(ExitCodes exitCode, String message, Throwable cause) {
    super(message, cause);
    this.exitCode = exitCode;
  }

  /** The exit code associated with this failure. */
  public ExitCodes getExitCode() {
    return exitCode;
  }
}
