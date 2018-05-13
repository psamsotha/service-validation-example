package com.example;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class ServiceValidationTest extends JerseyTest {


    public static class Model {
        @NotNull
        private String value;

        public Model(String value) {
            this.value = value;
        }

        @NotNull
        public String getValue() {
            return this.value;
        }
    }

    @Validated
    public static class Service {

        public String getValue(@Valid Model model) {
            return model.value;
        }
    }

    @Path("test")
    public static class TestResource {

        @Inject
        private Service service;

        @GET
        @Path("invalid")
        public String getInvalid() {
            return service.getValue(new Model(null));
        }

        @GET
        @Path("valid")
        public String getValid() {
            return service.getValue(new Model("Hello World"));
        }
    }

    @Override
    public ResourceConfig configure() {
        return new ResourceConfig()
                .register(TestResource.class)
                .register(new ExceptionMapper<Throwable>() {
                    @Override
                    public Response toResponse(Throwable t) {
                        t.printStackTrace();
                        return Response.serverError().entity(t.getMessage()).build();
                    }
                })

                .register(ValidationFeature.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bindAsContract(Service.class).in(Singleton.class);
                    }
                });
    }

    @Test
    public void testInvalid() {
        Response res = target("test/invalid").request().get();

        assertThat(res.getStatus()).isEqualTo(500);
        res.close();
    }

    @Test
    public void testValid() {
        Response res = target("test/valid").request().get();

        assertThat(res.getStatus()).isEqualTo(200);
        assertThat(res.readEntity(String.class)).isEqualTo("Hello World");
    }
}
