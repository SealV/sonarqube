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

package org.sonar.server.measure.ws;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.core.measure.db.MetricDto;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.MyBatis;
import org.sonar.server.db.DbClient;

import java.util.List;
import java.util.Map;

public class ListAction implements MetricsWsAction {

  public static final String PARAM_IS_CUSTOM = "is_custom";

  private static final Map<String, String> typeNamesByTypeKey = ImmutableMap.<String, String>builder()
    .put("INT", "Integer")
    .put("BOOL", "Yes/No")
    .put("FLOAT", "Float")
    .put("PERCENT", "Percent")
    .put("STRING", "Text")
    .put("LEVEL", "Level")
    .put("WORK_DUR", "Work Duration")
    .build();

  private final DbClient dbClient;

  public ListAction(DbClient dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("list")
      .setSince("5.2")
      .setResponseExample(Resources.getResource(getClass(), "example-list.json"))
      .setHandler(this);

    action.createParam(PARAM_IS_CUSTOM)
      .setExampleValue("true")
      .setDescription("Choose custom metrics following 3 cases:" +
        "<ul>" +
        "<li>true: only custom metrics are returned</li>" +
        "<li>false: only non custom metrics are returned</li>" +
        "<li>not specified: all metrics are returned</li>" +
        "</ul>");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    DbSession dbSession = dbClient.openSession(false);
    try {
      List<MetricDto> metrics = dbClient.metricDao().selectEnabled(dbSession);
      JsonWriter json = response.newJsonWriter();
      json.beginObject();
      writeMetrics(json, metrics);
      json.endObject();
      json.close();
    } finally {
      MyBatis.closeQuietly(dbSession);
    }
  }

  private void writeMetrics(JsonWriter json, List<MetricDto> metrics) {
    json.name("metrics");
    json.beginArray();
    for (MetricDto metric : metrics) {
      json.beginObject();
      json.prop("id", String.valueOf(metric.getId()));
      json.prop("key", metric.getKey());
      json.prop("name", metric.getShortName());
      json.prop("description", metric.getDescription());
      json.prop("domain", metric.getDomain());
      json.prop("type", typeNamesByTypeKey.get(metric.getValueType()));
      json.endObject();
    }
    json.endArray();
  }
}
