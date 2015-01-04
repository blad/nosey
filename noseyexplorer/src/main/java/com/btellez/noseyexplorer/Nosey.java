package com.btellez.noseyexplorer;

import android.content.Context;
import android.content.Intent;

import com.btellez.noseyexplorer.activity.ModelSelectionActivity;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;

public class Nosey {
    protected Map<String, Class> objectTypes = new HashMap<String, Class>();
    
    private static Nosey instance;
    private Context context;

    private Nosey(Context context) {
        this.context = context;
    }
    
    public static Nosey getInstance(Context context) {
        if (instance == null)
            instance = new Nosey(context);
        return instance;
    }

    /**
     * Start the Nosey Explorer.
     */
    public void start() {
        Intent intent = new Intent(context, ModelSelectionActivity.class);
        context.startActivity(intent);
    }

    /**
     * Register a model to be explored
     * @param type
     * @return
     */
    public Nosey register(Class<? extends RealmObject> type) {
        objectTypes.put(type.getSimpleName(), type);
        return this;
    }

    public Nosey unregister(Class<? extends RealmObject> type) {
        objectTypes.remove(type.getSimpleName());
        return this;
    }
}
