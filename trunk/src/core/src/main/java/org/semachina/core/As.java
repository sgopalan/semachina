package org.semachina.core;

public interface As {
	
	public boolean canAs(Class<?> type);
	
	public <T> T as(Class<T> type);
}
