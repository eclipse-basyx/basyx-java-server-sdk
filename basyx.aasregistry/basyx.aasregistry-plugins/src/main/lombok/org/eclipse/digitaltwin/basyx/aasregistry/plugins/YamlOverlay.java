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
package org.eclipse.digitaltwin.basyx.aasregistry.plugins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.yaml.snakeyaml.Yaml;

@Mojo(name = "yaml-overlay", defaultPhase = LifecyclePhase.INITIALIZE)
public class YamlOverlay extends AbstractMojo {

	@Parameter(defaultValue = "${project}", required = true, readonly = true)
	private MavenProject project;

	@Parameter(property = "base")
	private File base;

	@Parameter(property = "overlay")
	private File overlay;

	@Parameter(property = "out")
	private File out;

	@Parameter(property = "charset", defaultValue = "UTF-8")
	private String charSet;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try (BufferedReader bReaderBase = new BufferedReader(new FileReader(base, Charset.forName(charSet)));
				BufferedReader bReaderOverlay = new BufferedReader(new FileReader(overlay, Charset.forName(charSet)));
				BufferedWriter bWriterOut = new BufferedWriter(new FileWriter(out, Charset.forName(charSet)))) {
			execute(bReaderBase, bReaderOverlay, bWriterOut);
		} catch (IOException e) {
			throw new MojoExecutionException("Failed to combine files", e);
		}
	}

	private void execute(BufferedReader bReaderBase, BufferedReader bReaderOverlay, BufferedWriter bWriterOut) throws MojoExecutionException {
		Yaml yaml = new Yaml();
		Map<String, Object> baseContent = yaml.load(bReaderBase);
		Map<String, Object> overlayContent = yaml.load(bReaderOverlay);
		Map<String, Object> target = new LinkedHashMap<>();
		doOverlay(baseContent, overlayContent, target);
		yaml.dump(target, bWriterOut);
	}

	void doOverlay(Map<String, Object> baseContent, Map<String, Object> overlayContent, Map<String, Object> target) throws MojoExecutionException {
		if (overlayContent != null &&  overlayContent.isEmpty()) {
			// means always erase for now
			return;
		}
		
		for (Entry<String, Object> eachBaseEntry : baseContent.entrySet()) {
			doOverlay(eachBaseEntry, overlayContent, target);
		}
		// for now we just append the remaining entries
		overlayContent.forEach(target::put);
	}

	@SuppressWarnings("unchecked")
	private void doOverlay(Entry<String, Object> eachBaseEntry, Map<String, Object> overlayContent, Map<String, Object> target) throws MojoExecutionException {
		String key = eachBaseEntry.getKey();
		Object baseValue = eachBaseEntry.getValue();
		Object overlayValue = overlayContent.remove(key);
		if (overlayValue != null) {
			Class<?> baseClass = baseValue.getClass();
			Class<?> overlayClass = overlayValue.getClass();
			if (ListProcessInstruction.isListProcessInstruction(baseValue, overlayValue)) {
				ListProcessInstruction instructions = ListProcessInstruction.readInstructions(this, overlayValue);
				List<Object> targetList = new ArrayList<>();
				target.put(key, targetList);
				instructions.apply((List<Object>) baseValue, targetList);
			} else if (baseClass != overlayClass) {
				throw new MojoExecutionException("Value conflict: Base: " + baseClass + " Overlay: " + overlayClass);
			} else if (baseValue instanceof Map) {
				Map<String, Object> targetMap = new LinkedHashMap<>();
				target.put(key, targetMap);
				doOverlay((Map<String, Object>) baseValue, (Map<String, Object>) overlayValue, targetMap);
			} else {
				// replace everything for primitive values and lists
				target.put(key, overlayValue);
			}
		} else {
			target.put(key, baseValue);
		}
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public void setBase(File base) {
		this.base = base;
	}

	public void setOverlay(File overlay) {
		this.overlay = overlay;
	}

	public void setOut(File out) {
		this.out = out;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	private static class ListProcessInstruction {

		private static final String OVERLAY = "overlay";
		private static final String REMOVE = "remove";
		private static final String REPLACE = "replace";
		private static final String LIST_PROCESS_INSTRUCTIONS = "$LIST_PROCESS_INSTRUCTION$";

		private final Map<Integer, Object> toReplace = new HashMap<>();
		private final List<Integer> toRemove = new ArrayList<>();
		private final Map<Integer, Map<String, Object>> toOverlay = new HashMap<>();

		private final YamlOverlay overlayer;

		@SuppressWarnings("unchecked")
		private ListProcessInstruction(YamlOverlay overLayer, Map<String, Object> instructions) throws MojoExecutionException {
			this.overlayer = overLayer;
			if (instructions.containsKey(REPLACE)) {
				toReplace.putAll((Map<Integer, Object>) instructions.get(REPLACE));
			}
			if (instructions.containsKey(REMOVE)) {
				toRemove.addAll((List<Integer>) instructions.get(REMOVE));
			}
			if (instructions.containsKey(OVERLAY)) {
				for (Entry<Integer, Object> eachEntry : ((Map<Integer, Object>) instructions.get(OVERLAY)).entrySet()) {
					Integer pos = eachEntry.getKey();
					Object value = eachEntry.getValue();
					if (!(value instanceof Map)) {
						throw new MojoExecutionException("Overlay instruction just works with map values");
					}
					toOverlay.put(pos, (Map<String, Object>) eachEntry.getValue());
				}
			}
		}

		public void apply(List<Object> baseValue, List<Object> target) throws MojoExecutionException {
			int pos = 0;
			for (Object eachValue : baseValue) {
				applyEach(eachValue, target, pos);
				pos++;
			}
		}
		
		private void applyEach(Object eachValue, List<Object> target, int pos) throws MojoExecutionException {
			if (toReplace.containsKey(pos)) {
				target.add(toReplace.get(pos));
			} else if (toOverlay.containsKey(pos)) {
				Object elem = toOverlay.get(pos);
				applyOverlay(eachValue, elem, target);
			} else if (!toRemove.contains(pos)) {
				target.add(eachValue);
			}
		}
		
		@SuppressWarnings("unchecked")
		private void applyOverlay(Object eachValue, Object elem, List<Object> target) throws MojoExecutionException {
			if (eachValue instanceof Map) {
				Map<String, Object> targetMap = new LinkedHashMap<>();
				target.add(targetMap);
				overlayer.doOverlay((Map<String, Object>) eachValue, (Map<String, Object>) elem, targetMap);
			} else { // if not a map -> just do overlay for now
				target.add(elem);
			}
		}

		@SuppressWarnings("unchecked")
		public static ListProcessInstruction readInstructions(YamlOverlay overlayer, Object overlayValue) throws MojoExecutionException {
			Map<String, Object> instructions = (Map<String, Object>) overlayValue;
			Map<String, Object> instructionMap = (Map<String, Object>) instructions.get(LIST_PROCESS_INSTRUCTIONS);
			return new ListProcessInstruction(overlayer, instructionMap);
		}

		@SuppressWarnings("unchecked")
		private static boolean isListProcessInstruction(Object baseValue, Object overlayValue) {
			if (!(baseValue instanceof List && overlayValue instanceof Map)) {
				return false;
			}
			Map<String, Object> overlayMap = (Map<String, Object>) overlayValue;
			return overlayMap.size() == 1 && overlayMap.containsKey(LIST_PROCESS_INSTRUCTIONS);
		}
	}
}