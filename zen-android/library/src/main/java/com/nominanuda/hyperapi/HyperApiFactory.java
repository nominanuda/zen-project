package com.nominanuda.hyperapi;

/**
 * Created by azum on 20/03/17.
 */

public interface HyperApiFactory {

	<API> API getInstance(String instanceHint, Class<? extends API> api);

}