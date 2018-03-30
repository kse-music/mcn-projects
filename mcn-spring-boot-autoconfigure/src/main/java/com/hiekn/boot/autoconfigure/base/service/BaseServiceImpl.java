package com.hiekn.boot.autoconfigure.base.service;

import com.google.common.collect.Maps;
import com.hiekn.boot.autoconfigure.base.mapper.BaseMapper;
import com.hiekn.boot.autoconfigure.base.model.PageModel;
import com.hiekn.boot.autoconfigure.base.model.result.RestData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

public class BaseServiceImpl<T,PK> implements BaseService<T,PK> {

    @Autowired
    private BaseMapper<T,PK> baseMapper;

    @Override
    public T save(T pojo) {
        baseMapper.insert(pojo);
        return pojo;
    }

    @Override
    public T saveSelective(T pojo) {
        baseMapper.insertSelective(pojo);
        return pojo;
    }

    @Override
    public void deleteByPrimaryKey(PK id) {
        baseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public T getByPrimaryKey(PK id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public void updateByPrimaryKeySelective(T pojo) {
        baseMapper.updateByPrimaryKeySelective(pojo);
    }


    @Override
    public RestData<T> listByPage(PageModel pageModel, Map<String, Object> params) {
        if(Objects.isNull(params)){
            params = Maps.newHashMap();
        }
        params.put("pageNo",pageModel.getPageNo());
        params.put("pageSize",pageModel.getPageSize());
        return new RestData<>(baseMapper.pageSelect(params),baseMapper.pageCount(params));
    }
}
