package com.wg.solr.domain;

import com.wg.common.utils.TimeUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: wzhonggo
 * Date: 16-9-24
 * Time: 下午8:36
 * To change this template use File | Settings | File Templates.
 */
@Cacheable
@Entity
@Table(name = "temp_solr_index", indexes = {@Index(name = "key_createdTime", columnList = "createdTime")})
public class TempSolrIndex {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private int type;
    private long objId;
    private Date createdTime = TimeUtils.getCurrentDate();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getObjId() {
        return objId;
    }

    public void setObjId(long objId) {
        this.objId = objId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
