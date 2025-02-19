package org.eclipse.digitaltwin.basyx.submodelrepository;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.common.backend.inmemory.core.InMemoryCrudRepository;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.filerepository.InMemoryFileRepository;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.backend.SubmodelBackend;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelService;
import org.eclipse.digitaltwin.basyx.submodelservice.SubmodelServiceFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
public class InMemorySubmodelRepositoryBackend extends InMemoryCrudRepository<Submodel> implements SubmodelBackend {

    private final SubmodelServiceFactory submodelServiceFactory;

    public InMemorySubmodelRepositoryBackend(SubmodelServiceFactory factory) {
        super(Submodel::getId);
        this.submodelServiceFactory = factory;
    }

    public static InMemorySubmodelRepositoryBackend buildDefault() {
        return new InMemorySubmodelRepositoryBackend(new InMemorySubmodelServiceFactory(new InMemoryFileRepository()));
    }

    @Override
    public CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException {
        return getSubmodelService(submodelId).getSubmodelElements(pInfo);
    }

    @Override
    public SubmodelElement getSubmodelElement(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException {
        return getSubmodelService(submodelId).getSubmodelElement(smeIdShortPath);
    }

    @Override
    public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
        return getSubmodelService(submodelId).getSubmodelElementValue(smeIdShort);
    }

    @Override
    public void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException {
        doWithService(submodelId, s -> s.setSubmodelElementValue(smeIdShort, value));
    }

    @Override
    public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
        doWithService(submodelId, s -> s.createSubmodelElement(smElement));
    }

    @Override
    public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
        doWithService(submodelId, s -> s.createSubmodelElement(idShortPath, smElement));
    }

    @Override
    public void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException {
        doWithService(submodelId, s -> s.updateSubmodelElement(idShortPath, submodelElement));
    }

    @Override
    public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
        doWithService(submodelId, s -> s.deleteSubmodelElement(idShortPath));
    }

    @Override
    public void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList) {
        doWithService(submodelId, s -> s.patchSubmodelElements(submodelElementList));
    }

    private SubmodelService getSubmodelService(String submodelId) {
        return findById(submodelId).map(submodelServiceFactory::create).orElseThrow(() -> new ElementDoesNotExistException(submodelId));
    }

    private void doWithService(String smId, Consumer<SubmodelService> consumer) {
        SubmodelService smService = getSubmodelService(smId);
        consumer.accept(smService);
        save(smService.getSubmodel());
    }
    
}
