/*
 * Copyright 2011-2024 smartics, Kronseder & Reiner GmbH
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


/**
 * Checks package cycles within the target folder classes folder.
 * <p>
 * This code is based on code by Daniel Seidewitz shown on <a href=
 * "http://stackoverflow.com/questions/3416547/maven-jdepend-fail-build-with-cycles"
 * >Stackoverflow</a>.
 * </p>
 */
public final class NoCyclicPackageDependencyRule
    extends AbstractNoCyclicPackageDependencyRule {
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

  @Override
  protected boolean includeTests() {
    return false;
  }

  // --- object basics --------------------------------------------------------

}
