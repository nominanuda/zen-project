package com.nominanuda.web.mvc;

import java.util.List;

public interface HandlerAndFilters {
	Object getHandler();
	List<HandlerFilter> getFilters();
}
