package com.aiddbot.archetype.cli.runtime;

/**
 * Maps exceptions to {@link ExitCodes} and renders a concise message for the user.
 *
 * <p>Implementations should be deterministic and side-effect free. They may unwrap custom wrapper
 * exceptions and should prefer specific categories (e.g., network vs IO) before generic runtime
 * failures.
 */
public interface ExitCodeExceptionMapper {
  /**
   * Resolve the most appropriate {@link ExitCodes} for the given Throwable.
   *
   * @param t the error to categorize (may be {@code null})
   * @return the numeric exit code to return from the process
   */
  int map(Throwable t);

  /**
   * Produce a stable, user-friendly message summarizing the failure.
   *
   * @param t the error (may be {@code null})
   * @return the message to show to the user
   */
  String message(Throwable t);
}
