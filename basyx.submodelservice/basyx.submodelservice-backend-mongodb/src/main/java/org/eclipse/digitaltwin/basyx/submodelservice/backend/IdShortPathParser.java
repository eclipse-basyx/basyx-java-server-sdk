/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import java.util.ArrayDeque;
import java.util.Deque;

import org.springframework.lang.NonNull;

/**
 * Parses a string representation of a path to an IdShort in a submodel.
 * 
 * @author mateusmolina
 */
public final class IdShortPathParser {

    public static Deque<GenericPath> parse(@NonNull String idShortPath) {
        Deque<GenericPath> paths = new ArrayDeque<>();
        StringBuilder currentPath = new StringBuilder();
        StringBuilder currentIndex = new StringBuilder();
        boolean parsingIndex = false;

        for (char c : idShortPath.toCharArray()) {
            if (c == '.') {
                if (currentPath.length() > 0) {
                    paths.add(new IdShortPath(currentPath.toString()));
                    currentPath.setLength(0);
                }
            } else if (c == '[') {
                if (currentPath.length() > 0) {
                    paths.add(new IdShortPath(currentPath.toString()));
                    currentPath.setLength(0);
                }
                parsingIndex = true;
            } else if (c == ']') {
                if (parsingIndex && currentIndex.length() > 0) {
                    paths.add(new IndexPath(Integer.parseInt(currentIndex.toString())));
                    currentIndex.setLength(0);
                    parsingIndex = false;
                }
            } else {
                if (parsingIndex) {
                    currentIndex.append(c);
                } else {
                    currentPath.append(c);
                }
            }
        }

        if (currentPath.length() > 0) {
            paths.add(new IdShortPath(currentPath.toString()));
        }

        return paths;
    }

    public sealed interface GenericPath permits IdShortPath, IndexPath {
    }

    public record IdShortPath(String idShort) implements GenericPath {
    }

    public record IndexPath(int index) implements GenericPath {
    }
}
