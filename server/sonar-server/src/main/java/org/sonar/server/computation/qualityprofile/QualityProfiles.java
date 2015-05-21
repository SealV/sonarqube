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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import org.sonar.core.UtcDateUtils;

@Immutable
public class QualityProfiles {

  private final SortedSet<QualityProfile> profiles;

  public QualityProfiles(Iterable<QualityProfile> qualityProfiles) {
    this.profiles = ImmutableSortedSet.copyOf(QualityProfileComparator.INSTANCE, qualityProfiles);
  }

  public static QualityProfiles fromJson(String json) {
    return new QualityProfiles(Iterables.transform(new JsonParser().parse(json).getAsJsonArray(), JsonElementToQualityProfile.INSTANCE));
  }

  public SortedSet<QualityProfile> getProfiles() {
    return profiles;
  }

  public Map<String, QualityProfile> getProfilesByKey() {
    return Maps.uniqueIndex(this.profiles, QualityProfileToKey.INSTANCE);
  }

  private enum QualityProfileComparator implements Comparator<QualityProfile> {
    INSTANCE;

    @Override
    public int compare(QualityProfile o1, QualityProfile o2) {
      int c = o1.getLanguage().compareTo(o2.getLanguage());
      if (c == 0) {
        c = o1.getName().compareTo(o2.getName());
      }
      return c;
    }
  }

  private enum JsonElementToQualityProfile implements Function<JsonElement, QualityProfile> {
    INSTANCE;

    @Override
    public QualityProfile apply(@Nonnull JsonElement jsonElt) {
      JsonObject jsonProfile = jsonElt.getAsJsonObject();
      return new QualityProfile(
          jsonProfile.get("key").getAsString(),
          jsonProfile.get("name").getAsString(),
          jsonProfile.get("language").getAsString(),
          UtcDateUtils.parseDateTime(jsonProfile.get("rulesUpdatedAt").getAsString()));
    }
  }

  private enum QualityProfileToKey implements Function<QualityProfile, String> {
    INSTANCE;

    @Override
    public String apply(@Nonnull QualityProfile input) {
      return input.getKey();
    }
  }
}
