/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.computation.qualityprofile;

import com.google.common.base.Objects;
import java.util.Date;
import javax.annotation.concurrent.Immutable;

import static java.util.Objects.requireNonNull;

@Immutable
public class QualityProfile {
  private final String key;
  private final String name;
  private final String language;
  private final Date rulesUpdatedAt;

  public QualityProfile(String key, String name, String language, Date rulesUpdatedAt) {
    this.key = requireNonNull(key);
    this.name = requireNonNull(name);
    this.language = requireNonNull(language);
    this.rulesUpdatedAt = requireNonNull(rulesUpdatedAt);
  }

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public String getLanguage() {
    return language;
  }

  public Date getRulesUpdatedAt() {
    return new Date(rulesUpdatedAt.getTime());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    QualityProfile qProfile = (QualityProfile) o;
    return key.equals(qProfile.key);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("key", key)
      .add("name", name)
      .add("language", language)
      .add("rulesUpdatedAt", rulesUpdatedAt)
      .toString();
  }
}
