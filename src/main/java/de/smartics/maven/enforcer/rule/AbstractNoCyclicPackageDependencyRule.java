/*
 * Copyright 2011-2026 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.smartics.maven.enforcer.rule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 * Checks package cycles within the target folder classes folder.
 * <p>
 * This code is based on code by Daniel Seidewitz shown on <a href=
 * "http://stackoverflow.com/questions/3416547/maven-jdepend-fail-build-with-cycles"
 * >Stackoverflow</a>.
 * </p>
 */
public abstract class AbstractNoCyclicPackageDependencyRule
    implements EnforcerRule {
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  // CHECKSTYLE:OFF
  @SuppressWarnings("unchecked")
  public void execute(final EnforcerRuleHelper helper)
      throws EnforcerRuleException {
    final Log log = helper.getLog();

    try {
      final MavenProject project = (MavenProject) helper.evaluate("${project}");
      final String projectName = project.getName();

      final File classesDir = new File(
          (String) helper.evaluate("${project.build.outputDirectory}"));

      if (classesDir.canRead()) {
        final JDepend jdepend = new JDepend();
        jdepend.addDirectory(classesDir.getAbsolutePath());
        addTestClassesIfRequested(helper, classesDir, jdepend);

        final Collection<JavaPackage> packages = jdepend.analyze();
        if (jdepend.containsCycles()) {
          final String buffer = collectCycles(packages);
          throw new EnforcerRuleException(
              "Dependency cycle check found package cycles in '" + projectName
                  + "': " + buffer);
        } else {
          log.info("No package cycles found in '" + projectName + "'.");
        }
      } else {
        log.warn("Skipping package cycle analysis since '" + classesDir
            + "' does not exist.");
      }
    } catch (final ExpressionEvaluationException e) {
      throw new EnforcerRuleException(
          "Dependency cycle check is unable to evaluate expression '"
              + e.getLocalizedMessage() + "'.",
          e);
    } catch (final IOException e) {
      throw new EnforcerRuleException(
          "Dependency cycle check is unable to access a classes directory '"
              + e.getLocalizedMessage() + "'.",
          e);
    }
  }
  // CHECKSTYLE:ON

  private void addTestClassesIfRequested(final EnforcerRuleHelper helper,
      final File classesDir, final JDepend jdepend)
      throws ExpressionEvaluationException, IOException {
    if (includeTests()) {
      final File testClassesDir = new File(
          (String) helper.evaluate("${project.build.testOutputDirectory}"));
      if (testClassesDir.canRead()) {
        jdepend.addDirectory(classesDir.getAbsolutePath());
      }
    }
  }

  /**
   * Checks if test classes are to be included in the cycle check.
   *
   * @return <code>true</code> if test packages are considered in the test,
   *         <code>false</code> otherwise.
   */
  protected abstract boolean includeTests();

  private static String collectCycles(final Collection<JavaPackage> packages) {
    final StringBuilder buffer = new StringBuilder(512);
    for (final JavaPackage aPackage : packages) {
      final List<JavaPackage> dependencies = new ArrayList<JavaPackage>();
      aPackage.collectCycle(dependencies);
      if (!dependencies.isEmpty()) {
        for (final JavaPackage dependency : dependencies) {
          buffer.append("->").append(dependency.getName());
        }
        buffer.append('\n');
      }
    }
    return buffer.toString();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Always returns the empty string.
   * </p>
   *
   * @see org.apache.maven.enforcer.rule.api.EnforcerRule#getCacheId()
   */
  public String getCacheId() {
    return "";
  }

  /**
   * {@inheritDoc}
   * <p>
   * Always returns <code>false</code>.
   * </p>
   *
   * @see org.apache.maven.enforcer.rule.api.EnforcerRule#isCacheable()
   */
  public boolean isCacheable() {
    return false;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Always returns <code>false</code>.
   * </p>
   *
   * @see org.apache.maven.enforcer.rule.api.EnforcerRule#isResultValid(org.apache.maven.enforcer.rule.api.EnforcerRule)
   */
  public boolean isResultValid(final EnforcerRule rule) {
    return false;
  }

  // --- object basics --------------------------------------------------------

}
