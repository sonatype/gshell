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
package com.planet57.gshell.util.style

import org.sonatype.goodies.testsupport.TestSupport

import com.planet57.gossip.Log
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStyle
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Tests for {@link StyleFactory}.
 */
class StyleFactoryTest
  extends TestSupport
{
  private MemoryStyleSource source

  private StyleFactory underTest

  @Before
  void setUp() {
    // force bootstrap gossip logger to adapt to runtime logger-factory
    Log.configure(LoggerFactory.getILoggerFactory())

    this.source = new MemoryStyleSource()
    this.underTest = new StyleFactory(source, 'test')
  }

  @Test
  void 'direct style'() {
    def string = underTest.style('bold,fg:red', 'foo %s', 'bar')
    def style = AttributedStyle.BOLD.foreground(AttributedStyle.RED)
    assert string == new AttributedString('foo bar', style)
  }

  @Test
  void 'referenced style'() {
    source.group('test').put('very-red', 'bold,fg:red')
    def string = underTest.style('.very-red', 'foo %s', 'bar')
    def style = AttributedStyle.BOLD.foreground(AttributedStyle.RED)
    assert string == new AttributedString('foo bar', style)
  }

  // FIXME: -: syntax doesn't work due to split being done before this gets handled

  @Test
  void 'missing referenced style with default'() {
    def string = underTest.style('.very-red:-bold,fg:red', 'foo %s', 'bar')
    def style = AttributedStyle.BOLD.foreground(AttributedStyle.RED)
    assert string == new AttributedString('foo bar', style)
  }

  @Test
  void 'missing referenced style with customized'() {
    source.group('test').put('very-red', 'bold,fg:yellow')
    def string = underTest.style('.very-red:-bold,fg:red', 'foo %s', 'bar')
    def style = AttributedStyle.BOLD.foreground(AttributedStyle.YELLOW)
    assert string == new AttributedString('foo bar', style)
  }
}