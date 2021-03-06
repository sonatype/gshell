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
package com.planet57.gshell.util.jline;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

/**
 * Dynamic {@link Completer}.
 *
 * @see #init()
 * @see #prepare()
 *
 * @since 3.0
 */
public abstract class DynamicCompleter
  implements Completer
{
  private boolean initialized = false;

  public DynamicCompleter() {
    // empty
  }

  /**
   * Invoked first time candidates are accessed.
   */
  protected void init() {
    // empty
  }

  /**
   * Invoked before candidates are accessed.
   */
  protected void prepare() {
    // empty
  }

  /**
   * Handles {@link #init()} and {@link #prepare()} to establish configuration of candidates.
   */
  private void setup() {
    if (!initialized) {
      init();
      initialized = true;
    }
    prepare();
  }

  protected abstract Collection<Candidate> getCandidates();

  @Override
  public void complete(final LineReader reader, final ParsedLine commandLine, final List<Candidate> candidates) {
    checkNotNull(candidates);
    setup();
    candidates.addAll(getCandidates());
  }
}
