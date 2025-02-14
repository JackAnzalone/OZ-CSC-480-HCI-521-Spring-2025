package oz.rest;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.*;

import jdk.javadoc.doclet.Reporter;
import org.bson.types.ObjectId;

import java.io.IOException;

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
                return Response.status(Response.Status.NOT_FOUND).entity("Returned Json was null. Check quote ID is correct").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Error: Invalid ObjectID format").build();
        }
    }

    @GET
    @Path("/search/query/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response advancedSearch(@PathParam("query") String query) {
        try{
            String result = mongo.searchQuote(query);
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Exception Occured: "+e+" you have an obligation to annoy engine team").build();
        }
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createQuote(String rawJson) {
        try{
            //map json to Java Object
            ObjectMapper objectMapper = new ObjectMapper();
            QuoteObject quote = objectMapper.readValue(rawJson, QuoteObject.class);

            ObjectId newQuoteId = mongo.createQuote(quote); //add to mongo database

            if(newQuoteId != null) {
                return Response.ok(newQuoteId.toHexString()).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong. Returned QuoteID null. Check json is formatted Correctly").build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("Exception occured: "+e).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateQuote(String rawJson) {
        try{

            //Map json to Java Object
            ObjectMapper objectMapper = new ObjectMapper();

            QuoteObject quote = objectMapper.readValue(rawJson, QuoteObject.class);

            boolean updated = mongo.updateQuote(quote);

            if(updated) {
                return Response.ok("Quote updated successfully").build();
            } else {
                return Response.status(Response.Status.CONFLICT).entity("Error updating quote, Json could be wrong or is missing quote ID").build();
            }
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("IOException: "+e).build();
        }
    }

    @DELETE
    @Path("/delete/{quoteId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteQuote(@PathParam("quoteId") String quoteID) {
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

    @GET
    @Path("/topBookmarked")
    public Response getTopBookmarks() {
        try{
            String result = mongo.getTopBookmarked();
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Exception Occurred: "+e+": pester engine team").build();
        }
    }

    @GET
    @Path("/topShared")
    public Response getSharedBookmarked() {
        try{
            String result = mongo.getTopShared();
            return Response.ok(result).build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Exception Occurred: "+e+": pester engine team").build();
        }
    }
}
