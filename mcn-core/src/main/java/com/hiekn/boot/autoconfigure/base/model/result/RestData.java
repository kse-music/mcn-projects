package com.hiekn.boot.autoconfigure.base.model.result;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 *
 * please RestResp count attr as rsCount
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestData<T> {

	private List<T> rsData;
	private Long rsCount;
	
	public RestData() {}

	public RestData(List<T> rsData, Integer count){
		this(rsData,Long.valueOf(count));
	}
	
	public RestData(List<T> rsData, Long count){
		this.rsData = rsData;
		this.rsCount = count;
	}
	
	public List<T> getRsData() {
		return rsData;
	}
	
	public void setRsData(List<T> rsData) {
		this.rsData = rsData;
	}
	
	public Long getRsCount() {
		return rsCount;
	}
	
	public void setRsCount(Long rsCount) {
		this.rsCount = rsCount;
	}
	
}
