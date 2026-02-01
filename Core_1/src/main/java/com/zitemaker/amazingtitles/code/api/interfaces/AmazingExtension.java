package com.zitemaker.amazingtitles.code.api.interfaces;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import com.zitemaker.amazingtitles.code.api.AmazingTitles;
import com.zitemaker.amazingtitles.code.internal.Booter;

import java.io.File;
import java.util.List;

public interface AmazingExtension {
	
	String extension_name();
	
	void load();
	void unload();
	
	default void addListener(Listener listener) {
		AmazingTitles.registerExtensionListener(this, listener);
	}
	
	default Plugin getPluginInstance() {
		return Booter.getInstance();
	}
	
	default List<Listener> getListeners() {
		return AmazingTitles.getExtensionListeners(extension_name());
	}
	
	default void unregisterListeners() {
		AmazingTitles.unregisterExtensionListeners(extension_name());
	}
	
	default File getAsFile() {
		return null;
	}
	
}
