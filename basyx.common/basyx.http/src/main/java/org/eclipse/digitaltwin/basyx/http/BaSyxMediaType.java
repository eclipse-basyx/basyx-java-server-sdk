/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.http;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

public final class BaSyxMediaType {

	private BaSyxMediaType() {
	}

	public static MediaType parseOrOctetStream(String mediaType) {
		if (mediaType == null || mediaType.isBlank()) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}

		try {
			return MediaType.parseMediaType(mediaType);
		} catch (IllegalArgumentException e) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	/**
	 * Parses the given media type or infers a media type from the file name when
	 * the given media type is missing, invalid, or {@code application/octet-stream}.
	 *
	 * @param mediaType the media type to parse
	 * @param fileName the file name to infer a media type from
	 * @return the parsed, inferred, or {@code application/octet-stream} media type
	 */
	public static String parseOrInferFromFileNameOrOctetStream(String mediaType, String fileName) {
		if (isSpecificMediaType(mediaType)) {
			return mediaType;
		}

		if (fileName == null || fileName.isBlank()) {
			return MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}

		return MediaTypeFactory.getMediaType(fileName)
				.map(MediaType::toString)
				.orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	private static boolean isSpecificMediaType(String mediaType) {
		if (mediaType == null || mediaType.isBlank()) {
			return false;
		}

		try {
			MediaType parsedMediaType = MediaType.parseMediaType(mediaType);
			return !isApplicationOctetStream(parsedMediaType);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private static boolean isApplicationOctetStream(MediaType mediaType) {
		return MediaType.APPLICATION_OCTET_STREAM.getType().equals(mediaType.getType())
				&& MediaType.APPLICATION_OCTET_STREAM.getSubtype().equals(mediaType.getSubtype());
	}

}
