package com.forex.forexpredictor.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


//TODO switch currency from string to List<String>
@Document(collection = "user")
public class User {

    @Id
    private String id;
    @Indexed(unique = true, direction = IndexDirection.DESCENDING, dropDups = true)
    private String fullname;
    private String email;
    private String password;
    private List<String> subscribedCurrencyPairs;
    @DBRef
    private Set<Role> roles;

    public User() {
        this.subscribedCurrencyPairs = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<String> getSubscribedCurrencyPairs() {
        return this.subscribedCurrencyPairs;
    }

    public void addCurrencyPair(String currencyPair) {
        if(findCurrencyPair(currencyPair) == null)
            this.subscribedCurrencyPairs.add(currencyPair);
    }

    public void removeCurrencyPair(String currencyPair) {
        if(findCurrencyPair(currencyPair) != null) {
            this.subscribedCurrencyPairs.remove(currencyPair);
        }
    }

    private String findCurrencyPair(String currencyPair) {
        for(String cp: this.subscribedCurrencyPairs) {
            if(cp.equals(currencyPair))
                return cp;
        }

        return null;
    }

    public boolean isPremium() {
        boolean premium = false;
        for(Role role: this.roles) {
            if (role.getRole().equals("PREMIUM"))
                premium = true;
        }
        return premium;
    }
}
