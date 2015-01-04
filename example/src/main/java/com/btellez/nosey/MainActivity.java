package com.btellez.nosey;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.btellez.noseyexplorer.Nosey;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (isRealmEmpty()) {
            populateModelWithData();
        }
        
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nosey nosey = Nosey.getInstance(MainActivity.this);
                nosey.register(SomeModelA.class);
                nosey.start();
            }
        });
    }
    
    private boolean isRealmEmpty() {
        RealmResults result = Realm.getInstance(this).where(SomeModelA.class).findAll();
        Log.d("isRealmEmpty", result.size()+"");
        return result.size() == 0;
    }

    private void populateModelWithData() {
        Log.d("populateModelWithData", "Adding 10 Items to Data.");
        Random random = new Random();
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        SomeModelA instance;
        for (int i = 0; i < 10; i++) {
            instance = realm.createObject(SomeModelA.class);
            instance.setField1(100 + random.nextInt() % 10);
            instance.setField2(200 + random.nextInt() % 10);
            instance.setField3(300 + random.nextInt() % 10);
            instance.setField4(400 + random.nextInt() % 10);
            instance.setField5(500 + random.nextInt() % 10);
            instance.setField6(600 + random.nextInt() % 10);
            instance.setField7(700 + random.nextInt() % 10);
            instance.setField8("This is a string");
        }
        realm.commitTransaction();
    }
}
