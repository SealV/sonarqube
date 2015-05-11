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

package org.sonar.core.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class NonNullInputPredicateTest {

  NonNullInputPredicate<Integer> sut = new TestPredicate();

  @Test
  public void fail_if_null_input() {
    try {
      sut.apply(null);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Null inputs are not allowed in this predicate");
    }
  }

  @Test
  public void apply() {
    assertThat(sut.apply(3)).isTrue();
  }

  private static class TestPredicate extends NonNullInputPredicate<Integer> {

    @Override
    protected boolean doApply(Integer input) {
      return true;
    }
  }

}
