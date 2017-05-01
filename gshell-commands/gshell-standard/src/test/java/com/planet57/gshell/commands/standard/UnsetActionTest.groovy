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
package com.planet57.gshell.commands.standard

import com.planet57.gshell.testharness.CommandTestSupport
import com.planet57.gshell.variables.Variables
import org.junit.Test

/**
 * Tests for {@link UnsetAction}.
 */
class UnsetActionTest
    extends CommandTestSupport
{
  UnsetActionTest() {
    super(UnsetAction.class)
  }

  @Test
  void testUndefineVariable() {
    Variables variables = shell.variables

    variables.set('foo', 'bar')
    assert variables.contains('foo')

    Object result = executeCommand('foo')
    assert result == null
    assert !variables.contains('foo')
  }

  @Test
  void testUndefineUndefinedVariable() {
    Variables variables = shell.variables

    assert !variables.contains('foo')
    Object result = executeCommand('foo')
    assert result == null
  }
}
