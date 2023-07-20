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

package org.eclipse.digitaltwin.basyx.submodelregistry.client.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataSpecificationIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LevelType;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ValueList;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.JSON;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-07-18T15:02:01.566475800+02:00[Europe/Berlin]")
@JsonDeserialize(using = DataSpecificationContent.DataSpecificationContentDeserializer.class)
@JsonSerialize(using = DataSpecificationContent.DataSpecificationContentSerializer.class)
public class DataSpecificationContent extends AbstractOpenApiSchema {
    private static final Logger log = Logger.getLogger(DataSpecificationContent.class.getName());

    public static class DataSpecificationContentSerializer extends StdSerializer<DataSpecificationContent> {
        public DataSpecificationContentSerializer(Class<DataSpecificationContent> t) {
            super(t);
        }

        public DataSpecificationContentSerializer() {
            this(null);
        }

        @Override
        public void serialize(DataSpecificationContent value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeObject(value.getActualInstance());
        }
    }

    public static class DataSpecificationContentDeserializer extends StdDeserializer<DataSpecificationContent> {
        public DataSpecificationContentDeserializer() {
            this(DataSpecificationContent.class);
        }

        public DataSpecificationContentDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public DataSpecificationContent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode tree = jp.readValueAsTree();
            Object deserialized = null;
            boolean typeCoercion = ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS);
            int match = 0;
            JsonToken token = tree.traverse(jp.getCodec()).nextToken();
            // deserialize DataSpecificationIec61360
            try {
                boolean attemptParsing = true;
                // ensure that we respect type coercion as set on the client ObjectMapper
                if (DataSpecificationIec61360.class.equals(Integer.class) || DataSpecificationIec61360.class.equals(Long.class) || DataSpecificationIec61360.class.equals(Float.class) || DataSpecificationIec61360.class.equals(Double.class) || DataSpecificationIec61360.class.equals(Boolean.class) || DataSpecificationIec61360.class.equals(String.class)) {
                    attemptParsing = typeCoercion;
                    if (!attemptParsing) {
                        attemptParsing |= ((DataSpecificationIec61360.class.equals(Integer.class) || DataSpecificationIec61360.class.equals(Long.class)) && token == JsonToken.VALUE_NUMBER_INT);
                        attemptParsing |= ((DataSpecificationIec61360.class.equals(Float.class) || DataSpecificationIec61360.class.equals(Double.class)) && token == JsonToken.VALUE_NUMBER_FLOAT);
                        attemptParsing |= (DataSpecificationIec61360.class.equals(Boolean.class) && (token == JsonToken.VALUE_FALSE || token == JsonToken.VALUE_TRUE));
                        attemptParsing |= (DataSpecificationIec61360.class.equals(String.class) && token == JsonToken.VALUE_STRING);
                    }
                }
                if (attemptParsing) {
                    deserialized = tree.traverse(jp.getCodec()).readValueAs(DataSpecificationIec61360.class);
                    // TODO: there is no validation against JSON schema constraints
                    // (min, max, enum, pattern...), this does not perform a strict JSON
                    // validation, which means the 'match' count may be higher than it should be.
                    match++;
                    log.log(Level.FINER, "Input data matches schema 'DataSpecificationIec61360'");
                }
            } catch (Exception e) {
                // deserialization failed, continue
                log.log(Level.FINER, "Input data does not match schema 'DataSpecificationIec61360'", e);
            }

            if (match == 1) {
                DataSpecificationContent ret = new DataSpecificationContent();
                ret.setActualInstance(deserialized);
                return ret;
            }
            throw new IOException(String.format("Failed deserialization for DataSpecificationContent: %d classes match result, expected 1", match));
        }

        /**
         * Handle deserialization of the 'null' value.
         */
        @Override
        public DataSpecificationContent getNullValue(DeserializationContext ctxt) throws JsonMappingException {
            throw new JsonMappingException(ctxt.getParser(), "DataSpecificationContent cannot be null");
        }
    }

    // store a list of schema names defined in oneOf
    public static final Map<String, Class<?>> schemas = new HashMap<>();

    public DataSpecificationContent() {
        super("oneOf", Boolean.FALSE);
    }

    public DataSpecificationContent(DataSpecificationIec61360 o) {
        super("oneOf", Boolean.FALSE);
        setActualInstance(o);
    }

    static {
        schemas.put("DataSpecificationIec61360", DataSpecificationIec61360.class);
        JSON.registerDescendants(DataSpecificationContent.class, Collections.unmodifiableMap(schemas));
        // Initialize and register the discriminator mappings.
        Map<String, Class<?>> mappings = new HashMap<String, Class<?>>();
        mappings.put("DataSpecificationIec61360", DataSpecificationIec61360.class);
        mappings.put("DataSpecificationContent", DataSpecificationContent.class);
        JSON.registerDiscriminator(DataSpecificationContent.class, "modelType", mappings);
    }

    @Override
    public Map<String, Class<?>> getSchemas() {
        return DataSpecificationContent.schemas;
    }

    /**
     * Set the instance that matches the oneOf child schema, check
     * the instance parameter is valid against the oneOf child schemas:
     * DataSpecificationIec61360
     *
     * It could be an instance of the 'oneOf' schemas.
     * The oneOf child schemas may themselves be a composed schema (allOf, anyOf, oneOf).
     */
    @Override
    public void setActualInstance(Object instance) {
        if (JSON.isInstanceOf(DataSpecificationIec61360.class, instance, new HashSet<Class<?>>())) {
            super.setActualInstance(instance);
            return;
        }

        throw new RuntimeException("Invalid instance type. Must be DataSpecificationIec61360");
    }

    /**
     * Get the actual instance, which can be the following:
     * DataSpecificationIec61360
     *
     * @return The actual instance (DataSpecificationIec61360)
     */
    @Override
    public Object getActualInstance() {
        return super.getActualInstance();
    }

    /**
     * Get the actual instance of `DataSpecificationIec61360`. If the actual instance is not `DataSpecificationIec61360`,
     * the ClassCastException will be thrown.
     *
     * @return The actual instance of `DataSpecificationIec61360`
     * @throws ClassCastException if the instance is not `DataSpecificationIec61360`
     */
    public DataSpecificationIec61360 getDataSpecificationIec61360() throws ClassCastException {
        return (DataSpecificationIec61360)super.getActualInstance();
    }



  /**
   * Convert the instance into URL query string.
   *
   * @return URL query string
   */
  public String toUrlQueryString() {
    return toUrlQueryString(null);
  }

  /**
   * Convert the instance into URL query string.
   *
   * @param prefix prefix of the query string
   * @return URL query string
   */
  public String toUrlQueryString(String prefix) {
    String suffix = "";
    String containerSuffix = "";
    String containerPrefix = "";
    if (prefix == null) {
      // style=form, explode=true, e.g. /pet?name=cat&type=manx
      prefix = "";
    } else {
      // deepObject style e.g. /pet?id[name]=cat&id[type]=manx
      prefix = prefix + "[";
      suffix = "]";
      containerSuffix = "]";
      containerPrefix = "[";
    }

    StringJoiner joiner = new StringJoiner("&");

    if (getActualInstance() instanceof DataSpecificationIec61360) {
        if (getActualInstance() != null) {
          joiner.add(((DataSpecificationIec61360)getActualInstance()).toUrlQueryString(prefix + "one_of_0" + suffix));
        }
        return joiner.toString();
    }
    return null;
  }

}

