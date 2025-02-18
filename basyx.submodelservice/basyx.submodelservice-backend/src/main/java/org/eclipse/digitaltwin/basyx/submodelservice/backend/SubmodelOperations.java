package org.eclipse.digitaltwin.basyx.submodelservice.backend;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

public interface SubmodelOperations {
    
	CursorResult<List<SubmodelElement>> getSubmodelElements(String submodelId, PaginationInfo pInfo) throws ElementDoesNotExistException;

	SubmodelElement getSubmodelElement(String submodelId, String smeIdShortPath) throws ElementDoesNotExistException;

	SubmodelElementValue getSubmodelElementValue(String submodelId, String smeIdShort) throws ElementDoesNotExistException ;

	void setSubmodelElementValue(String submodelId, String smeIdShort, SubmodelElementValue value) throws ElementDoesNotExistException ;

	void createSubmodelElement(String submodelId, SubmodelElement smElement) ;

	void createSubmodelElement(String submodelId, String idShortPath, SubmodelElement smElement) throws ElementDoesNotExistException ;

	void updateSubmodelElement(String submodelId, String idShortPath, SubmodelElement submodelElement) throws ElementDoesNotExistException ;

	void deleteSubmodelElement(String submodelId, String idShortPath) throws ElementDoesNotExistException ;

	void patchSubmodelElements(String submodelId, List<SubmodelElement> submodelElementList);

}
