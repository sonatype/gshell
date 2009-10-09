/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.gshell.core.simple;

import org.apache.gshell.command.CommandAction;
import org.apache.gshell.registry.CommandRegistrarSupport;
import org.apache.gshell.registry.CommandRegistry;

/**
 * Simple {@link CommandRegistrar}.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 2.0
 */
public class SimpleCommandRegistrar
    extends CommandRegistrarSupport
{
    private final CommandRegistry commandRegistry;

    public SimpleCommandRegistrar(final CommandRegistry commandRegistry) {
        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void registerCommands() throws Exception {
        // Nothing
    }

    @SuppressWarnings({ "unchecked" })
    public void registerCommand(final String name, final String classname) throws Exception {
        assert name != null;
        assert classname != null;

        Class<CommandAction> type = (Class<CommandAction>) Thread.currentThread().getContextClassLoader().loadClass(classname);
        CommandAction command = type.newInstance();

        commandRegistry.registerCommand(name, command);
    }
}