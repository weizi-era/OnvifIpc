package com.example.onvifipc.bean;

public class User {

    private int id;
    private String userName;
    private int localPermissions;
    private int remotePermissions;

    public User(int id, String userName, int localPermissions, int remotePermissions) {
        this.id = id;
        this.userName = userName;
        this.localPermissions = localPermissions;
        this.remotePermissions = remotePermissions;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLocalPermissions() {
        return localPermissions;
    }

    public void setLocalPermissions(int localPermissions) {
        this.localPermissions = localPermissions;
    }

    public int getRemotePermissions() {
        return remotePermissions;
    }

    public void setRemotePermissions(int remotePermissions) {
        this.remotePermissions = remotePermissions;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", localPermissions='" + localPermissions + '\'' +
                ", remotePermissions='" + remotePermissions + '\'' +
                '}';
    }
}
