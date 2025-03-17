package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2.dto;
import jakarta.persistence.*;
import org.eclipse.digitaltwin.aas4j.v3.model.Key;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;

@Entity
@Table(name = "keys")
public class KeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Storing enum as a string for readability
    private KeyTypes type;

    @Column(name = "key_value")
    private String value;

    public KeyEntity() {}

    public KeyEntity(Key key) {
        this.type = key.getType();
        this.value = key.getValue();
    }

    public KeyEntity(KeyTypes type, String value) {
        this.type = type;
        this.value = value;
    }

    public DefaultKey toDefaultKey() {
        DefaultKey key = new DefaultKey();
        key.setType(this.type);
        key.setValue(this.value);
        return key;
    }

    // Getters and Setters
    public KeyTypes getType() {
        return type;
    }

    public void setType(KeyTypes type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
