/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.script;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

/**
 * An exception thrown when trying to compile Java programs from strings
 * containing source.
 * 
 * @author <a href="mailto:David.Biesack@sas.com">David J. Biesack</a>
 */
public class CharSequenceCompilerException extends Exception {
   private static final long serialVersionUID = 1L;
   /**
    * The fully qualified name of the class that was being compiled.
    */
   private Set<String> classNames;
   // Unfortunately, Diagnostic and Collector are not Serializable, so we can't
   // serialize the collector.
   transient private DiagnosticCollector<JavaFileObject> diagnostics;

   public CharSequenceCompilerException(String message,
         Set<String> qualifiedClassNames, Throwable cause,
         DiagnosticCollector<JavaFileObject> diagnostics) {
      super(message, cause);
      setClassNames(qualifiedClassNames);
      setDiagnostics(diagnostics);
   }

   public CharSequenceCompilerException(String message,
         Set<String> qualifiedClassNames,
         DiagnosticCollector<JavaFileObject> diagnostics) {
      super(message);
      setClassNames(qualifiedClassNames);
      setDiagnostics(diagnostics);
   }

   public CharSequenceCompilerException(Set<String> qualifiedClassNames,
         Throwable cause, DiagnosticCollector<JavaFileObject> diagnostics) {
      super(cause);
      setClassNames(qualifiedClassNames);
      setDiagnostics(diagnostics);
   }

   private void setClassNames(Set<String> qualifiedClassNames) {
      // create a new HashSet because the set passed in may not
      // be Serializable. For example, Map.keySet() returns a non-Serializable
      // set.
      classNames = new HashSet<String>(qualifiedClassNames);
   }

   private void setDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
      this.diagnostics = diagnostics;
   }

   /**
    * Gets the diagnostics collected by this exception.
    * 
    * @return this exception's diagnostics
    */
   public DiagnosticCollector<JavaFileObject> getDiagnostics() {
      return diagnostics;
   }

   /**
    * @return The name of the classes whose compilation caused the compile
    *         exception
    */
   public Collection<String> getClassNames() {
      return Collections.unmodifiableSet(classNames);
   }
}
