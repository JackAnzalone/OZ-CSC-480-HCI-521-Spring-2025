package oz.rest;

import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;

public class QuoteObject {
    @BsonId
    private ObjectId id;
    @BsonProperty("author")
    private String author;
    @BsonProperty("quote")
    private String text;
    @BsonProperty("bookmarks")
    private int bookmarks = -1;
    @BsonProperty("shares")
    private int shares = -1;
    @BsonProperty("date")
    private int date = -1;

    //getters
    public ObjectId getId() {return id;}
    public String getAuthor() {return author;}
    public String getText() {return  text;}
    public int getBookmarks() {return bookmarks;}
    public int getShares() {return shares;}
    public int getDate() {return date;}

    //setters
    public void setId(ObjectId id) {this.id = id;}
    public void setAuthor(String author) {this.author = author;}
    public void setText(String text) {this.text = text;}
    public void setBookmarks(int bookmarks) {this.bookmarks = bookmarks;}
    public void setShares(int shares) {this.shares = shares;}
    public void setDate(int date) {this.date = date;}
}
