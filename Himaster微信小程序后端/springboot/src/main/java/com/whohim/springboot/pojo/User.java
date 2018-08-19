package com.whohim.springboot.pojo;

import java.util.Date;

public class User {
    private Integer id;

    private String openid;

    private String nickname;

    private String avatarurl;

    private Integer stepinfo;

    private String password;

    private String raspberrypie;

    private String token;

    private String phone;

    private Integer role;

    private Date createTime;

    private Date updateTime;

    public User(Integer id, String openid, String nickname, String avatarurl, Integer stepinfo, String password, String raspberrypie, String token, String phone, Integer role, Date createTime, Date updateTime) {
        this.id = id;
        this.openid = openid;
        this.nickname = nickname;
        this.avatarurl = avatarurl;
        this.stepinfo = stepinfo;
        this.password = password;
        this.raspberrypie = raspberrypie;
        this.token = token;
        this.phone = phone;
        this.role = role;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public User() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid == null ? null : openid.trim();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl == null ? null : avatarurl.trim();
    }

    public Integer getStepinfo() {
        return stepinfo;
    }

    public void setStepinfo(Integer stepinfo) {
        this.stepinfo = stepinfo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getRaspberrypie() {
        return raspberrypie;
    }

    public void setRaspberrypie(String raspberrypie) {
        this.raspberrypie = raspberrypie == null ? null : raspberrypie.trim();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}