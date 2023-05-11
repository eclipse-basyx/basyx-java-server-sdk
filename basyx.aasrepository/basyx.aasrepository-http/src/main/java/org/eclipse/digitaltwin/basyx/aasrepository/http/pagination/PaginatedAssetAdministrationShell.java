package org.eclipse.digitaltwin.basyx.aasrepository.http.pagination;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.core.exceptions.CursorNotFoundException;

public class PaginatedAssetAdministrationShell {

	private List<AssetAdministrationShell> assetAdministrationShells;
	private Integer limit;
	private String cursor;

	public PaginatedAssetAdministrationShell(List<AssetAdministrationShell> assetAdministrationShells, Integer limit,
			String cursor) {
		super();
		this.assetAdministrationShells = assetAdministrationShells;
		this.limit = limit;
		this.cursor = cursor;
	}

	public List<AssetAdministrationShell> getAssetAdministrationShells() {
		return assetAdministrationShells;
	}

	public Integer getLimit() {
		return limit;
	}

	public String getCursor() {
		return cursor;
	}

	public List<AssetAdministrationShell> getPaginatedAdministrationShells() {
		if (assetAdministrationShells.isEmpty())
			return this.assetAdministrationShells;
		
		int start = 0;

		if (cursor != null && !cursor.isBlank())
			start = getNextIndexFromCursor();

		int end = Math.min(start + limit, assetAdministrationShells.size());

		throwIfCursorNotExists(start, end);

		return assetAdministrationShells.subList(start, end);
	}

	private void throwIfCursorNotExists(int start, int end) {
		if (start <= end)
			return;
		
		throw new CursorNotFoundException(cursor);
	}

	private int getNextIndexFromCursor() {
		int index = 0;

		for (AssetAdministrationShell assetAdministrationShell : assetAdministrationShells) {
			if (assetAdministrationShell.getId().equals(cursor))
				return ++index;

			index++;
		}

		return ++index;
	}

}
