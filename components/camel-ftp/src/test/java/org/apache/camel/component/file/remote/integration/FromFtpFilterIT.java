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

import org.apache.camel.BindToRegistry;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileFilter;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

/**
 * Unit test to test filter option.
 */
public class FromFtpFilterIT extends FtpServerTestSupport {

    @BindToRegistry("myFilter")
    private MyFileFilter filter = new MyFileFilter<>();

    protected String getFtpUrl() {
        return "ftp://admin@localhost:{{ftp.server.port}}/filter?password=admin&binary=false&filter=#myFilter";
    }

    @Test
    public void testFilterFiles() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(0);

        sendFile(getFtpUrl(), "This is a file to be filtered", "skipme.txt");

        mock.setResultWaitTime(3000);
        mock.assertIsSatisfied();
    }

    @Test
    public void testFilterFilesWithARegularFile() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.expectedBodiesReceived("Hello World");

        sendFile(getFtpUrl(), "This is a file to be filtered", "skipme.txt");

        sendFile(getFtpUrl(), "Hello World", "hello.txt");

        mock.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from(getFtpUrl()).to("mock:result");
            }
        };
    }

    // START SNIPPET: e1
    public class MyFileFilter<T> implements GenericFileFilter<T> {
        @Override
        public boolean accept(GenericFile<T> file) {
            // we don't accept any files starting with skip in the name
            return !file.getFileName().startsWith("skip");
        }
    }
    // END SNIPPET: e1
}
