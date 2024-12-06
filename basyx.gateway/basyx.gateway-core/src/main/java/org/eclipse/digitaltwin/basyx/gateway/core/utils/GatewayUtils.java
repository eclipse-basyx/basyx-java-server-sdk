/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.gateway.core.utils;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.client.ConnectedAasRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.RepositoryRegistryLinkException;
import org.eclipse.digitaltwin.basyx.gateway.core.DefaultGateway;
import org.eclipse.digitaltwin.basyx.gateway.core.exception.BaSyxComponentNotHealthyException;
import org.eclipse.digitaltwin.basyx.gateway.core.exception.RegistryUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class GatewayUtils {
    private Logger logger = LoggerFactory.getLogger(GatewayUtils.class);


    public static boolean isRegistryDefined(String aasRegistry) {
        return aasRegistry != null;
    }

    public void validateRepository(String aasRepository) throws BaSyxComponentNotHealthyException {
        if (aasRepository == null) {
            throw new UnsupportedOperationException("No AAS Repository configured");
        }
        throwExceptionIfIsUnhealthyBaSyxRepository(aasRepository);
    }

    public void validateRegistry(String aasRegistry) throws BaSyxComponentNotHealthyException {
        throwExceptionIfIsUnhealthyBaSyxRegistry(aasRegistry);
    }

    public void handleRegistryLinkException(ConnectedAasRepository aasRepo, AssetAdministrationShell aas, String aasRepository, String aasRegistry, RepositoryRegistryLinkException e) throws RegistryUnavailableException {
        logger.error("Unable to link AAS {} with registry {}. Rolling back...", aas.getId(), aasRegistry);
        aasRepo.deleteAas(aas.getId());
        logger.error("Rollback in AAS Repository {} completed.", aasRepository);
        throw new RegistryUnavailableException("Unable to link AAS with registry. Changes in AAS Repository rolled back.");
    }


    public void throwExceptionIfIsUnhealthyBaSyxRepository(String componentURL) {
        componentURL = formatURL(componentURL);

        if (isBaSyxComponent(componentURL,"/shells") && !isHealthy(componentURL)) {
            throw new BaSyxComponentNotHealthyException(componentURL + " is not healthy");
        }
    }

    public void throwExceptionIfIsUnhealthyBaSyxRegistry(String componentURL) {
        componentURL = formatURL(componentURL);

        if (isBaSyxComponent(componentURL,"/shell-descriptors") && !isHealthy(componentURL)) {
            throw new BaSyxComponentNotHealthyException(componentURL + " is not healthy");
        }
    }

    public String formatURL(String componentURL) {
        try {
            URL url = new URL(componentURL);
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();
            if (port == -1) { // no port specified, use default port
                port = url.getDefaultPort();
            }
            componentURL = protocol + "://" + host + ":" + port;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return componentURL;
    }

    public boolean isBaSyxComponent(String componentURL, String endpointToCheck) {
        try {
            HttpURLConnection connection = getRequest(componentURL, "/shells");

            String aasMiddleware = connection.getHeaderField("aas_middleware");

            return "BaSyx".equals(aasMiddleware);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean isHealthy(String componentURL){
        try {
            HttpURLConnection connection = getRequest(componentURL, "/actuator/health");

            String body = new String(connection.getInputStream().readAllBytes());

            return connection.getResponseCode() == 200 && body.contains("UP");

        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public static HttpURLConnection getRequest(String componentURL, String path) throws IOException {
        URL url = new URL(componentURL + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }
}
