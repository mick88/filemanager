package com.michaldabski.utils;

public interface OnResultListener<T>
{
	void onResult(AsyncResult<T> result);
}
