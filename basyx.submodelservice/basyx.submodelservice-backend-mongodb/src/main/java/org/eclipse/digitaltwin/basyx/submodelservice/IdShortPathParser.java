package org.eclipse.digitaltwin.basyx.submodelservice;

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

    public static final record IdShortPath(String idShort) implements GenericPath {
    }

    public static final record IndexPath(int index) implements GenericPath {
    }
}
