package com.shaneisrael.st.prefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shaneisrael.st.data.Locations;
import com.shaneisrael.st.data.OperatingSystem;
import com.shaneisrael.st.utilities.FileReader;

public class Preferences
{
    public static long TOTAL_SAVED_UPLOADS = 0;
    private static Preferences instance;

    private final Locations locations;
    private String jsonData;
    private PreferenceData preferences;

    private Preferences(Locations locations) throws PreferencesException
    {
        this.locations = locations;
        if (locations.getPreferencesFile().exists() && locations.getPreferencesFile().isFile())
        {
            try
            {
                jsonData = FileReader.readFile(locations.getPreferencesFile().getAbsolutePath());
            } catch (IOException e)
            {
                throw new PreferencesException("Error reading preferences with Locations: " + locations.toString()
                    + ": " + e.getMessage());
            }
            init();
        } else
        {
            setDefaultPreferences();
        }
    }

    private void init()
    {
        Gson gson = new Gson();
        preferences = gson.fromJson(jsonData, PreferenceData.class);
    }

    public void save()
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(preferences);
        try
        {
            FileReader.writeFile(locations.getPreferencesFile().getAbsolutePath(), json);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        System.out.println("Saved preferences to " + locations.getPreferencesFile().getAbsolutePath());
    }

    /**
     * Reloads settings from disk
     */
    public void refresh()
    {
        try
        {
            jsonData = FileReader.readFile(locations.getPreferencesFile().getAbsolutePath());
        } catch (IOException e)
        {
            e.printStackTrace(); //shouldn't happen
        }
        Gson gson = new Gson();
        preferences = gson.fromJson(jsonData, PreferenceData.class);
        System.out.println("Refreshed preferences from " + locations.getPreferencesFile().getAbsolutePath());
        System.out.println(jsonData);
    }

    public void setDefaultPreferences()
    {
        new File(OperatingSystem.getCurrentOS().getDataDirectoryPath()).mkdirs();
        new File(OperatingSystem.getCurrentOS().getPictureDirectoryPath()).mkdirs();

        preferences = new PreferenceData();
        preferences.setAutoSaveEnabled(true);
        preferences.setEditorEnabled(true);
        preferences.setDefaultTool(0);
        preferences.setCaptureDirectoryRoot(locations.getPictureDirectory().getAbsolutePath());
        save();
    }

    public String getCaptureDirectoryRoot()
    {
        return preferences.getCaptureDirectoryRoot();
    }

    /**
     * @param captureDirectoryRoot
     *            the captureDirectoryRoot to set
     */
    public void setCaptureDirectoryRoot(String captureDirectoryRoot)
    {
        preferences.captureDirectoryRoot = captureDirectoryRoot;
        save();
    }

    public boolean isAutoSaveEnabled()
    {
        return preferences.isAutoSaveEnabled();
    }

    /**
     * @param autoSaveEnabled
     *            the autoSaveEnabled to set
     */
    public void setAutoSaveEnabled(boolean autoSaveEnabled)
    {
        preferences.autoSaveEnabled = autoSaveEnabled;
        save();
    }

    public boolean isEditorEnabled()
    {
        return preferences.isEditorEnabled();
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEditorEnabled(boolean enabled)
    {
        preferences.getEditor().enabled = enabled;
        save();
    }

    public long getDefaultTool()
    {
        return preferences.getDefaultTool();
    }

    /**
     * @param defaultTool
     *            the defaultTool to set
     */
    public void setDefaultTool(long defaultTool)
    {
        preferences.getEditor().defaultTool = defaultTool;
        save();
    }

    public static Preferences getInstance()
    {
        if (instance == null)
        {
            try
            {
                instance = new Preferences(new Locations());
            } catch (PreferencesException e)
            {
                e.printStackTrace();
            }
        }
        return instance;
    }
}