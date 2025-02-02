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
package org.apache.camel.component.jetty;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

/**
 * Unit test with a simple route test.
 */
public class JettyContentBasedRouteTest extends BaseJettyTest {

    private String serverUri = "http://localhost:" + getPort() + "/myservice";

    @Test
    public void testSendOne() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:one");

        mock.expectedHeaderReceived("one", "true");

        template.requestBody(serverUri + "?one=true", null, Object.class);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSendOther() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:other");

        mock.expectedHeaderReceived("two", "true");

        template.requestBody(serverUri + "?two=true", null, Object.class);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                // START SNIPPET: e1
                from("jetty:" + serverUri).choice().when().simple("${header.one}").to("mock:one").otherwise().to("mock:other");
                // END SNIPPET: e1
            }
        };
    }

}
