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
package org.eclipse.digitaltwin.basyx.aasregistry.plugins.path;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.digitaltwin.basyx.aasregistry.plugins.path.PathInfo.ComplexRangeRelationInfo;
import org.eclipse.digitaltwin.basyx.aasregistry.plugins.path.PathInfo.ConstantInfo;
import org.eclipse.digitaltwin.basyx.aasregistry.plugins.path.PathInfo.ModelInfo;
import org.eclipse.digitaltwin.basyx.aasregistry.plugins.path.PathInfo.PrimitiveRangeRelationInfo;
import org.eclipse.digitaltwin.basyx.aasregistry.plugins.path.PojoClassVisitor.PojoRelation.PojoRelationType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathInfoGenerator {

	private final Class<?> cls;

	public PathInfo generate() {
		PathInfo info = new PathInfo();
		info.setInputClassPackageName(cls.getPackageName());
		PojoClassVisitorDelegate delegate = new PojoClassVisitorDelegate(new ConstantFiller(info), new ModelFiller(info));
		PojoWalkCycleDetector visitor = new PojoWalkCycleDetector(delegate);
		PojoClassWalker walker = new PojoClassWalker(cls, visitor);
		walker.walkClass();
		return info;
	}

	private static class ModelFiller extends AbstractPojoClassVisitor {

		private Set<ModelInfo> innerModels = new TreeSet<>(Comparator.comparing(ModelInfo::getName));
		private Map<String, ModelInfo> lookupModels = new HashMap<>();

		private Set<String> rootRanges = new HashSet<>();
		private Set<String> innerRanges = new HashSet<>();
		private Set<String> foundSubTypes = new HashSet<>();
		private Map<String, List<String>> typeSubTypeMapping = new HashMap<>();
		private final PathInfo pathInfo;

		public ModelFiller(PathInfo info) {
			this.pathInfo = info;
			info.setPrimitiveRanges(new TreeSet<>());
			info.setModels(innerModels);
		}

		@Override
		public void startRelation(PojoRelation relation) {
			String subject = relation.getSubject();
			ModelInfo mSubject = lookupModels.get(subject);
			if (relation.isComplex()) {
				ComplexRangeRelationInfo info = new ComplexRangeRelationInfo(relation.getMethodName(), relation.getFieldName(), relation.getRange(), relation.getType() == PojoRelationType.LIST);
				mSubject.getComplexRangeRelations().add(info);
				if (relation.isRootRelation()) {
					rootRanges.add(relation.getRange());
				} else {
					innerRanges.add(relation.getRange());
				}
			} else {
				PrimitiveRangeRelationInfo info = new PrimitiveRangeRelationInfo(relation.getMethodName(), relation.getFieldName(), relation.getRange(), relation.getType() == PojoRelationType.LIST);
				mSubject.getPrimitiveRangeRelations().add(info);
			}
		}

		@Override
		public void onSubTypeRelation(String base, List<String> subTypes) {
			foundSubTypes.addAll(subTypes);
			typeSubTypeMapping.put(base, subTypes);
		}

		@Override
		public boolean startType(String name, boolean isRoot) {
			ModelInfo current = new ModelInfo(name);
			current.setInfo(pathInfo);
			if (isRoot) {
				pathInfo.setRootModel(current);
			} else {
				innerModels.add(current);
			}
			lookupModels.put(name, current);
			current.setPrimitiveRangeRelations(new LinkedList<>());
			current.setComplexRangeRelations(new LinkedList<>());
			return true;
		}

		@Override
		public void stop() {
			for (ModelInfo eachInfo : innerModels) {
				String modelName = eachInfo.getName();
				if (foundSubTypes.contains(modelName) || rootRanges.contains(modelName)) {
					eachInfo.setSinglePathConstructor(modelName);
				}
				if (innerRanges.contains(modelName)) {
					eachInfo.setPathAndSegmentConstructor(modelName);
				}
			}
			for (Entry<String, List<String>> eachMapping : typeSubTypeMapping.entrySet()) {
				ModelInfo info = lookupModels.get(eachMapping.getKey());
				info.setSubModels(eachMapping.getValue());
			}
			Set<ModelInfo> allModels = new TreeSet<>(Comparator.comparing(ModelInfo::getName));
			allModels.addAll(lookupModels.values());
			this.pathInfo.setAllModels(allModels);
		}
	}

	private static class ConstantFiller extends AbstractPojoClassVisitor {

		private Set<ConstantInfo> constants = new TreeSet<>(Comparator.comparing(ConstantInfo::getName));

		public ConstantFiller(PathInfo info) {
			info.setConstants(constants);
		}

		@Override
		public void startRelation(PojoRelation relation) {
			constants.add(new ConstantInfo(relation.getFieldName()));
		}
	}
}