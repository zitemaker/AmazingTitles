package com.zitemaker.amazingtitles.code.providers.R1_21_R7;

import com.zitemaker.amazingtitles.code.internal.spi.NmsBuilder;
import com.zitemaker.amazingtitles.code.internal.spi.NmsProvider;

public class R1_21_R7_Builder implements NmsBuilder {
	
	@Override
	public boolean checked(String version) {
		return version.equals("v1_21_R7") 
			|| version.startsWith("1.21.11-R0.1")
			|| version.startsWith("1.21.11-R0.2");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_21_R7();
	}
	
}
