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

package org.sonar.server.computation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import javax.annotation.Nonnull;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Language;
import org.sonar.batch.protocol.output.BatchReport;
import org.sonar.batch.protocol.output.BatchReportReader;
import org.sonar.core.component.ComponentDto;
import org.sonar.server.computation.language.LanguageRepository;
import org.sonar.server.computation.componenttree.ComponentTree;
import org.sonar.server.computation.componenttree.ComponentTreeImpl;
import org.sonar.server.db.DbClient;

public class ComputationContext {

  private final BatchReportReader reportReader;
  private final ComponentDto project;
  private final Settings projectSettings;
  private final DbClient dbClient;
  // cache of metadata as it's frequently accessed
  private final BatchReport.Metadata reportMetadata;
  private ComponentTree componentTree;

  public ComputationContext(BatchReportReader reportReader, ComponentDto project, Settings projectSettings, DbClient dbClient) {
    this.reportReader = reportReader;
    this.project = project;
    this.projectSettings = projectSettings;
    this.dbClient = dbClient;
    this.reportMetadata = reportReader.readMetadata();
  }

  private ComponentTree buildComponentTree(ComputationContext computationContext, BatchReportReader reportReader) {
    int rootComponentRef = computationContext.getReportMetadata().getRootComponentRef();
    BatchReport.Component component = reportReader.readComponent(rootComponentRef);
    return new ComponentTreeImpl(this, component, buildComponentTreeImpl(computationContext, rootComponentRef));
  }

  private Iterable<ComponentTree> buildComponentTreeImpl(final ComputationContext computationContext, int componentRef) {
    BatchReport.Component component = computationContext.getReportReader().readComponent(componentRef);
    return Iterables.transform(
        component.getChildRefList(),
        new Function<Integer, ComponentTree>() {
          @Override
          public ComponentTree apply(@Nonnull Integer componentRef) {
            BatchReport.Component component = computationContext.getReportReader().readComponent(componentRef);
            return new ComponentTreeImpl(computationContext, component, buildComponentTreeImpl(computationContext, componentRef));
          }
        }
    );
  }

  public BatchReport.Metadata getReportMetadata() {
    return reportMetadata;
  }

  public ComponentDto getProject() {
    return project;
  }

  public BatchReportReader getReportReader() {
    return reportReader;
  }

  public Settings getProjectSettings() {
    return projectSettings;
  }

  public ComponentTree getComponentTree() {
    if (componentTree == null) {
      componentTree = buildComponentTree(this, reportReader);
    }
    return componentTree;
  }

  @Deprecated
  public DbClient getDbClient() {
    return dbClient;
  }

  public LanguageRepository getLanguageRepository() {
    return new LanguageRepository() {
      @Override
      public Optional<Language> find(String languageKey) {
        return Optional.absent();
      }
    };
  }
}
