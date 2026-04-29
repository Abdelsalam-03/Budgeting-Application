package manager;

import java.util.ArrayList;

import model.User;
import resource.UserResource;

import core.observer.*;

public class AuthenticationManager implements Observable {

    private static AuthenticationManager instance ;
    private UserResource authenticatedUser;

    private final ArrayList<Observer> observers;
    
    private AuthenticationManager() {
        observers = new ArrayList<>();
    }
    @Override
    public void addObserver(Observer o){
        observers.add(o);
    }
    @Override
    public void removeObserver(Observer o){
        observers.remove(o);
    }
    @Override
    public void notifyObservers(){
        observers.forEach((o) -> {
            o.update();
        });
    }
    
    public static AuthenticationManager getAuthenticationManager() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    public boolean logIn(String email, String password){
        try {
            User user = new User(email);
            if (password.equals(user.password)) {
                System.out.println("Before setting user");
                this.authenticatedUser = new UserResource(user.id, user.name);
                System.out.println("After setting user");
                this.notifyObservers();
//                setUser(new UserResource(user.id, user.name));
            } else {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            throw e;
        }
        return true;
    }

    public boolean signUp(String name, String email, String password){
        try {
            User user = User.create(name, email, password);
            setUser(new UserResource(user.id, user.name));
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("Email is already exists");
        }
        return true;
    }

    public boolean logout(){
        try {
            setUser(null);
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("Exception Occurred");
        }
        return true;
    }

    public UserResource getUser(){
        if (authenticatedUser == null) {
            // throw exception
            throw new RuntimeException("No logged in user");
        }
        return authenticatedUser;
    }
    
    private void setUser(UserResource user){
        authenticatedUser = user;
        this.notifyObservers();
    }

}