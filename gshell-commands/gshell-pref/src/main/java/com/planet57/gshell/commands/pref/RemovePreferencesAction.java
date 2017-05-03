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
package com.planet57.gshell.commands.pref;

import java.util.prefs.Preferences;

import com.planet57.gshell.command.Command;
import com.planet57.gshell.command.CommandContext;
import com.planet57.gshell.util.cli2.Option;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Remove a tree of preferences.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
@Command(name = "pref/remove", description = "Remove preferences")
public class RemovePreferencesAction
    extends PreferenceNodeActionSupport
{
  @Option(name = "r", longName = "tree", description = "Remove tree")
  private boolean tree;

  @Override
  public Object execute(@Nonnull final CommandContext context) throws Exception {
    Preferences prefs = node();

    log.debug("Removing preferences: {}", prefs);

    if (tree) {
      prefs.clear();
      prefs.sync();
    }
    else {
      prefs.removeNode();
    }

    return null;
  }
}
