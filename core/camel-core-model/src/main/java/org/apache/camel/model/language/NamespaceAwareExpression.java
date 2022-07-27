/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.model.language;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.camel.model.PropertyDefinition;
import org.apache.camel.spi.NamespaceAware;
import org.apache.camel.spi.annotations.DslProperty;

/**
 * A useful base class for any expression which may be namespace or XML content aware such as {@link XPathExpression} or
 * {@link XQueryExpression}
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class NamespaceAwareExpression extends ExpressionDefinition implements NamespaceAware {

    @XmlElement(name = "namespace")
    private List<PropertyDefinition> namespace;
    @XmlTransient
    private Map<String, String> namespaces;

    public NamespaceAwareExpression() {
    }

    public NamespaceAwareExpression(String expression) {
        super(expression);
    }

    @Override
    public Map<String, String> getNamespaces() {
        return getNamespaceAsMap();
    }

    /**
     * Injects the XML Namespaces of prefix -> uri mappings
     *
     * @param namespaces the XML namespaces with the key of prefixes and the value the URIs
     */
    @Override
    public void setNamespaces(Map<String, String> namespaces) {
        this.namespaces = namespaces;
    }

    public List<PropertyDefinition> getNamespace() {
        return namespace;
    }

    /**
     * Injects the XML Namespaces of prefix -> uri mappings
     */
    public void setNamespace(List<PropertyDefinition> namespace) {
        this.namespace = namespace;
    }

    public Map<String, String> getNamespaceAsMap() {
        if (namespaces == null && namespace != null) {
            namespaces = new HashMap<>();
        }
        if (namespace != null) {
            for (PropertyDefinition def : namespace) {
                namespaces.put(def.getKey(), def.getValue());
            }
        }
        return namespaces;
    }

}
