package com.drewweise.provisions.user.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
/**
 * Represents a user account in Provisions.
 * @author dweise
 */
public class User {

    /** The user's database userId. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "cognito_user_id", nullable = false, unique = true)
    private String cognitoUserId;

    /** Required by Hibernate. */
    public User() {}

    /** Creates a user associated with a Cognito account. */
    public User(String cognitoUserId) {this.cognitoUserId = cognitoUserId;}

    /** @return the user's ID */
    public int getUserId() {
        return userId;
    }

    /** Gets the user's cognitoUserId */
    public String getCognitoUserId() {return cognitoUserId;}

}
