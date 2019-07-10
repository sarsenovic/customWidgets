package com.example.testcustomwidgetslibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

/**
 * Magic with Shared Preferences xD
 * Doesn't work with Set (wait for update)
 */
public class SharedPrefs {
    private static final String TAG = "CustomWidgetSharedPrefs";
    private Context context;
    private String prefsName = "";
    private int mode = 0;
    private boolean loggingEnabled = false;

    /**
     * Creates an instance from class SharedPrefs
     *
     * @param context        Context.
     * @param prefsName      not null String which is name of Shared Preference
     * @param mode           int mode (access modifier)
     * @param loggingEnabled boolean - true if you allow to make changes in log
     */
    public SharedPrefs(Context context, String prefsName, int mode, boolean loggingEnabled) {
        this.context = context;
        this.prefsName = prefsName;
        this.mode = mode;
        this.loggingEnabled = loggingEnabled;
    }

    /**
     * Return all parameter from Shared Preferences
     */
    public Map<String, ?> getAllFromPrefs() {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                return sharedPreferences.getAll();
            } else {
                loge("Shared Prefererences is null");
                return null;
            }
        } else {
            loge("Context is null");
            return null;
        }
    }

    /**
     * Returns true if requested parameter exist in Shared Preferences
     *
     * @param parameter Requested parameter for search
     */
    public boolean checkIfParameterExist(String parameter) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameter != null && !parameter.equals("")) {
                    if (sharedPreferences.contains(parameter)) {
                        logi("Parameter: " + parameter + " found");
                        return true;
                    } else {
                        loge("Your Shared Preferences doesn't contains parameter " + parameter);
                        return false;
                    }
                } else {
                    loge("Requested parameter is null, or it's empty");
                }
            } else {
                loge("Shared Prefererences is null");
                return false;
            }
        } else {
            loge("Context is null");
            return false;
        }
        return false;
    }

    /**
     * Create or edit Shared Preferences
     *
     * @param params          parameters to insert or update
     * @param editPreferences true if you want to edit your Shared Preferences, false if you want to create it
     */
    public void updatePrefs(Map<String, Object> params, boolean editPreferences) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (params != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    boolean haveChanges = false;

                    for (Map.Entry parameter : params.entrySet()) {
                        if (editPreferences) {
                            if (sharedPreferences.contains(String.valueOf(parameter.getKey()))) {
                                //Update parameters of shared preferences
                                String valueType = getValueTypeOfMapParameter(parameter.getValue());

                                if (valueType != null) {
                                    switch (valueType) {
                                        case "string":
                                            editor.putString(String.valueOf(parameter.getKey()), String.valueOf(parameter.getValue()));
                                            break;
                                        case "int":
                                            editor.putInt(String.valueOf(parameter.getKey()), (Integer) parameter.getValue());
                                            break;
                                        case "long":
                                            editor.putLong(String.valueOf(parameter.getKey()), (Long) parameter.getValue());
                                            break;
                                        case "float":
                                            editor.putFloat(String.valueOf(parameter.getKey()), (Float) parameter.getValue());
                                            break;
                                        case "boolean":
                                            editor.putBoolean(String.valueOf(parameter.getKey()), (Boolean) parameter.getValue());
                                            break;
                                    }
                                    haveChanges = true;
                                }

                            } else {
                                loge("Parameter " + parameter.getKey() + " doesn't exist in " + prefsName);
                            }
                        } else {
                            //Create shared preferences with parameters
                            String valueType = getValueTypeOfMapParameter(parameter.getValue());

                            if (valueType != null) {
                                switch (valueType) {
                                    case "string":
                                        editor.putString(String.valueOf(parameter.getKey()), String.valueOf(parameter.getValue()));
                                        break;
                                    case "int":
                                        editor.putInt(String.valueOf(parameter.getKey()), (Integer) parameter.getValue());
                                        break;
                                    case "long":
                                        editor.putLong(String.valueOf(parameter.getKey()), (Long) parameter.getValue());
                                        break;
                                    case "float":
                                        editor.putFloat(String.valueOf(parameter.getKey()), (Float) parameter.getValue());
                                        break;
                                    case "boolean":
                                        editor.putBoolean(String.valueOf(parameter.getKey()), (Boolean) parameter.getValue());
                                        break;
                                }
                                haveChanges = true;
                            }
                        }
                    }

                    if (haveChanges) {
                        editor.apply();
                        logi("Successfuly updated changes on shared prefs parameters");
                    }
                }
            } else {
                loge("Shared Prefererences is null");
            }
        } else {
            loge("Context is null");
        }
    }

    private String getValueTypeOfMapParameter(Object parameterValue) {
        if (parameterValue != null) {
            if (parameterValue instanceof String) {
                return "string";
            } else if (parameterValue instanceof Integer) {
                return "int";
            } else if (parameterValue instanceof Long) {
                return "long";
            } else if (parameterValue instanceof Float) {
                return "float";
            } else if (parameterValue instanceof Boolean) {
                return "boolean";
            } else {
                if (loggingEnabled)
                    loge("Unknown type of parameter (String, Integer, Long, Float and Boolean are included");
                return null;
            }
        } else {
            loge("Parameter value is null while getting type of parameter");
            return null;
        }
    }

    /**
     * Removes one parameter from Shared Preferences
     *
     * @param parameterKey key of the parameter which you want to remove
     */
    public void removeParameterFromSharedPrefs(String parameterKey) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameterKey != null && !parameterKey.equals("")) {
                    if (sharedPreferences.contains(parameterKey)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(parameterKey);
                        editor.apply();
                        logi("Successfuly removed " + parameterKey + " from " + prefsName);
                    } else {
                        loge("Requested parameter for delete doesn't exist in your shared preferences");
                    }
                } else {
                    loge("parameterKey is null, or it's empty from method parameters. Check it and try again");
                }
            } else {
                loge("Shared Preferences is null");
            }
        } else {
            loge("Context is null");
        }
    }

    /**
     * Clears all data from Shared Preferences
     */
    public void clearAllDataFromSharedPrefs() {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                logi("Successfuly cleared " + prefsName);
            } else {
                loge("Shared Preferences is null");
            }
        } else {
            loge("Context is null");
        }
    }

    /**
     * Puts one param in Shared Preferences
     *
     * @param parameterKey key of the parameter which you want to receive
     * @param objectValue  value of parameter which you want to put into sp
     */
    public void addOneParameterInPrefs(String parameterKey, Object objectValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameterKey != null && !parameterKey.equals("")) {
                    if (objectValue != null) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (objectValue instanceof String) {
                            editor.putString(parameterKey, (String) objectValue);
                            editor.apply();
                        } else if (objectValue instanceof Integer) {
                            editor.putInt(parameterKey, (Integer) objectValue);
                            editor.apply();
                        } else if (objectValue instanceof Long) {
                            editor.putLong(parameterKey, (Long) objectValue);
                            editor.apply();
                        } else if (objectValue instanceof Float) {
                            editor.putFloat(parameterKey, (Float) objectValue);
                            editor.apply();
                        } else if (objectValue instanceof Boolean) {
                            editor.putBoolean(parameterKey, (Boolean) objectValue);
                            editor.apply();
                        } else {
                            loge(parameterKey + " cannot be added in " + prefsName);
                        }
                    }
                } else {
                    loge(parameterKey + " is null, or it's empty from method parameters. Check it and try again");
                }
            } else {
                loge("Shared Preferences is null");
            }
        } else {
            loge("Context is null");
        }
    }

    /**
     * Returns a String from Shared Preferences
     *
     * @param parameterKey       key of the parameter which you want to receive
     * @param defaultStringValue default value of requested parameter
     */
    public String getStringValueFromPrefs(String parameterKey, String defaultStringValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameterKey != null && !parameterKey.equals("")) {
                    if (sharedPreferences.contains(parameterKey)) {
                        return sharedPreferences.getString(parameterKey, defaultStringValue);
                    } else {
                        loge(parameterKey + " doesn't exist in " + prefsName);
                    }
                } else {
                    loge(parameterKey + " is null, or it's empty from method parameters. Check it and try again");
                    return defaultStringValue;
                }
            } else {
                loge("Shared Preferences is null");
                return defaultStringValue;
            }
            return defaultStringValue;
        } else {
            loge("Context is null");
            return null;
        }
    }

    /**
     * Returns an int from Shared Preferences
     *
     * @param parameterKey    key of the parameter which you want to receive
     * @param defaultIntValue default value of requested parameter
     */
    public int getIntValueFromPrefs(String parameterKey, int defaultIntValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameterKey != null && !parameterKey.equals("")) {
                    if (sharedPreferences.contains(parameterKey)) {
                        return sharedPreferences.getInt(parameterKey, defaultIntValue);
                    } else {
                        loge(parameterKey + " doesn't exist in " + prefsName);
                    }
                } else {
                    loge(parameterKey + " is null, or it's empty from method parameters. Check it and try again");
                    return defaultIntValue;
                }
            } else {
                loge("Shared Preferences is null");
                return defaultIntValue;
            }
            return defaultIntValue;
        } else {
            loge("Context is null");
            return 0;
        }
    }

    /**
     * Returns a long from Shared Preferences
     *
     * @param parameterKey     key of the parameter which you want to receive
     * @param defaultLongValue default value of requested parameter
     */
    public long getLongValueFromPrefs(String parameterKey, long defaultLongValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameterKey != null && !parameterKey.equals("")) {
                    if (sharedPreferences.contains(parameterKey)) {
                        return sharedPreferences.getLong(parameterKey, defaultLongValue);
                    } else {
                        loge(parameterKey + " doesn't exist in " + prefsName);
                    }
                } else {
                    loge(parameterKey + " is null, or it's empty from method parameters. Check it and try again");
                    return defaultLongValue;
                }
            } else {
                loge("Shared Preferences is null");
                return defaultLongValue;
            }
            return defaultLongValue;
        } else {
            loge("Context is null");
            return 0L;
        }
    }

    /**
     * Returns a float from Shared Preferences
     *
     * @param parameterKey      key of the parameter which you want to receive
     * @param defaultFloatValue default value of requested parameter
     */
    public float getFloatValueFromPrefs(String parameterKey, float defaultFloatValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameterKey != null && !parameterKey.equals("")) {
                    if (sharedPreferences.contains(parameterKey)) {
                        return sharedPreferences.getFloat(parameterKey, defaultFloatValue);
                    } else {
                        loge(parameterKey + " doesn't exist in " + prefsName);
                    }
                } else {
                    loge(parameterKey + " is null, or it's empty from method parameters. Check it and try again");
                    return defaultFloatValue;
                }

            } else {
                loge("Shared Preferences is null");
                return defaultFloatValue;
            }
            return defaultFloatValue;
        } else {
            loge("Context is null");
            return 0f;
        }
    }

    /**
     * Returns a boolean from Shared Preferences
     *
     * @param parameterKey        key of the parameter which you want to receive
     * @param defaultBooleanValue default value of requested parameter
     */
    public boolean getBooleanValueFromPrefs(String parameterKey, boolean defaultBooleanValue) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
            if (sharedPreferences != null) {
                if (parameterKey != null && !parameterKey.equals("")) {
                    if (sharedPreferences.contains(parameterKey)) {
                        return sharedPreferences.getBoolean(parameterKey, defaultBooleanValue);
                    } else {
                        loge(parameterKey + " doesn't exist in " + prefsName);
                    }
                } else {
                    loge(parameterKey + " is null, or it's empty from method parameters. Check it and try again");
                    return defaultBooleanValue;
                }

            } else {
                loge("Shared Preferences is null");
                return defaultBooleanValue;
            }
            return defaultBooleanValue;
        } else {
            loge("Context is null");
            return false;
        }
    }

    /**
     * Prints message in error log.
     *
     * @param message not null String for show message in log (error)
     */
    private void loge(String message) {
        if (loggingEnabled && message != null)
            Log.e(TAG, message);
    }

    /**
     * Prints message in info log.
     *
     * @param message not null String for show message in log (info)
     */
    private void logi(String message) {
        if (loggingEnabled && message != null)
            Log.i(TAG, message);
    }

    //Forgottable methods xD

//    public static Object getOneParameterFromPrefs(Context context, String prefsName, int mode, String parameterKey, String nameOfParameterType) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(prefsName, mode);
//        if (sharedPreferences != null) {
//            if (nameOfParameterType != null) {
//                if (parameterKey != null && !parameterKey.equals("")) {
//                    switch (nameOfParameterType) {
//                        case "string":
//                            return sharedPreferences.getString(parameterKey, "");
//                        case "int":
//                            return sharedPreferences.getInt(parameterKey, 0);
//                        case "long":
//                            return sharedPreferences.getLong(parameterKey, 0);
//                        case "float":
//                            return sharedPreferences.getFloat(parameterKey, 0);
//                        case "boolean":
//                            return sharedPreferences.getBoolean(parameterKey, false);
//
//                    }
//                } else {
//                    Log.e(TAG, "parameterKey is null, or it's empty from method parameters. Check it and try again");
//                }
//            } else {
//                Log.e(TAG, "String nameOfParameterType is null from method parameters. Check it and try again");
//            }
//        } else {
//            Log.e(TAG, "NULL Shared Preferences");
//            return null;
//        }
//        return null;
//    }
}
