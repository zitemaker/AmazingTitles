package com.zitemaker.amazingtitles.code.providers.R1_20_R0;

import com.zitemaker.amazingtitles.code.internal.spi.NmsBuilder;
import com.zitemaker.amazingtitles.code.internal.spi.NmsProvider;

public class R1_20_R1_Builder implements NmsBuilder {
	
	
	@Override
	public boolean checked(String version) {
		return version.equals("v1_20_R1");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_20_R1();
	}
	
}
