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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.backend;
import jakarta.persistence.*;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto.SpecificAssetIdEntity;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "aas_discovery_document")
public class AasDiscoveryDocumentEntity {
    @Id
    private String shellIdentifier;

    @ElementCollection
    @CollectionTable(name = "aas_asset_links", joinColumns = @JoinColumn(name = "aas_discovery_document_id"))
    private Set<AssetLink> assetLinks;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "aas_discovery_document_id")
    private List<SpecificAssetIdEntity> specificAssetIds;

    public AasDiscoveryDocumentEntity() {}

    public AasDiscoveryDocumentEntity(String shellIdentifier, Set<AssetLink> assetLinks, List<SpecificAssetIdEntity> specificAssetIds) {
        this.shellIdentifier = shellIdentifier;
        this.assetLinks = assetLinks;
        this.specificAssetIds = specificAssetIds;
    }

    public String getShellIdentifier() {
        return shellIdentifier;
    }

    public Set<AssetLink> getAssetLinks() {
        return assetLinks;
    }

    public List<SpecificAssetIdEntity> getSpecificAssetIds() {
        return specificAssetIds;
    }
}
