/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.component.java;

import java.nio.file.Path;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gerhard Sonnenberg DFKI GmbH
 */
@Configuration
@ConfigurationProperties(prefix = "basyx.operation.java")
public class JavaInvokableDefinition {

	private Path sourcesPath;
	private Path classesPath;
	private List<Path> additionalClasspath = List.of();
	
	public Path getSourcesPath() {
		return sourcesPath;
	}

	public void setSourcesPath(Path sourcesPath) {
		this.sourcesPath = sourcesPath;
	}

	public Path getClassesPath() {
		return classesPath;
	}

	public void setClassesPath(Path classesPath) {
		this.classesPath = classesPath;
	}

	public List<Path> getAdditionalClasspath() {
		return additionalClasspath;
	}

	public void setAdditionalClasspath(List<Path> additionalClasspath) {
		this.additionalClasspath = additionalClasspath;
	}

}
