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

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.sonar.core.measure.db.MetricDto;
import org.sonar.core.persistence.DbSession;
import org.sonar.core.persistence.DbTester;
import org.sonar.server.db.DbClient;
import org.sonar.server.measure.persistence.MetricDao;
import org.sonar.server.ws.WsTester;

public class ListActionTest {

  @ClassRule
  public static DbTester db = new DbTester();
  DbClient dbClient;
  DbSession dbSession;
  WsTester ws;

  @Before
  public void setUp() {
    dbClient = new DbClient(db.database(), db.myBatis(), new MetricDao());
    dbSession = dbClient.openSession(false);
    ws = new WsTester(new MetricsWs(new ListAction(dbClient)));
  }

  @After
  public void tearDown() {
    dbSession.close();
  }

  @Test
  public void list_metrics_in_database() throws Exception {
    dbClient.metricDao().insert(dbSession, newCustomlMetric("1"));
    dbClient.metricDao().insert(dbSession, newCustomlMetric("2"));
    dbClient.metricDao().insert(dbSession, newCustomlMetric("3"));
    dbSession.commit();

    WsTester.Result result = ws.newGetRequest("api/metrics", "list").execute();

    result.assertJson(getClass(), "list_metrics.json");
  }

  private MetricDto newCustomlMetric(String id) {
    return MetricTesting.newDto()
      .setKey("custom-key-" + id)
      .setShortName("custom-name-" + id)
      .setDomain("custom-domain-" + id)
      .setDescription("custom-description-" + id)
      .setValueType("INT")
      .setUserManaged(true)
      .setEnabled(true);
  }
}
