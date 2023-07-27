package org.eclipse.digitaltwin.basyx.core.pagination;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaginationSupport<T extends Object> {

	private final TreeMap<String, T> sortedMap;
	private final Function<T, String> idResolver;

	public PaginationSupport(TreeMap<String, T> sortedMap, Function<T, String> idResolver) {
		this.sortedMap = sortedMap;
		this.idResolver = idResolver;
	}

	public CursorResult<List<T>> getPaged(PaginationInfo pInfo) {
		Map<String, T> cursorView = getCursorView(pInfo);
		Stream<Entry<String, T>> eStream = cursorView.entrySet()
				.stream();

		Stream<T> tStream = eStream.map(Entry::getValue);
		tStream = applyLimit(pInfo, tStream);

		List<T> resultList = tStream.collect(Collectors.toList());
		String cursor = computeNextCursor(resultList);
		return new CursorResult<>(cursor, resultList);
	}

	private Stream<T> applyLimit(PaginationInfo info, Stream<T> aStream) {
		if (info.hasLimit()) {
			return aStream.limit(info.getLimit());
		}
		return aStream;
	}

	private String computeNextCursor(List<T> list) {
		if (!list.isEmpty()) {
			T last = list.get(list.size() - 1);
			String lastId = idResolver.apply(last);
			return sortedMap.higherKey(lastId);
		}
		return null;
	}

	private Map<String, T> getCursorView(PaginationInfo info) {
		if (info.hasCursor()) {
			return sortedMap.tailMap(info.getCursor());
		} else {
			return sortedMap;
		}
	}

}
