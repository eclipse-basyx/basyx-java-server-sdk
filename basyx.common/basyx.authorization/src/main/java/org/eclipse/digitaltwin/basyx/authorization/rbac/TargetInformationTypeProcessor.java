package org.eclipse.digitaltwin.basyx.authorization.rbac;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Set;

public class TargetInformationTypeProcessor {

    public static Object getImplementation(String type) {
        Reflections reflections = new Reflections("org.eclipse.digitaltwin.basyx", Scanners.TypesAnnotated);

        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(TargetInformationSubtype.class);

        for (Class<?> clazz : annotatedClasses) {
            TargetInformationSubtype annotation = clazz.getAnnotation(TargetInformationSubtype.class);
            if (annotation != null && annotation.getValue().equals(type)) {
                try {
                    return clazz.getDeclaredConstructor().newInstance(); // Create instance
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null; // or throw an exception if not found
    }
}
