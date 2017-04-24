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
package com.planet57.gshell.commands.shell;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;

import org.jline.builtins.Nano;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Argument;
import com.planet57.gshell.variables.VariableNames;
import com.planet57.gshell.variables.Variables;
import org.jline.reader.Completer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Nano action.
 *
 * @since 3.0
 */
@Command(name = "nano")
public class NanoAction
    extends CommandActionSupport
{
  @Argument(required = true)
  private List<String> files;

  @Inject
  public void installCompleters(final @Named("file-name") Completer c1) {
    checkNotNull(c1);
    setCompleters(c1);
  }

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    Variables variables = context.getVariables();
    File currentDir = variables.get(VariableNames.SHELL_USER_DIR, File.class);
    assert currentDir != null;

    Nano nano = new Nano(context.getIo().terminal, currentDir);
    nano.open(files);
    nano.run();

    return Result.SUCCESS;
  }
}
