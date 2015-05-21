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

/**
 * Implementation of {@link ComponentTypeTreeVisitor} that implements a depth first crawling of the
 * {@link ComponentTreeImpl} tree.
 */
public abstract class AbstractChildFirstComponentTypeTreeVisitor implements ComponentTypeTreeVisitor {
  @Override
  public void visit(ComponentTree tree) {
    for (ComponentTree child : tree.getChildren()) {
      this.visit(child);
    }
    switch (tree.getComponent().getType()) {
      case PROJECT:
        visitProject(tree);
        break;
      case MODULE:
        visitModule(tree);
        break;
      case DIRECTORY:
        visitDirectory(tree);
        break;
      case FILE:
        visitFile(tree);
        break;
      default:
        visitUnknown(tree);
    }
  }

}
