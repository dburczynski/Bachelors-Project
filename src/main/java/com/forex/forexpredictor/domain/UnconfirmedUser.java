package com.forex.forexpredictor.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "unconfirmed_user")
public class UnconfirmedUser extends User {

    private Date dateCreated;

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }
}
