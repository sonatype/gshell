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
package com.planet57.gshell.internal;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.command.CommandHelper;
import com.planet57.gshell.command.IO;
import com.planet57.gshell.shell.Shell;
import com.planet57.gshell.util.cli2.CliProcessor;
import com.planet57.gshell.util.cli2.HelpPrinter;
import com.planet57.gshell.util.cli2.OpaqueArguments;
import com.planet57.gshell.util.pref.PreferenceProcessor;
import com.planet57.gshell.variables.Variables;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Function;
import org.sonatype.goodies.common.ComponentSupport;

import javax.annotation.Nonnull;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * GOGO {@link Function} adapting a {@link CommandAction}.
 *
 * @since 3.0
 */
public class CommandActionFunction
  extends ComponentSupport
  implements Function
{
  public static final String SHELL_VAR = ".shell";

  private final CommandAction action;

  public CommandActionFunction(final CommandAction action) {
    this.action = checkNotNull(action);
  }

  @Override
  public Object execute(final CommandSession session, final List<Object> arguments) throws Exception {
    log.debug("Executing ({}): {}", action.getName(), arguments);

    Stopwatch watch = Stopwatch.createStarted();

    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final Shell shell = (Shell) session.get(SHELL_VAR);
    final IO io = shell.getIo();

    Object result = null;
    try {
      boolean execute = true;

      // Process command preferences
      PreferenceProcessor pp = new PreferenceProcessor();
      pp.setBasePath(shell.getBranding().getPreferencesBasePath());
      pp.addBean(action);
      pp.process();

      // Process command arguments unless marked as opaque
      if (!(action instanceof OpaqueArguments)) {
        CommandHelper help = new CommandHelper();
        CliProcessor clp = help.createCliProcessor(action);
        clp.process(arguments);

        // Render command-line usage
        if (help.displayHelp) {
          io.out.println(CommandHelper.getDescription(action));
          io.out.println();

          HelpPrinter printer = new HelpPrinter(clp, io.terminal);
          printer.printUsage(io.out, action.getSimpleName());

          // Skip execution
          result = CommandAction.Result.SUCCESS;
          execute = false;
        }
      }

      if (execute) {
        result = action.execute(new CommandContext() {
          @Override
          @Nonnull
          public Shell getShell() {
            return shell;
          }

          @Override
          @Nonnull
          public List<?> getArguments() {
            return ImmutableList.copyOf(arguments);
          }

          @Override
          @Nonnull
          public IO getIo() {
            return io;
          }

          @Override
          @Nonnull
          public Variables getVariables() {
            return shell.getVariables();
          }
        });
      }
    }
    finally {
      io.flush();
      Thread.currentThread().setContextClassLoader(cl);
    }

    log.debug("Result: {}; {}", result, watch);
    return result;
  }
}