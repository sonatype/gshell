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
import com.planet57.gshell.util.style.StyleBundle.DefaultStyle
import com.planet57.gshell.util.style.StyleBundle.StyleGroup
import com.planet57.gshell.util.style.StyleBundle.StyleName
import org.jline.utils.AttributedString
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * Various style trials.
 */
class StyleTrial
  extends TestSupport
{
  private MemoryStyleSource source

  @Before
  void setUp() {
    // force bootstrap gossip logger to adapt to runtime logger-factory
    Log.configure(LoggerFactory.getILoggerFactory())

    this.source = new MemoryStyleSource()
    Styler.source = source
  }

  @StyleGroup('test')
  private interface Styles
      extends StyleBundle
  {
    @DefaultStyle('@{bold,fg:yellow %3d}')
    AttributedString history_index(int index) // maps to '.history_index'

    @StyleName('history_index')
    @DefaultStyle('@{bold,fg:yellow %3d}')
    AttributedString whatever(int index) // maps to '.history_index' due to @StyleName
  }

  //
  // StyleBundle
  //

  @Test
  void 'styler style-bundle default-style'() {
    def styles = Styler.bundle(Styles.class)

    def string = styles.history_index(1)

    def style = AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.YELLOW)
    def string2 = new AttributedString(String.format('%3d', 1), style)
    assert string == string2
  }

  //
  // StyleFactory
  //

  @Test
  void 'styler style-factory style-format direct'() {
    def styles = Styler.factory('test')

    def string = styles.style('bold,fg:yellow', '%3d', 11)

    def style = AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.YELLOW)
    def string2 = new AttributedString(String.format('%3d', 1), style)
    assert string == string2
  }

  @Test
  void 'styler style-factory style-format sourced'() {
    def styles = Styler.factory('test')

    def string = styles.style('.history_index', '%3d', 1)

    def style = AttributedStyle.DEFAULT.bold().foreground(AttributedStyle.YELLOW)
    def string2 = new AttributedString(String.format('%3d', 1), style)
    assert string == string2
  }

  @Test
  void 'styler style-factory expression'() {
    def styles = Styler.factory('test')
    AttributedStringBuilder buff = new AttributedStringBuilder()

    buff.append(styles.style('@{bold,fg:yellow %3d}', 1))
    buff.append(styles.style('@{.history_index %3d}', 1))
  }
}