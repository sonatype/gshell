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

import java.net.InetAddress;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.io.IO;
import com.planet57.gshell.command.CommandActionSupport;
import com.planet57.gshell.util.cli2.Option;

import javax.annotation.Nonnull;

/**
 * Displays the name of the current host.
 *
 * @since 2.0
 */
@Command(name = "hostname", description = "Displays the name of the current host")
public class HostnameAction
    extends CommandActionSupport
{
  @Option(name = "v", longName = "verbose", description = "Enable verbose output")
  private boolean verbose;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    IO io = context.getIo();

    InetAddress localhost = InetAddress.getLocalHost();
    io.println(localhost.getHostName());
    if (verbose) {
      io.println(localhost.getHostAddress());
    }

    return null;
  }
}
