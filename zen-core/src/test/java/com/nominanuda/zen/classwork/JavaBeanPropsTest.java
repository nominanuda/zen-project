package com.nominanuda.zen.classwork;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.junit.Test;

public class JavaBeanPropsTest {

	@Test
	public void test() throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(Foo.class);
		PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
		
		for (PropertyDescriptor pd : pds) {
			String propertyName = pd.getName();
			System.out.println("propertyName = " + propertyName);
		}
	}

	private static class Foo {
	}
}
