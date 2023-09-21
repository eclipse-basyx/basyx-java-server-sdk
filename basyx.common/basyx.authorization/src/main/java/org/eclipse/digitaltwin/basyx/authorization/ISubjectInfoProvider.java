package org.eclipse.digitaltwin.basyx.authorization;

import java.util.Optional;

public interface ISubjectInfoProvider {
    public ISubjectInfo<?> get();
}
