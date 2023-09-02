package org.eclipse.digitaltwin.basyx;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.CollidingIdentifierException;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelValueOnly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Repository decorator for the authorization on the submodel level.
 * 
 * @author wege
 */
public class AuthorizationSubmodelRepository implements SubmodelRepository {
	private static Logger logger = LoggerFactory.getLogger(AuthorizationSubmodelRepository.class);

	private SubmodelRepository decorated;

	public AuthorizationSubmodelRepository(SubmodelRepository decorated) {
		this.decorated = decorated;
	}

	@Override
	public Collection<Submodel> getAllSubmodels() {
		return decorated.getAllSubmodels();
	}

	@Override
	public Submodel getSubmodel(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodel(submodelId);
	}

	@Override
	public void updateSubmodel(String submodelId, Submodel submodel) throws ElementDoesNotExistException {
		decorated.updateSubmodel(submodelId, submodel);
	}

	@Override
	public void createSubmodel(Submodel submodel) throws CollidingIdentifierException {
		decorated.createSubmodel(submodel);
	}

	@Override
	public void deleteSubmodel(String submodelId) throws ElementDoesNotExistException {
		Submodel submodel = decorated.getSubmodel(submodelId);
		decorated.deleteSubmodel(submodelId);
	}

	@Override
	public Collection<SubmodelElement> getSubmodelElements(String submodelId) throws ElementDoesNotExistException {
		return decorated.getSubmodelElements(submodelId);
	}

	@Override
	public SubmodelElement getSubmodelElement(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElement(submodelId, smeIdShort);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException {
		return decorated.getSubmodelElementValue(submodelId, smeIdShort);
	}

	@Override
	public void setSubmodelElementValue(String submodelId, String idShortPath, SubmodelElementValue value) throws ElementDoesNotExistException {
		decorated.setSubmodelElementValue(submodelId, idShortPath, value);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public void createSubmodelElement(String submodelId, SubmodelElement smElement) {
		decorated.createSubmodelElement(submodelId, smElement);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, smElement.getIdShort());
	}

	@Override
	public void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException {
		decorated.createSubmodelElement(submodelId, smElement);
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException {
		SubmodelElement submodelElement = decorated.getSubmodelElement(submodelId, idShortPath);
		decorated.deleteSubmodelElement(submodelId, idShortPath);
	}

	@Override
	public String getName() {
		return decorated.getName();
	}

	@Override
	public SubmodelValueOnly getSubmodelByIdValueOnly(String submodelId) {
		return decorated.getSubmodelByIdValueOnly(submodelId);
	}

	@Override
	public Submodel getSubmodelByIdMetadata(String submodelId) {
		return decorated.getSubmodelByIdMetadata(submodelId);
	}

}
