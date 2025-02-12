package oz.rest;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class MongoUtil {
    private static final String CONNECTION_STRING = "mongodb+srv://database_admin:pXzO2cMkmk7LXVCH@csc480cluster.ldmco.mongodb.net/";
    private static final String DATABASE_NAME = "Data";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    static {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        database = mongoClient.getDatabase(DATABASE_NAME);
    }

    public static MongoDatabase getDatabase() {
        return database;
    }

    public String getQuote(ObjectId quoteID) {

        MongoCollection<Document> collection = database.getCollection("Quotes");
        Document query = new Document("_id", quoteID);
        Document result = collection.find(query).first();

        return (result != null) ? result.toJson() : null;
    }

    private QuoteObject parseQuote(String jsonQuote) {
        //parses json from string form to Java Object
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            QuoteObject quote = objectMapper.readValue(jsonQuote, QuoteObject.class);
            return quote;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateQuote(ObjectId quoteId, QuoteObject quote) {
        MongoCollection<Document> collection = database.getCollection("Quotes");

        // get current quote for reference
        String originalQuote = getQuote(quote.getId());
        QuoteObject ogQuote = parseQuote(originalQuote);

        try {
            Document IdQuery = new Document();
            IdQuery.append("_id", quote.getId());

            //document containing data to update
            Document newData = new Document();

            //manually doing this right now
            //look into automatic ways
            //skipping blank fields
            if(quote.getAuthor() != null && !quote.getAuthor().isEmpty()) {
                newData.append("author", quote.getAuthor());
            }
            if(quote.getText() != null && !quote.getText().isEmpty()) {
                newData.append("quote", quote.getText());
            }
            if(quote.getBookmarks() >= 0) {
                newData.append("bookmarks", quote.getBookmarks());
            }
            if(quote.getShares() >= 0) {
                newData.append("shares", quote.getShares());
            }
            if(quote.getDate() >= 0) {
                newData.append("date", quote.getDate());
            }


            //create update operation
            Document updateOperation = new Document("$set", newData);

            long modifiedCount = collection.updateOne(IdQuery, updateOperation).getModifiedCount();
            return modifiedCount > 0;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuote(ObjectId quoteId) {
        MongoCollection<Document> collection = database.getCollection("Quotes");
        try {
            Document idQuery = new Document();
            idQuery.append("_id", quoteId);

            long deletedCount = collection.deleteOne(idQuery).getDeletedCount();
            return deletedCount > 0;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ObjectId createQuote(QuoteObject quoteData) {
        try{
            MongoCollection<Document> collection = database.getCollection("Quotes");
            //give quote a new id
            quoteData.setId(new ObjectId());

            Document quoteDoc = new Document()
                    .append("_id", quoteData.getId())
                    .append("author", quoteData.getAuthor())
                    .append("quote", quoteData.getText())
                    .append("bookmarks", quoteData.getBookmarks())
                    .append("shares", quoteData.getShares())
                    .append("date", quoteData.getDate());

            collection.insertOne(quoteDoc);
            return quoteData.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ObjectId[] getTopBookmarked(){
        MongoCollection<Document> collection = database.getCollection("Quotes");

    }

    public ObjectId[] getTopShared() {
        MongoCollection<Document> collection = database.getCollection("Quotes");
    }

    public static void close() {
        if(mongoClient != null) {
            mongoClient.close();
        }
    }
}
