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
package com.planet57.gshell.command.support;

import java.util.List;
import java.util.MissingResourceException;

import com.planet57.gshell.command.CommandAction;
import com.planet57.gshell.command.resolver.NodePath;
import com.planet57.gshell.util.i18n.MessageSource;
import com.planet57.gshell.util.i18n.ResourceBundleMessageSource;
import jline.console.completer.Completer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Provides support for {@link CommandAction} implementations.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public abstract class CommandActionSupport
    implements CommandAction, CommandAction.NameAware
{
  protected final Logger log = LoggerFactory.getLogger(getClass());

  private String name;

  private MessageSource messages;

  private Completer[] completers;

  @Override
  public String getName() {
    checkState(name != null);
    return name;
  }

  @Override
  public void setName(final String name) {
    checkState(this.name == null);
    this.name = checkNotNull(name);
  }

  @Override
  public String getSimpleName() {
    return new NodePath(getName()).last();
  }

  @Override
  public MessageSource getMessages() {
    if (messages == null) {
      try {
        messages = createMessages();
      }
      catch (MissingResourceException e) {
        log.warn("Missing resources: " + e);

        messages = new MessageSource()
        {
          @Override
          public String getMessage(final String code) {
            return code;
          }

          @Override
          public String format(final String code, final Object... args) {
            return code;
          }
        };
      }
    }

    return messages;
  }

  /**
   * @since 2.4
   */
  protected ResourceBundleMessageSource createMessages() {
    return new ResourceBundleMessageSource(getClass());
  }

  @Override
  public Completer[] getCompleters() {
    return completers;
  }

  public void setCompleters(@Nullable final Completer... completers) {
    this.completers = completers;
  }

  public void setCompleters(@Nullable final List<Completer> completers) {
    if (completers != null) {
      setCompleters(completers.toArray(new Completer[completers.size()]));
    }
  }

  @Override
  public CommandAction copy() {
    try {
      return (CommandAction) super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }
}
