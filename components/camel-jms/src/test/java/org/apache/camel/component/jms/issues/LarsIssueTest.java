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
package org.apache.camel.component.jms.issues;

import javax.jms.ConnectionFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.AbstractJMSTest;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.camel.component.jms.JmsComponent.jmsComponentAutoAcknowledge;
import static org.apache.camel.test.infra.activemq.common.ConnectionFactoryHelper.createConnectionFactory;

/**
 * Lets test that a number of headers MQSeries doesn't like to be sent are excluded when forwarding a JMS message from
 * one destination to another
 */
public class LarsIssueTest extends AbstractJMSTest {
    private static final Logger LOG = LoggerFactory.getLogger(LarsIssueTest.class);

    @Test
    public void testSendSomeMessages() throws Exception {
        MockEndpoint endpoint = getMockEndpoint("mock:results");
        String body1 = "Hello world!";
        String body2 = "Goodbye world!";
        endpoint.expectedBodiesReceived(body1, body2);

        template.sendBody("activemq:queue:LarsIssueTest.bar", body1);
        template.sendBody("activemq:queue:LarsIssueTest.bar", body2);

        assertMockEndpointsSatisfied();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();

        ConnectionFactory connectionFactory
                = createConnectionFactory(service);
        camelContext.addComponent("activemq", jmsComponentAutoAcknowledge(connectionFactory));

        return camelContext;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                Processor myProcessor = e -> LOG.info(">>>> Received exchange: " + e);

                // lets enable CACHE_CONSUMER so that the consumer stays around in JMX
                // as the default due to the spring bug means we keep creating & closing consumers
                from("activemq:queue:LarsIssueTest.bar?cacheLevelName=CACHE_CONSUMER")
                        .process(myProcessor)
                        .to("mock:results");
            }
        };
    }
}
