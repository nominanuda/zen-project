package com.nominanuda.web.mvc;

import java.util.Collections;
import java.util.List;

import com.nominanuda.code.Immutable;
import com.nominanuda.lang.Check;

@Immutable
public class HandlerAndFiltersBean implements HandlerAndFilters {
	private final Object handler;
	private final List<HandlerFilter> filters;
	public HandlerAndFiltersBean(Object handler, List<HandlerFilter> filters) {
		this.handler = Check.notNull(handler);
		this.filters = Collections.unmodifiableList(Check.notNull(filters));
	}

	public Object getHandler() {
		return handler;
	}

	public List<HandlerFilter> getFilters() {
		return filters;
	}

}
