/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.core;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a Message for HTTP Result in the standardized format.
 * @author fried
 */
public class ResultMessage {

    private final String message;
    private final int code;
    private final String correlationId;
    private final MessageType messageType;

    public ResultMessage(String message, int code, String correlationId, MessageType messageType){
        this.message = message;
        this.code = code;
        this.correlationId = correlationId;
        this.messageType = messageType;
    }

    public Object build() {
        Map<String, Object> body = new HashMap<>();
        String timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        body.put("timestamp", timestamp);
        body.put("messageType", this.messageType);
        body.put("code", this.code);
        body.put("text", this.message);
        body.put("correlationId", this.correlationId);
        return body;
    }
}
