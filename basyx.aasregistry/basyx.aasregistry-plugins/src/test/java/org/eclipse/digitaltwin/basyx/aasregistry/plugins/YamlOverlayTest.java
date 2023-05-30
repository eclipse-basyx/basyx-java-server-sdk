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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class YamlOverlayTest {

	@Test
	public void testSimpleOverlay() throws IOException, MojoExecutionException {
		new TestRunner().testOverlay("/org/eclipse/digitaltwin/basyx/aasregistry/plugins/simple");
	}

	@Test
	public void testArrayOverlay() throws MojoExecutionException, IOException {
		new TestRunner().testOverlay("/org/eclipse/digitaltwin/basyx/aasregistry/plugins/listinstructions");
	}

	private static class TestRunner {

		public void testOverlay(String path) throws IOException, MojoExecutionException {
			try (BufferedReader baseIn = buildReader(path, "base");
					BufferedReader overlayIn = buildReader(path, "overlay");
					BufferedReader expectedIn = buildReader(path, "expected")) {
				Yaml yaml = new Yaml();
				Map<String, Object> base = yaml.load(baseIn);
				Map<String, Object> overlay = yaml.load(overlayIn);
				Map<String, Object> expected = yaml.load(expectedIn);
				Map<String, Object> result = new HashMap<>();
				YamlOverlay overlayer = new YamlOverlay();
				overlayer.doOverlay(base, overlay, result);

				Assert.assertEquals(expected, result);
			}
		}
		
		private BufferedReader buildReader(String path, String name) {
			InputStream resourceStream = TestRunner.class.getResourceAsStream(path + "/" + name + ".yaml");
			InputStreamReader resourceReader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
			return new BufferedReader(resourceReader);
		}
		
		private BufferedReader buildWriter(String path, String name) {
			InputStream resourceStream = TestRunner.class.getResourceAsStream(path + "/" + name + ".yaml");
			InputStreamReader resourceReader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8);
			return new BufferedReader(resourceReader);
		}
	}
}
