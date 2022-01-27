package com.example.hp.teacher;

public class UsersModel {
   public String name;
   public String image;
    public String wall_image;
    public String country;



   public UsersModel()
    {

    }
    public UsersModel(String name, String image, String wall_image,String country) {
        this.name = name;
        this.image = image;
        this.wall_image = wall_image;
        this.country=country;


    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getUser_country(){return country;}

    public void setUser_country(String user_country){this.country=country;}


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWall_image() {
        return wall_image;
    }

    public void setWall_image(String wall_image) {
        this.wall_image = wall_image;
    }




}
