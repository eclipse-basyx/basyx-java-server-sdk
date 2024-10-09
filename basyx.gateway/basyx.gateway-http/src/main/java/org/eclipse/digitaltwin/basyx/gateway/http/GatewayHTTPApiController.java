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

package org.eclipse.digitaltwin.basyx.gateway.http;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.gateway.core.exception.BaSyxComponentNotHealthyException;
import org.eclipse.digitaltwin.basyx.gateway.core.Gateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(name = "basyx.gateway.aas-repository")
public class GatewayHTTPApiController implements GatewayHTTPApi{
    @Value("${basyx.gateway.aas-repository:#{null}}")
    public String aasRepositoryURL;
    @Value("${basyx.gateway.submodel-repository:#{null}}")
    public String submodelRepositoryURL;
    @Value("${basyx.gateway.aas-registry:#{null}}")
    public String aasRegistryURL;
    @Value("${basyx.gateway.submodel-registry:#{null}}")
    public String submodelRegistryURL;

    private final Gateway gateway;

    public GatewayHTTPApiController(Gateway gateway){
        this.gateway = gateway;
    }

    @Override
    public ResponseEntity<?> postAssetAdministrationShell(AssetAdministrationShell body, String aasRepositoryURL, String aasRegistryURL) {
        if(!isAnyURLSet(aasRepositoryURL, this.aasRepositoryURL)){
           return ResponseEntity.badRequest().body("No AAS Repository URL set");
        }
        try {
            gateway.createAAS(body, aasRepositoryURL == null ? this.aasRepositoryURL : aasRepositoryURL, aasRegistryURL);
        }catch(BaSyxComponentNotHealthyException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        }
        return new ResponseEntity<AssetAdministrationShell>(body, HttpStatus.CREATED);
    }

    private static boolean isAnyURLSet(String url1, String url2) {
        return url1 != null || url2 != null;
    }


}
