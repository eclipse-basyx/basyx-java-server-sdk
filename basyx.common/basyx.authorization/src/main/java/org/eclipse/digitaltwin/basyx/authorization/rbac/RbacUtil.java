package org.eclipse.digitaltwin.basyx.authorization.rbac;

import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.UncheckedIOException;

import static org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig.DEFAULT_RULES_FILE;
import static org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig.RULES_FILE_KEY;

public class RbacUtil {
    public static RbacRuleSet getRbacRuleSetFromFile(Environment environment) {
        try {
            return new RbacRuleSetDeserializer().fromFile(environment.getProperty(RULES_FILE_KEY, DEFAULT_RULES_FILE));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
