package com.example.pma.model;

public class UserResponse {
    //@SerializedName("id")
    //@SerializedName("firstname")
    //@SerializedName("lastname")
    //@SerializedName("email")
    // @SerializedName("username")
    private String username;
    private String firstname;
    private String  lastname;
    private String email;
    private Integer id;

    public UserResponse(){
    }

    public UserResponse(String username, String firstname, String lastname, String email, Integer id) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
