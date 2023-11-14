/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.http;

import com.google.common.base.Strings;
import org.eclipse.digitaltwin.basyx.core.StandardizedLiteralEnum;

/**
 * Helper class to map custom string literals to StandardizedLiteralEnums.
 *
 * @author alexgordtop
 */
public class StandardizedLiteralEnumHelper {

  /**
   * Maps string literals of {@link StandardizedLiteralEnum}s to enum constants. The string literals read via
   * getStandardizedLiteral() from the enum constants.
   *
   * @param <T>     Enum class implementing StandardizedLiteralEnum
   * @param clazz   Target enum with matching custom string literal
   * @param literal The literal as contained in e.g. XML schema
   * @return Enum constant
   * @throws IllegalArgumentException when string literal is not found in enum.
   */
  public static <T extends StandardizedLiteralEnum> T fromLiteral(Class<T> clazz, String literal) {
    if (Strings.isNullOrEmpty(literal)) {
      return null;
    }

    T[] enumConstants = clazz.getEnumConstants();
    for (T constant : enumConstants) {
      if (constant.getValue().equals(literal)) {
        return constant;
      }
    }
    throw new IllegalArgumentException("The literal '" + literal + "' is not contained in enum " + clazz.getName());
  }
}
