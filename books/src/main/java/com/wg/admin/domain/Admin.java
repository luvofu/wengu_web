package com.wg.admin.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-11-6
 * Time: 上午10:34
 * To change this template use File | Settings | File Templates.
 */
@Cacheable
@Entity
@Table(name = "admin", indexes = {@Index(name = "ukey_username", columnList = "username", unique = true)})
public class Admin {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long adminId;
    private String username;
    private String password;
    private int roleType;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    @org.hibernate.annotations.CreationTimestamp
    private Date createdTime = TimeUtils.getCurrentDate();
    ;

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
