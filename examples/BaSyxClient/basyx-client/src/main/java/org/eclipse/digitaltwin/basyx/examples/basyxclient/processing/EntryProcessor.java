package org.eclipse.digitaltwin.basyx.examples.basyxclient.processing;

import org.eclipse.digitaltwin.basyx.examples.basyxclient.model.MotorEntry;

public interface EntryProcessor {
    void process(MotorEntry entry);
}
