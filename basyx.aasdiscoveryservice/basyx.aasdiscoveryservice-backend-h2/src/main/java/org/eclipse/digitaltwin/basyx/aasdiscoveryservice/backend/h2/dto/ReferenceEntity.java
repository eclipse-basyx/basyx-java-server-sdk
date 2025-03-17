package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto;
import jakarta.persistence.*;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;

import java.util.List;

@Entity
@Table(name = "references")
public class ReferenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reference_keys")
    private List<KeyEntity> keys;

    public ReferenceEntity() {}

    public ReferenceEntity(List<KeyEntity> keys) {
        this.keys = keys;
    }

    public List<KeyEntity> getKeys() {
        return keys;
    }

    public void setKeys(List<KeyEntity> keys) {
        this.keys = keys;
    }

    public Reference toReference() {
        List<Key> keyList = keys.stream()
                .map(KeyEntity::toDefaultKey)  // Convert KeyEntity to DefaultKey
                .map(key -> (Key) key) // Explicitly cast to Key interface
                .toList();

        return new DefaultReference.Builder().keys(keyList).build();
    }

}
