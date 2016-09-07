package tomerbu.edu.firebaseupdatechildrenandondisconnect.models;

public class Post extends BaseModel{

    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;


    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }
}