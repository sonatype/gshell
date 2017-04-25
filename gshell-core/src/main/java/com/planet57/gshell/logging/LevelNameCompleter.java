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
package com.planet57.gshell.logging;

import com.planet57.gshell.util.jline.DynamicCompleter;
import com.planet57.gshell.util.jline.StringsCompleter2;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Completer} for {@link LevelComponent} names.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.5
 */
@Named("level-name")
@Singleton
public class LevelNameCompleter
    extends DynamicCompleter
{
  private final StringsCompleter2 delegate = new StringsCompleter2();

  @Nullable
  private LoggingSystem logging;

  @Inject
  public LevelNameCompleter(@Nullable final LoggingSystem logging) {
    this.logging = logging;
  }

  @Override
  protected void init() {
    if (logging != null) {
      logging.getLevels().forEach(level -> delegate.add(level.getName()));
    }
  }

  @Override
  protected Collection<Candidate> getCandidates() {
    return delegate.getCandidates();
  }
}
