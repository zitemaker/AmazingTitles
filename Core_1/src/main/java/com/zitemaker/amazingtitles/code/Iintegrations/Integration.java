package com.zitemaker.amazingtitles.code.Iintegrations;

import com.zitemaker.amazingtitles.code.api.AmazingTitles;

import java.io.File;

public interface Integration {

    void reload();

    default File getDataFolder() {
        return AmazingTitles.INTEGRATIONS_FOLDER;
    }

}
