package com.zitemaker.amazingtitles.code.api.interfaces;

import com.zitemaker.amazingtitles.code.internal.components.ComponentArguments;

import java.util.LinkedList;

public interface FramesBuilder {
	
	LinkedList<String> buildFrames(ComponentArguments arguments, String[] args);
	
}
