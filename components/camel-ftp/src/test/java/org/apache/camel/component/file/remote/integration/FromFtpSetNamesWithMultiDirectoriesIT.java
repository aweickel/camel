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
package org.apache.camel.component.file.remote.integration;

import java.io.File;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.converter.IOConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test to verify that using option setNames and having multi remote directories the files are stored locally in
 * the same directory layout.
 */
public class FromFtpSetNamesWithMultiDirectoriesIT extends FtpServerTestSupport {

    private String getFtpUrl() {
        return "ftp://admin@localhost:{{ftp.server.port}}"
               + "/incoming?password=admin&binary=true&recursive=true&initialDelay=0&delay=100";
    }

    @Test
    public void testFtpRoute() throws Exception {
        NotifyBuilder notify = new NotifyBuilder(context).whenDone(2).create();
        MockEndpoint resultEndpoint = getMockEndpoint("mock:result");
        resultEndpoint.expectedMessageCount(2);

        prepareFtpServer();

        context.getRouteController().startRoute("foo");

        assertMockEndpointsSatisfied();
        assertTrue(notify.matchesWaitTime());

        Exchange ex = resultEndpoint.getExchanges().get(0);
        byte[] bytes = ex.getIn().getBody(byte[].class);
        assertTrue(bytes.length > 10000, "Logo size wrong");

        // assert the file
        File file = testFile("data1/logo1.jpeg").toFile();
        assertTrue(file.exists(), "The binary file should exists");
        assertTrue(file.length() > 10000, "Logo size wrong");

        // assert the file
        file = testFile("data2/logo2.png").toFile();
        assertTrue(file.exists(), " The binary file should exists");
        assertTrue(file.length() > 50000, "Logo size wrong");
    }

    private void prepareFtpServer() throws Exception {
        // prepares the FTP Server by creating a file on the server that we want
        // to unit
        // test that we can pool and store as a local file
        String ftpUrl = "ftp://admin@localhost:{{ftp.server.port}}/incoming/data1/?password=admin&binary=true";
        Endpoint endpoint = context.getEndpoint(ftpUrl);
        Exchange exchange = endpoint.createExchange();
        exchange.getIn().setBody(IOConverter.toFile("src/test/data/ftpbinarytest/logo1.jpeg"));
        exchange.getIn().setHeader(Exchange.FILE_NAME, "logo1.jpeg");
        Producer producer = endpoint.createProducer();
        producer.start();
        producer.process(exchange);
        producer.stop();

        ftpUrl = "ftp://admin@localhost:{{ftp.server.port}}/incoming/data2/?password=admin&binary=true";
        endpoint = context.getEndpoint(ftpUrl);
        exchange = endpoint.createExchange();
        exchange.getIn().setBody(IOConverter.toFile("src/test/data/ftpbinarytest/logo2.png"));
        exchange.getIn().setHeader(Exchange.FILE_NAME, "logo2.png");
        producer = endpoint.createProducer();
        producer.start();
        producer.process(exchange);
        producer.stop();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from(getFtpUrl()).routeId("foo").noAutoStartup().to(fileUri(), "mock:result");
            }
        };
    }
}
