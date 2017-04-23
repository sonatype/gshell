/*
 * Copyright (c) 2009-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet57.gshell.console;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.sonatype.goodies.common.ComponentSupport;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides an abstraction of a console.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public class Console
    extends ComponentSupport
    implements Runnable
{
  private final LineReader lineReader;

  private final ConsolePrompt prompt;

  private final Callable<ConsoleTask> taskFactory;

  private final ConsoleErrorHandler errorHandler;

  private volatile ConsoleTask currentTask;

  private volatile boolean running;

  public Console(final LineReader lineReader,
                 final ConsolePrompt prompt,
                 final Callable<ConsoleTask> taskFactory,
                 final ConsoleErrorHandler errorHandler)
    throws IOException
  {
    this.prompt = checkNotNull(prompt);
    this.taskFactory = checkNotNull(taskFactory);
    this.errorHandler = checkNotNull(errorHandler);
    this.lineReader = checkNotNull(lineReader);
  }

  public ConsoleTask getCurrentTask() {
    return currentTask;
  }

  public void close() {
    if (running) {
      log.trace("Closing");
      running = false;
    }
  }

  public void run() {
    log.trace("Running");
    running = true;

    // prepare handling for CTRL-C
    Terminal terminal = lineReader.getTerminal();
    Terminal.SignalHandler intHandler = terminal.handle(Terminal.Signal.INT, s -> {
      interruptTask();
    });

    try {
      while (running) {
        try {
          running = work();
        }
        catch (Throwable t) {
          log.trace("Work failed", t);
          running = errorHandler.handleError(t);
        }
      }
    }
    finally {
      terminal.handle(Terminal.Signal.INT, intHandler);
    }

    log.trace("Stopped");
  }

  /**
   * Read and execute a line.
   *
   * @return False to abort, true to continue running.
   * @throws Exception Work failed.
   */
  private boolean work() throws Exception {
    String line = lineReader.readLine(prompt != null ? prompt.prompt() : ConsolePrompt.DEFAULT_PROMPT);

    log.trace("Read line: {}", line);

    if (log.isTraceEnabled()) {
      traceLine(line);
    }

    if (line != null) {
      line = line.trim();
    }

    if (line == null || line.length() == 0) {
      return true;
    }

    // Build the task and execute it
    checkState(currentTask == null);
    currentTask = taskFactory.call();
    log.trace("Current task: {}", currentTask);

    try {
      return currentTask.execute(line);
    }
    finally {
      currentTask = null;
    }
  }

  private void traceLine(@Nullable final String line) {
    if (line == null) {
      return;
    }

    StringBuilder idx = new StringBuilder();
    StringBuilder hex = new StringBuilder();

    for (byte b : line.getBytes()) {
      String h = Integer.toHexString(b);

      hex.append('x').append(h).append(' ');
      idx.append(' ').append((char) b).append("  ");
    }

    log.trace("HEX: {}", hex);
    log.trace("     {}", idx);
  }

  private boolean interruptTask() {
    boolean interrupt = false;

    ConsoleTask task = getCurrentTask();
    if (task != null) {
      synchronized (task) {
        log.debug("Interrupting task");
        interrupt = true;

        if (task.isStopping()) {
          task.abort();
        }
        else if (task.isRunning()) {
          task.stop();
        }
      }
    }
    else {
      log.debug("No task running to interrupt");
    }

    return interrupt;
  }
}
