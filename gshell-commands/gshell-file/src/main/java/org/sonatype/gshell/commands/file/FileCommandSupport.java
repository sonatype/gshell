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

package org.sonatype.gshell.commands.file;

import org.sonatype.gshell.VariableNames;
import org.sonatype.gshell.Variables;
import org.sonatype.gshell.command.CommandActionSupport;
import org.sonatype.gshell.command.CommandContext;

import java.io.File;
import java.io.IOException;

/**
 * Support for file-related commands.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class FileCommandSupport
    extends CommandActionSupport
    implements VariableNames
{
    private File resolveDir(final CommandContext context, final String name) throws IOException {
        assert context != null;
        assert name != null;

        Variables vars = context.getVariables();
        String path = vars.get(name, String.class);

        return new File(path).getCanonicalFile();
    }

    protected File getShellHomeDir(final CommandContext context) throws IOException {
        return resolveDir(context, SHELL_HOME);
    }

    protected File getUserDir(final CommandContext context) throws IOException {
        return resolveDir(context, SHELL_USER_DIR);
    }

    protected File getUserHomeDir(final CommandContext context) throws IOException {
        return resolveDir(context, SHELL_USER_HOME);
    }

    protected File resolveFile(final CommandContext context, File baseDir, final String path) throws IOException {
        assert context != null;
        // baseDir may be null
        // path may be null

        File userDir = getUserDir(context);

        if (baseDir == null) {
            baseDir = userDir;
        }

        File file;
        if (path == null) {
            file = baseDir;
        }
        else if (path.startsWith("~")) {
            File userHome = getUserHomeDir(context);
            file = new File(userHome.getPath() + path.substring(1));
        }
        else {
            file = new File(path);
        }

        if (!file.isAbsolute()) {
            file = new File(baseDir, file.getPath());
        }

        return file.getCanonicalFile();
    }

    protected File resolveFile(final CommandContext context, final String path) throws IOException {
        return resolveFile(context, null, path);
    }

    protected boolean hasChildren(final File file) {
        assert file != null;

        if (file.isDirectory()) {
            File[] children = file.listFiles();

            if (children != null && children.length != 0) {
                return true;
            }
        }

        return false;
    }
}