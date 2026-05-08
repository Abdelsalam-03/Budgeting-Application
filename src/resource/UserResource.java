package resource;

public class UserResource {

    private int id;
    private String name;

    // Full constructor
    public UserResource(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }
}