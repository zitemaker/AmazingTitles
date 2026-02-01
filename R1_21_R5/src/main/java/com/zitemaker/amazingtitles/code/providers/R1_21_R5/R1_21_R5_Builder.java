package com.zitemaker.amazingtitles.code.providers.R1_21_R5;

import com.zitemaker.amazingtitles.code.internal.spi.NmsBuilder;
import com.zitemaker.amazingtitles.code.internal.spi.NmsProvider;

public class R1_21_R5_Builder implements NmsBuilder {
	
	@Override
	public boolean checked(String version) {
		return version.equals("v1_21_R5") 
			|| version.startsWith("1.21.6-R0.1")
			|| version.startsWith("1.21.7-R0.1")
			|| version.startsWith("1.21.8-R0.1");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_21_R5();
	}
	
}
