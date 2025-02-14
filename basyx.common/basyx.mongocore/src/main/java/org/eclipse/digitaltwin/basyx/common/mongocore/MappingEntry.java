package org.eclipse.digitaltwin.basyx.common.mongocore;

public interface MappingEntry {

    String getCollectionName();

    Class<?> getEntityClass();


    public static MappingEntry of(String collectionName, Class<?> entityClass) {
        return new MappingEntry() {
            @Override
            public String getCollectionName() {
                return collectionName;
            }

            @Override
            public Class<?> getEntityClass() {
                return entityClass;
            }
        };
    }
    
}
