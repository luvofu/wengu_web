package com.wg.user.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by wzhonggo on 8/4/2016.
 */
//用户令牌表
@Cacheable
@Entity
@Table(name = "valid",
        uniqueConstraints = {@UniqueConstraint(name = "ukey_dt_rm_uc", columnNames = {"deviceToken", "regMobile", "useCondition"})})
public class Valid {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long validcodeId;
    private String deviceToken;
    private String regMobile;
    private int useCondition;
    private String validcode;
    private int restSendNum;
    @org.hibernate.annotations.UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private Date updatedTime;
    private Date createdTime = TimeUtils.getCurrentDate();

    public long getValidcodeId() {
        return validcodeId;
    }

    public void setValidcodeId(long validcodeId) {
        this.validcodeId = validcodeId;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getRegMobile() {
        return regMobile;
    }

    public void setRegMobile(String regMobile) {
        this.regMobile = regMobile;
    }

    public int getUseCondition() {
        return useCondition;
    }

    public void setUseCondition(int useCondition) {
        this.useCondition = useCondition;
    }

    public String getValidcode() {
        return validcode;
    }

    public void setValidcode(String validcode) {
        this.validcode = validcode;
    }

    public int getRestSendNum() {
        return restSendNum;
    }

    public void setRestSendNum(int restSendNum) {
        this.restSendNum = restSendNum;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
