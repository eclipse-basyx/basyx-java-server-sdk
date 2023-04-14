package org.eclipse.digitaltwin.basyx.conceptdescriptionservice;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.ConceptDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;

/**
 * Specifies the overall {@link ConceptDescriptionService} API
 * 
 * @author danish
 *
 */
public interface ConceptDescriptionService {
	/**
	 * Retrieves the ConceptDescription contained in the service
	 * 
	 * @return
	 */
	public ConceptDescription getConceptDescription();
	
	public List<Reference> getIsCaseOf();
	
	public void setIsCaseOf(List<Reference> isCaseOf);

}
