package main.java.com.ridesharing.models;

// Abstract User class
abstract class User {
    protected int id;
    protected String name;

    protected User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

