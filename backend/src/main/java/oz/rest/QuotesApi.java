package oz.rest;


import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.*;

import org.bson.types.ObjectId;

@Path("/quotes")
public class QuotesApi {

    @Inject
    MongoUtil mongo;

    @GET
    @Path("/search/{quoteID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response stringSearch(@PathParam("quoteID") String quoteID) {
        try {
            ObjectId objectId = new ObjectId(quoteID);
            String jsonQuote = mongo.getQuote(objectId);

            if(jsonQuote != null) {
                return Response.ok(jsonQuote).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Something went wrong, and I'm not sure why yet").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid ObjectID format").build();
        }
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createQuote(QuoteObject quote) {
        try{
            ObjectId newQuoteId = mongo.createQuote(quote);

            if(newQuoteId != null) {
                return Response.ok(newQuoteId).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong. Ping the engine team until they fix it").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Exception occured: "+e).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateQuote(QuoteObject quote) {
        try{
            ObjectId objectId = quote.getId();
            boolean updated = mongo.updateQuote(objectId, quote);

            if(updated) {
                return Response.ok("Quote updated successfully").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Error updating quote").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request").build();
        }
    }

    @DELETE
    @Path("/delete/{quoteId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteQuote(@PathParam("quoteID") String quoteID) {
        try{
            ObjectId objectId = new ObjectId(quoteID);
            boolean result = mongo.deleteQuote(objectId);
            if(result) {
                return Response.ok("Quote successfully deleted").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Quote not found, could not be deleted").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid request").build();
        }
    }
}
