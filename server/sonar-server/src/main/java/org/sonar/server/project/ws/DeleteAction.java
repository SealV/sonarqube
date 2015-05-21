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

package org.sonar.server.project.ws;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.web.UserRole;
import org.sonar.core.component.ComponentDto;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.MyBatis;
import org.sonar.server.component.ComponentCleanerService;
import org.sonar.server.db.DbClient;
import org.sonar.server.user.UserSession;

import javax.annotation.Nullable;

import java.util.List;

public class DeleteAction implements ProjectsWsAction {
  private static final String ACTION = "delete";
  private static final String PARAM_UUIDS = "uuids";
  private static final String PARAM_KEYS = "keys";

  private final ComponentCleanerService componentCleanerService;
  private final DbClient dbClient;
  private final UserSession userSession;

  public DeleteAction(ComponentCleanerService componentCleanerService, DbClient dbClient, UserSession userSession) {
    this.componentCleanerService = componentCleanerService;
    this.dbClient = dbClient;
    this.userSession = userSession;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context
      .createAction(ACTION)
      .setPost(true)
      .setDescription("Delete one or several projects.<br /> Requires 'Administer System' permission.")
      .setSince("5.2")
      .setHandler(this);

    action
      .createParam(PARAM_UUIDS)
      .setDescription("List of project UUIDs to delete")
      .setExampleValue("ce4c03d6-430f-40a9-b777-ad877c00aa4d,c526ef20-131b-4486-9357-063fa64b5079");

    action
      .createParam(PARAM_KEYS)
      .setDescription("List of project keys to delete")
      .setExampleValue("org.apache.hbas:hbase,com.microsoft.roslyn:roslyn");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    userSession.checkGlobalPermission(UserRole.ADMIN);
    List<String> uuids = request.paramAsStrings(PARAM_UUIDS);
    List<String> keys = request.paramAsStrings(PARAM_KEYS);

    DbSession dbSession = dbClient.openSession(false);
    try {
      List<ComponentDto> projects = searchProjects(dbSession, uuids, keys);
      componentCleanerService.delete(dbSession, projects);
    } finally {
      MyBatis.closeQuietly(dbSession);
    }

    response.noContent();
  }

  private List<ComponentDto> searchProjects(DbSession dbSession, @Nullable List<String> uuids, @Nullable List<String> keys) {
    if (uuids != null) {
      return dbClient.componentDao().selectByUuids(dbSession, uuids);
    }
    if (keys != null) {
      return dbClient.componentDao().selectByKeys(dbSession, keys);
    }

    throw new IllegalArgumentException("UUIDs or keys must be provided");
  }
}
