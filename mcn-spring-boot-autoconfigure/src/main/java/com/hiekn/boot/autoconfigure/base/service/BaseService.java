package com.hiekn.boot.autoconfigure.base.service;

import com.hiekn.boot.autoconfigure.base.model.PageModel;
import com.hiekn.boot.autoconfigure.base.model.result.RestData;

import java.util.Map;

public interface BaseService<T,PK> {
    T save(T pojo);
    T saveSelective(T pojo);
    void deleteByPrimaryKey(PK id);
    T getByPrimaryKey(PK id);
    void updateByPrimaryKeySelective(T pojo);
    RestData<T> listByPage(PageModel pageModel, Map<String, Object> params);

}
