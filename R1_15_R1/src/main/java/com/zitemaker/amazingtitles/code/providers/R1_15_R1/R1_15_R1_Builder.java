package com.zitemaker.amazingtitles.code.providers.R1_15_R1;


import com.zitemaker.amazingtitles.code.internal.spi.NmsBuilder;
import com.zitemaker.amazingtitles.code.internal.spi.NmsProvider;

public class R1_15_R1_Builder implements NmsBuilder {
	
	
	@Override
	public boolean checked(String version) {
		return version.equals("v1_15_R1");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_15_R1();
	}
	
}
