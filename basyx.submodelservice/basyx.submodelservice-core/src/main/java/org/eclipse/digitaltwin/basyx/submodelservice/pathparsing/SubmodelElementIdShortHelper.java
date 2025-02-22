package org.eclipse.digitaltwin.basyx.submodelservice.pathparsing;

public class SubmodelElementIdShortHelper {
	
	/**
	 * Check whether the given idShortPath is a simple idShort or a path of idShorts
	 * 
	 * @param idShortPath
	 * @return true if the given idShortPath is a path of IdShorts
	 */
	public static boolean isNestedIdShortPath(String idShortPath) {
		return hasDot(idShortPath)|| hasOpeningBrackets(idShortPath);
	}

	/**
	 * Extract the idShort of the direct parent (submodel element collection) of the
	 * nested submodel element
	 * 
	 * @param idShortPath
	 *            - A path to the nested target submodel element
	 * @return idShort of the direct parent of the nested submodel element
	 */
	public static String extractDirectParentSubmodelElementCollectionIdShort(String idShortPath) {
		return idShortPath.substring(0, idShortPath.lastIndexOf("."));
		
	}
	
	/**
	 * Extract the idShort of the direct parent (submodel element list) of the
	 * nested submodel element
	 * 
	 * @param idShortPath
	 *            - A path to the nested target submodel element
	 * @return idShort of the direct parent of the nested submodel element
	 */
	public static String extractDirectParentSubmodelElementListIdShort(String idShortPath) {
		if(hasOpeningBrackets(idShortPath) && hasDot(idShortPath)) {
			int indexDot = idShortPath.lastIndexOf(".");
			int indexBracket = idShortPath.lastIndexOf("[");
			if(indexDot > indexBracket)  return idShortPath.substring(0, indexDot);
			
			return idShortPath.substring(0, indexBracket);
		}
		
		return idShortPath.substring(0, idShortPath.lastIndexOf("["));
	}
	
	public static boolean isDirectParentASubmodelElementList(String idShortPath) {
		if(hasOpeningBrackets(idShortPath) && hasDot(idShortPath)) {
			int indexDot = idShortPath.lastIndexOf(".");
			int indexBracket = idShortPath.lastIndexOf("[");
			return indexDot < indexBracket;
		}
		if(hasOpeningBrackets(idShortPath)) return true;
		return false;
	}

	private static boolean hasDot(String idShortPath) {
		return idShortPath.contains(".");
	}
	
	private static boolean hasOpeningBrackets(String idShortPath) {
		return idShortPath.contains("[");
	}

}
