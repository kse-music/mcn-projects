package com.hiekn.boot.autoconfigure.base.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

public class BaseModel {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageNo;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer pageSize;

    private transient Date createTime;

    private transient Date updateTime;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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