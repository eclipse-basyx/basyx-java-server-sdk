package org.eclipse.digitaltwin.basyx.core.pagination;

import java.util.List;
import java.util.function.Function;

public class PaginationUtilities {
	
	public static String resolveCursor(PaginationInfo pRequest, List<String> foundDescriptors) {
	    return resolveCursor(pRequest, foundDescriptors, Function.identity());
	}

	public static <T> String resolveCursor(PaginationInfo pRequest, List<T> foundDescriptors, Function<T, String> idResolver) {

		if (foundDescriptors.isEmpty() || !pRequest.isPaged())
			return null;

		T last = foundDescriptors.get(foundDescriptors.size() - 1);

		return idResolver.apply(last);
	}

}
