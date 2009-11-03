/*
 * Copyright (C) 2009 the original author(s).
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

package org.sonatype.gshell.core.commands;

import org.sonatype.gshell.cli.Argument;
import org.sonatype.gshell.cli.Option;
import org.sonatype.gshell.cli.Processor;
import org.sonatype.gshell.command.Command;
import org.sonatype.gshell.command.CommandContext;
import org.sonatype.gshell.command.IO;
import org.sonatype.gshell.core.command.CommandActionSupport;

import java.util.List;
import java.util.prefs.Preferences;

/**
 * Manage preferences.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command
public class PreferenceCommand
    extends CommandActionSupport
{
    public static enum Mode
    {
        LIST,
        REMOVE,
        SET,
        GET,
        UNSET,
        CLEAR,
        DUMP,
        LOAD,

        // TODO: Once we can effectively take objects, add listener support
    }

    @Option(name="-s", aliases={"--system"})
    private boolean system;

    @Argument(index=0, required=true)
    private Mode mode;
    
    @Argument(index=1, required=true)
    private String path;

    @Argument(index=2, multiValued=true)
    private List<String> args;

    public Object execute(final CommandContext context) throws Exception {
        assert context != null;
        IO io = context.getIo();

        Preferences root;
        if (system) {
            root = Preferences.systemRoot();
        }
        else {
            root = Preferences.userRoot();
        }

        Operation op = createOperation(context);
        Processor cli = new Processor(op);
        cli.process(args);

        Preferences node = root.node(path);
        log.debug("Selected node: {}", node);

        Object result = op.execute(node);

        node.sync();

        return result;
    }

    private Operation createOperation(CommandContext context) {
        log.debug("Mode: {}", mode);

        switch (mode) {
            case LIST:
                return new ListOperation(context);

            case REMOVE:
                return new RemoveOperation(context);

            case SET:
                return new SetOperation(context);

            case GET:
                return new GetOperation(context);

            case UNSET:
                return new UnsetOperation(context);

            case CLEAR:
                return new ClearOperation(context);

            case DUMP:
                return new DumpOperation(context);

            case LOAD:
                return new LoadOperation(context);

            default:
                // Should never happen
                throw new InternalError();
        }
    }

    private interface Operation
    {
        Object execute(Preferences prefs) throws Exception;
    }

    private abstract class OperationSupport
        implements Operation
    {
        protected final CommandContext context;
        protected final IO io;

        protected OperationSupport(final CommandContext context) {
            assert context != null;
            this.context = context;
            this.io = context.getIo();
        }
    }

    private class ListOperation
        extends OperationSupport
    {
        @Option(name="-r", aliases={"--recursive"})
        private boolean recursive;
        
        private ListOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            list(prefs);
            return Result.SUCCESS;
        }

        private void list(final Preferences node) throws Exception {
            io.info("{}", node.absolutePath());
            for (String key : node.keys()) {
                io.info("  {}={}", key, node.get(key, null));
            }
            if (recursive) {
                for (String name : node.childrenNames()) {
                    list(node.node(name));
                }
            }
        }
    }

    private class RemoveOperation
        extends OperationSupport
    {
        private RemoveOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            // TODO:
            return Result.SUCCESS;
        }
    }

    private class SetOperation
        extends OperationSupport
    {
        private SetOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            // TODO:
            return Result.SUCCESS;
        }
    }

    private class GetOperation
        extends OperationSupport
    {
        private GetOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            // TODO:
            return Result.SUCCESS;
        }
    }

    private class UnsetOperation
        extends OperationSupport
    {
        private UnsetOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            // TODO:
            return Result.SUCCESS;
        }
    }

    private class ClearOperation
        extends OperationSupport
    {
        private ClearOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            // TODO:
            return Result.SUCCESS;
        }
    }

    private class DumpOperation
        extends OperationSupport
    {
        // TODO: exportNode or exportSubtree

        private DumpOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            prefs.exportNode(io.streams.out);
            return Result.SUCCESS;
        }
    }

    private class LoadOperation
        extends OperationSupport
    {
        private LoadOperation(final CommandContext context) {
            super(context);
        }

        public Object execute(final Preferences prefs) throws Exception {
            // TODO:
            return Result.SUCCESS;
        }
    }
}