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
package org.sonar.server.computation.componenttree;

import java.util.List;
import org.sonar.batch.protocol.output.BatchReport;
import org.sonar.server.computation.event.EventRepository;
import org.sonar.server.computation.language.LanguageRepository;
import org.sonar.server.computation.measure.MeasureRepository;

public interface ComponentTree {
  // FIXME should not expose Component from BatchReport (do not expose any object but replace with getKey(), getUUId(), getType(), etc. ?)
  BatchReport.Component getComponent();

  List<ComponentTree> getChildren();

  /**
   * The language repository for the current component
   */
  LanguageRepository getLanguageRepository();

  /**
   * The event repository for the current component
   */
  EventRepository getEventRepository();

  /**
   * the measure repository for the current component
   */
  MeasureRepository getMeasureRepository();

}
