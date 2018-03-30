package com.hiekn.boot.autoconfigure.web.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class SpringBeanUtils{
	
	private static ApplicationContext ac;
	
	public static Object getBean(String name) {  
		checkApplicationContext();  
		return ac.getBean(name);  
	}  
	
	public static <T> T getBean(String name,Class<T> clazz) {
		checkApplicationContext();
		return ac.getBean(name,clazz);
	} 
	
	public static <T> T getBean(Class<T> clazz) {  
		checkApplicationContext();  
		return ac.getBean(clazz);
	} 

	public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ac = applicationContext;
	}

	public static ApplicationContext getAc() {
		return ac;
	}

	private static void checkApplicationContext() {
		if (ac == null) { 
			throw new IllegalStateException("applicationContext not inject yet");
		}
	}

}
