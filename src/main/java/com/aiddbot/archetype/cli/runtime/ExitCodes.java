package com.aiddbot.archetype.cli.runtime;

/**
 * Canonical process exit codes for the CLI.
 *
 * <p>These codes are used consistently across the application to signal outcome to the caller
 * (shell, CI, scripts). Semantics:
 *
 * <ul>
 *   <li>{@link #SUCCESS} – Successful execution.
 *   <li>{@link #VALIDATION} – Invalid user input or configuration (e.g., {@link
 *       IllegalArgumentException}, bean validation errors).
 *   <li>{@link #RUNTIME} – Uncaught runtime errors that aren’t IO or network related.
 *   <li>{@link #IO} – Local filesystem or stream errors (e.g., {@link java.io.IOException}).
 *   <li>{@link #NETWORK} – Remote connectivity issues (timeouts, DNS, connect failures).
 *   <li>{@link #UNKNOWN} – Anything else not covered by previous categories.
 * </ul>
 */
public enum ExitCodes {
  /** Operation completed successfully. */
  SUCCESS(0),
  /** Input/argument/config validation failed. */
  VALIDATION(2),
  /** Unhandled runtime error occurred. */
  RUNTIME(3),
  /** Local IO failed (files, streams, permissions). */
  IO(4),
  /** Remote communication failed (timeouts, DNS, connection). */
  NETWORK(5),
  /** Fallback when no specific mapping applies. */
  UNKNOWN(1);

  private final int code;

  ExitCodes(int code) {
    this.code = code;
  }

  public int code() {
    return code;
  }
}
