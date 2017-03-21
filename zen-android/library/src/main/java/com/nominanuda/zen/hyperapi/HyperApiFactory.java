package com.nominanuda.zen.hyperapi;

/**
 * Created by azum on 20/03/17.
 */

public interface HyperApiFactory {

	<T> T getInstance(String instanceHint, Class<? extends T> role);

}