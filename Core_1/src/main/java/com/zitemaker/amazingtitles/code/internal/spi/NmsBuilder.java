package com.zitemaker.amazingtitles.code.internal.spi;

public interface NmsBuilder {
	
	boolean checked(String version);
	NmsProvider build();
	
}
