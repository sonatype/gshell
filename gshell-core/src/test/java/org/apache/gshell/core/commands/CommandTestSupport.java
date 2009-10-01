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

package org.apache.gshell.core.commands;

import org.apache.gshell.Shell;
import org.apache.gshell.VariableNames;
import org.apache.gshell.Variables;
import org.apache.gshell.ansi.Ansi;
import org.apache.gshell.command.CommandAction;
import org.apache.gshell.core.TestBranding;
import org.apache.gshell.core.TestShellBuilder;
import org.apache.gshell.registry.AliasRegistry;
import org.apache.gshell.registry.CommandRegistrar;
import org.apache.gshell.registry.CommandRegistry;
import org.apache.gshell.testsupport.PlexusTestSupport;
import org.apache.gshell.testsupport.TestIO;
import org.apache.gshell.testsupport.TestUtil;
import org.apache.gshell.util.Strings;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Support for testing {@link org.apache.gshell.command.CommandAction} instances.
 * 
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class CommandTestSupport
    implements VariableNames
{
    protected final String name;

    protected final TestShellBuilder builder = new TestShellBuilder();;

    private final TestUtil util = new TestUtil(this);

    private PlexusTestSupport plexus;
    
    private TestIO io;

    private Shell shell;

    protected AliasRegistry aliasRegistry;

    protected CommandRegistry commandRegistry;

    protected Variables vars;

    protected final Map<String,Class> requiredCommands = new HashMap<String,Class>();

    protected CommandTestSupport(final String name, final Class type) {
        assertNotNull(name);
        assertNotNull(type);
        this.name = name;
        requiredCommands.put(name, type);
    }

    @Before
    public void setUp() throws Exception {
        plexus = new PlexusTestSupport(this);

        io = new TestIO();

        TestShellBuilder builder = new TestShellBuilder();

        shell = builder
                .setContainer(plexus.getContainer())
                .setBranding(new TestBranding(util.resolveFile("target/shell-home")))
                .setIo(io)
                .setRegisterCommands(false)
                .create();

        CommandRegistrar registrar = plexus.lookup(CommandRegistrar.class);
        for (Map.Entry<String,Class> entry : requiredCommands.entrySet()) {
            registrar.registerCommand(entry.getKey(), entry.getValue().getName());
        }

        // For simplicity of output verification disable ANSI
        Ansi.setEnabled(false);
        
        vars = shell.getVariables();
        aliasRegistry = plexus.lookup(AliasRegistry.class);
        commandRegistry = plexus.lookup(CommandRegistry.class);
    }

    @After
    public void tearDown() throws Exception {
        commandRegistry = null;
        aliasRegistry = null;
        vars = null;
        io = null;
        shell.close();
        shell = null;
        plexus = null;
        requiredCommands.clear();
    }

    protected Shell getShell() {
        assertNotNull(shell);
        return shell;
    }

    protected TestIO getIo() {
        assertNotNull(io);
        return io;
    }

    protected Object execute(final String line) throws Exception {
        assertNotNull(line);
        return getShell().execute(line);
    }

    protected Object execute() throws Exception {
        return execute(name);
    }

    protected Object execute(final String... args) throws Exception {
        return execute(Strings.join(args, " "));
    }

    protected Object executeWithArgs(final String args) throws Exception {
        assertNotNull(args);
        return execute(name, args);
    }

    protected Object executeWithArgs(final String... args) throws Exception {
        assertNotNull(args);
        return execute(name, Strings.join(args, " "));
    }

    //
    // Assertion helpers
    //
    
    protected void assertEqualsSuccess(final Object result) {
        assertEquals(CommandAction.Result.SUCCESS, result);
    }

    protected void assertEqualsFailure(final Object result) {
        assertEquals(CommandAction.Result.FAILURE, result);
    }

    protected void assertOutputEquals(final String expected) {
        assertEquals(getIo().getOutputString(), expected);
    }

    protected void assertErrorOutputEquals(final String expected) {
        assertEquals(getIo().getErrorString(), expected);
    }

    //
    // Some default tests for all commands
    //

    @Test
    public void testRegistered() throws Exception {
        assertTrue(commandRegistry.containsCommand(name));
    }

    @Test
    public void testHelp() throws Exception {
        Object result;

        result = executeWithArgs("--help");
        assertEqualsSuccess(result);

        result = executeWithArgs("-h");
        assertEqualsSuccess(result);
    }

    @Test
    public void testDefault() throws Exception {
        Object result = execute();
        assertEqualsSuccess(result);
    }
}