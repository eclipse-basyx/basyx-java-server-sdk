/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.BsonRegularExpression;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ShellDescriptorQuery.QueryTypeEnum;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.mongodb.SegmentBlocksBuilder.SegmentBlock;
import org.springframework.data.mongodb.core.query.Criteria;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchPathCriteriaBuilder {

	private final Map<String, String> pathMappings;

	public Criteria buildSearchPathCriteria(String searchPath, String value, QueryTypeEnum queryType) {
		SegmentBlocksBuilder sbBuilder = new SegmentBlocksBuilder(pathMappings);
		List<SegmentBlock> blockSegments = sbBuilder.buildSegmentBlocks(searchPath);
		CriteriaBuilder converter = new CriteriaBuilder(value, queryType, blockSegments);
		return converter.buildCriteria();
	}

	@RequiredArgsConstructor
	private static class CriteriaBuilder {

		private final String value;
		private final QueryTypeEnum queryType;
		private final List<SegmentBlock> segments;

		public Criteria buildCriteria() {
			return buildCriteriaRecursively(segments.iterator());
		}

		private Criteria buildCriteriaRecursively(Iterator<SegmentBlock> segmentIter) {
			SegmentBlock segmentBlock = segmentIter.next();
			String segment = segmentBlock.getSegment();
			Criteria criteria = Criteria.where(segment);

			if (segmentBlock.isListLeaf()) {
				if (queryType == QueryTypeEnum.REGEX) {
					return Criteria.where(segment).in(new BsonRegularExpression(value));
				}
				return  Criteria.where(segment).in(value);
			} else if (segmentBlock.isLeaf()) {
				if (queryType == QueryTypeEnum.REGEX) {
					return criteria.regex(value);
				}
				return criteria.is(value);
			} else {
				Criteria innerCriteria = buildCriteriaRecursively(segmentIter);
				return criteria.elemMatch(innerCriteria);
			}
		}
	}
}