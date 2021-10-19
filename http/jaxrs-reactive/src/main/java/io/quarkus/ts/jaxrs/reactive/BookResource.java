package io.quarkus.ts.jaxrs.reactive;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/custom-book")
public class BookResource {

    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Book getBook() {
        Book book = new Book();
        book.title = "Title!";
        return book;
    }
}
