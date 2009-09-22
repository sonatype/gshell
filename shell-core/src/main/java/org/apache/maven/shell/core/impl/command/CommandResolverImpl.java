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

package org.apache.maven.shell.core.impl.command;

import org.apache.maven.shell.command.Command;
import org.apache.maven.shell.command.CommandContext;
import org.apache.maven.shell.command.CommandException;
import org.apache.maven.shell.registry.CommandResolver;
import org.apache.maven.shell.command.CommandSupport;
import org.apache.maven.shell.command.OpaqueArguments;
import org.apache.maven.shell.registry.AliasRegistry;
import org.apache.maven.shell.registry.CommandRegistry;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default {@link CommandResolver} component.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
@Component(role= CommandResolver.class)
public class CommandResolverImpl
    implements CommandResolver
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Requirement
    private AliasRegistry aliasRegistry;

    @Requirement
    private CommandRegistry commandRegistry;

    public CommandResolverImpl() {}
    
    public CommandResolverImpl(final AliasRegistry aliasRegistry, final CommandRegistry commandRegistry) {
        assert aliasRegistry != null;
        this.aliasRegistry = aliasRegistry;

        assert commandRegistry != null;
        this.commandRegistry = commandRegistry;
    }

    public Command resolveCommand(final String name) throws CommandException {
        assert name != null;

        log.trace("Resolving command: {}", name);

        Command command = resolveAlias(name);
        if (command == null) {
            command = resolveRegistered(name);
            if (command != null) {
                // Copy the prototype to use
                command = command.copy();
            }
        }

        if (command == null) {
            throw new CommandException("Unable to resolve command: " + name);
        }

        log.trace("Resolved command: {}", command);

        return command;
    }

    private Command resolveAlias(final String name) throws CommandException {
        assert name != null;
        assert aliasRegistry != null;

        if (aliasRegistry.containsAlias(name)) {
            return new Alias(name, aliasRegistry.getAlias(name));
        }

        return null;
    }

    private Command resolveRegistered(final String name) throws CommandException {
        assert name != null;
        assert commandRegistry != null;

        if (commandRegistry.containsCommand(name)) {
            return commandRegistry.getCommand(name);
        }

        return null;
    }

    //
    // Alias
    //

    private static class Alias
        extends CommandSupport
        implements OpaqueArguments
    {
        private final String name;

        private final String target;

        public Alias(final String name, final String target) {
            assert name != null;
            assert target != null;

            this.name = name;
            this.target = target;
        }

        public String getName() {
            return name;
        }

        public Object execute(final CommandContext context) throws Exception {
            assert context != null;

            String alias = target;

            // Need to append any more arguments in the context
            Object[] args = context.getArguments();
            if (args.length > 0) {
                alias = String.format("%s %s", target, StringUtils.join(args, " "));
            }

            log.debug("Executing alias ({}) -> {}", name, alias);

            return context.getShell().execute(alias);
        }
    }
}