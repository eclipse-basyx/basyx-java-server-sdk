package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto;
import jakarta.persistence.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSpecificAssetId;

@Entity
@Table(name = "specific_asset_ids")
public class SpecificAssetIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "specificassetid_value")
    private String value;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "external_subject_id")
    private ReferenceEntity externalSubjectId;

    public SpecificAssetIdEntity() {}

    public SpecificAssetIdEntity(DefaultSpecificAssetId specificAssetId) {
        this.name = specificAssetId.getName();
        this.value = specificAssetId.getValue();
        this.externalSubjectId = specificAssetId.getExternalSubjectId() != null
                ? new ReferenceEntity(specificAssetId.getExternalSubjectId().getKeys().stream()
                .map(KeyEntity::new)
                .toList()) : null;
    }

    public DefaultSpecificAssetId toSpecificAssetId() {
        DefaultSpecificAssetId specificAssetId = new DefaultSpecificAssetId();
        specificAssetId.setName(this.name);
        specificAssetId.setValue(this.value);
        if (this.externalSubjectId != null) {
            specificAssetId.setExternalSubjectId(this.externalSubjectId.toReference());
        }
        return specificAssetId;
    }

}
