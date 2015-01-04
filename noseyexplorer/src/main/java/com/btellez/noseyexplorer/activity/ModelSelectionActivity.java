package com.btellez.noseyexplorer.activity;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.btellez.noseyexplorer.Inspector;
import com.btellez.noseyexplorer.Nosey;

public class ModelSelectionActivity extends Activity {
    
    // Nosey Items
    Nosey nosey;
    Inspector.ModelNameInspector inspector;
    
    // Data Access
    BaseAdapter adaper;
    
    // View
    ListView listView;
    TextView emptyView;
    
    final Context context = this;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);
      
        nosey = Nosey.getInstance(this);
      
        inspector = new Inspector.ModelNameInspector();
        inspector.inspect(nosey);
      
        adaper = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, inspector.getModelNames());
      
        listView = new ListView(this);
        listView.setAdapter(adaper);
        listView.setEmptyView(getEmptyView());
        listView.setOnItemClickListener(getOnItemClickListener());
        
        setContentView(listView);
    }

    private AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DisplayModelActivity.startActivity(ModelSelectionActivity.this, inspector.getModelNames().get(position));
                Toast.makeText(context, "Clicked "+ position, Toast.LENGTH_LONG).show();
            }
        };
    }

    private View getEmptyView() {
        emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        emptyView.setText("No Models Have Been Registered.");
        return emptyView;
    }
}
