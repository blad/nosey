package com.btellez.noseyexplorer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ModelSelectionActivity extends Activity implements AdapterView.OnItemClickListener {
    
    Nosey nosey;
    Inspector.ModelNameInspector inspector;

    BaseAdapter adaper;
    ListView listView;
    TextView emptyView;

    final Context context = this;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentView = new FrameLayout(this);
        setContentView(contentView);
        
        // Set up Nosey
        nosey = Nosey.getInstance(this);
        inspector = new Inspector.ModelNameInspector();
        inspector.inspect(nosey);

        // Set up List Adapter
        int listItemLayout = android.R.layout.simple_list_item_1;
        List<String> modelNames = inspector.getModelNames();
        adaper = new ArrayAdapter<String>(context, listItemLayout, modelNames);
        
        // Set up List View
        listView = new ListView(this);
        listView.setAdapter(adaper);
        listView.setEmptyView(getEmptyView());
        listView.setOnItemClickListener(this);
        
        // Add View Elements to Activity Container
        contentView.addView(listView);
        contentView.addView(emptyView);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String modelName = inspector.getModelNames().get(position);
        DisplayModelActivity.startActivity(context, modelName);
    }
    
    private View getEmptyView() {
        emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        emptyView.setText("No Models Have Been Registered.");
        return emptyView;
    }
}
