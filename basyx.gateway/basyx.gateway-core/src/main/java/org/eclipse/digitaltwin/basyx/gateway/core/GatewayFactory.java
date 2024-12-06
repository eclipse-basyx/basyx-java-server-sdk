package org.eclipse.digitaltwin.basyx.gateway.core;

import org.eclipse.digitaltwin.basyx.gateway.core.Gateway;

/**
 * Factory interface to create a Gateway instance
 *
 * @author fried
 */
public interface GatewayFactory {
    public Gateway create();

}
