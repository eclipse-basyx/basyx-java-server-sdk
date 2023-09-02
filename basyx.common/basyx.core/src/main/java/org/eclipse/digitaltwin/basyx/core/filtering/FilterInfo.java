package org.eclipse.digitaltwin.basyx.core.filtering;

public class FilterInfo<T> {
    private final T filter;
    public T getFilter() {
        return filter;
    }

    public FilterInfo(T filter) {
        this.filter = filter;
    }
}
