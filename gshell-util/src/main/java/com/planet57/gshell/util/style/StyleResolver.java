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
package com.planet57.gshell.util.style;

import java.util.Locale;

import javax.annotation.Nullable;

import com.google.common.base.Splitter;
import com.planet57.gossip.Log;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Resolves named (or source-referenced) {@link AttributedStyle}.
 *
 * @since 3.0
 */
public class StyleResolver
{
  private static final Logger log = Log.getLogger(StyleResolver.class);

  private final StyleSource source;

  private final String group;

  public StyleResolver(final StyleSource source, final String group) {
    this.source = checkNotNull(source);
    this.group = checkNotNull(group);
  }

  /**
   * Resolve the given style specification.
   *
   * If for some reason the specification is invalid, then {@link AttributedStyle#DEFAULT} will be used.
   */
  public AttributedStyle resolve(final String spec) {
    checkNotNull(spec);
    // apply style specification based on DEFAULT style
    return apply(AttributedStyle.DEFAULT, spec);
  }

  /**
   * Apply style specification.
   */
  private AttributedStyle apply(AttributedStyle style, final String spec) {
    for (String item : Splitter.on(',').omitEmptyStrings().trimResults().split(spec)) {
      if (item.startsWith(".")) {
        style = applyReference(style, item);
      }
      else if (item.contains(":")) {
        style = applyColor(style, item);
      }
      else {
        style = applyNamed(style, item);
      }
    }

    return style;
  }

  /**
   * Apply source-referenced named style.
   *
   * @param name Source reference: {@code .<discriminator>}.  {@code discriminator} is used to consult {@link StyleSource}.
   */
  private AttributedStyle applyReference(final AttributedStyle style, final String name) {
    if (name.length() == 1) {
      log.warn("Invalid style-reference; missing discriminator: {}", name);
    }
    else {
      String ref = name.substring(1, name.length());
      String spec = source.get(group, ref);
      // TODO: this does not protect against circular styles references; beware
      if (spec == null) {
        log.warn("Missing style-reference: {}", ref);
      }
      else {
        // FIXME: this could presently be an @{...} expression, which isn't valid here
        return apply(style, spec);
      }
    }

    return style;
  }

  /**
   * Apply default named styles.
   */
  private AttributedStyle applyNamed(final AttributedStyle style, final String name) {
    switch (name.toLowerCase(Locale.US)) {
      case "default":
        return AttributedStyle.DEFAULT;

      case "bold":
        return style.bold();

      case "faint":
        return style.faint();

      case "italic":
        return style.italic();

      case "underline":
        return style.underline();

      case "blink":
        return style.blink();

      case "inverse":
        return style.inverse();

      case "inverse-neg":
      case "inverseNeg":
        return style.inverseNeg();

      case "conceal":
        return style.conceal();

      case "crossed-out":
      case "crossedOut":
        return style.crossedOut();

      case "hidden":
        return style.hidden();

      default:
        log.warn("Unknown style: {}", name);
        return style;
    }
  }

  /**
   * Apply color styles specification.
   *
   * @param spec Color specification: {@code <color-mode>:<color-name>}
   */
  private AttributedStyle applyColor(final AttributedStyle style, final String spec) {
    // extract color-mode:color-name
    String[] parts = spec.split(":", 2);
    String colorMode = parts[0];
    String colorName = parts[1];

    // resolve the color-name
    Integer color = color(colorName);
    if (color == null) {
      log.warn("Invalid color-name: {}", colorName);
    }
    else {
      // resolve and apply color-mode
      switch (colorMode.toLowerCase(Locale.US)) {
        case "foreground":
        case "fg":
          return style.foreground(color);

        case "background":
        case "bg":
          return style.background(color);

        default:
          log.warn("Invalid color-mode: {}", colorMode);
      }
    }
    return style;
  }

  /**
   * Returns the color identifier for the given name.
   */
  @Nullable
  private static Integer color(final String name) {
    switch (name.toLowerCase(Locale.US)) {
      case "black":
        return AttributedStyle.BLACK;

      case "red":
        return AttributedStyle.RED;

      case "green":
        return AttributedStyle.GREEN;

      case "yellow":
        return AttributedStyle.YELLOW;

      case "blue":
        return AttributedStyle.BLUE;

      case "magenta":
        return AttributedStyle.MAGENTA;

      case "cyan":
        return AttributedStyle.CYAN;

      case "white":
        return AttributedStyle.WHITE;

      // FIXME: bright usage here may be invalid; do we need a flag to indicate bright color style?
      case "bright":
        return AttributedStyle.BRIGHT;
    }

    return null;
  }
}