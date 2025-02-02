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
package org.apache.camel.component.jms;

import java.util.concurrent.TimeUnit;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

public class JmsSuspendResumeTest extends AbstractPersistentJMSTest {

    @Test
    public void testSuspendResume() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:JmsSuspendResumeTest");
        mock.expectedBodiesReceived("Hello World");

        template.sendBody("activemq:queue:JmsSuspendResumeTest", "Hello World");

        assertMockEndpointsSatisfied();

        context.getRouteController().suspendRoute("JmsSuspendResumeTest");

        resetMocks();
        mock.expectedMessageCount(0);

        // sleep a bit to ensure its properly suspended
        Thread.sleep(2000);

        template.sendBody("activemq:queue:JmsSuspendResumeTest", "Bye World");

        assertMockEndpointsSatisfied(1, TimeUnit.SECONDS);

        resetMocks();
        mock.expectedBodiesReceived("Bye World");

        context.getRouteController().resumeRoute("JmsSuspendResumeTest");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("activemq:queue:JmsSuspendResumeTest").routeId("JmsSuspendResumeTest").to("mock:JmsSuspendResumeTest");
            }
        };
    }
}
