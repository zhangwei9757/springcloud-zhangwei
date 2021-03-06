/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.adapter.quarkus.jaxrs.deployment;


import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author sea
 */
@Path("/test")
public class TestResource {

    ExecutorService executor = Executors.newFixedThreadPool(5);

    @Path("/hello")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String sayHello() {
        return "Hello!";
    }

    @Path("/async-hello")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public void asyncSayHello(@Suspended final AsyncResponse asyncResponse) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                asyncResponse.resume("Hello!");
            }
        });
    }

    @Path("/hello/{name}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String sayHelloWithName(@PathParam(value = "name") String name) {
        return "Hello " + name + " !";
    }

    @Path("/ex")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String exception() {
        throw new RuntimeException("test exception mapper");
    }

    @Path("/400")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String badRequest() {
        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                .entity("test return 400")
                .build());
    }

    @Path("/delay/{seconds}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public String delay(@PathParam(value = "seconds") long seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
        return "finish";
    }
}
