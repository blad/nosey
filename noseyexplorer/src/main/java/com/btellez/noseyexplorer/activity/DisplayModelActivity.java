package com.btellez.noseyexplorer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.btellez.noseyexplorer.Inspector;
import com.btellez.noseyexplorer.Nosey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmResults;

public class DisplayModelActivity extends Activity {

    private final static String EXTRA_MODEL_KEY = "extra_model_key";

    TableLayout table;

    Nosey nosey;
    Inspector.ModelMapInspector inspector;

    public static void startActivity(Context context, String modelName) {
        Intent intent = new Intent(context, DisplayModelActivity.class);
        intent.putExtra(EXTRA_MODEL_KEY, modelName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        table = new TableLayout(this);

        nosey = Nosey.getInstance(this);

        inspector = new Inspector.ModelMapInspector();
        inspector.inspect(nosey);

        addModelFiledHeaders();
        addModelDataRows();

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        scrollView.setFillViewport(true);
        
        scrollView.addView(table);
        setContentView(scrollView);
    }

    private void addModelDataRows() {
        String modelKey = getIntent().getStringExtra(EXTRA_MODEL_KEY);
        Class model = inspector.getModelMap().get(modelKey);

        Realm realm = Realm.getInstance(this);
        RealmResults data = realm.where(model).findAll();

        Method[] methods = model.getDeclaredMethods(); // TODO: Find a way to sort.
        Arrays.sort(methods, new Comparator<Method>() {
            @Override public int compare(Method lhs, Method rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        
        for (Object target : data) {
            TableRow row = newTableRow();
            for (Method method : methods) {
                String value = "";
                if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                    try {
                        value = method.invoke(target).toString();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                
                row.addView(newCell(value));
            }
            table.addView(row);
        }
    }

    public void addModelFiledHeaders() {
        // Get Fields for a given model
        String modelKey = getIntent().getStringExtra(EXTRA_MODEL_KEY);
        Class model = inspector.getModelMap().get(modelKey);
        model.getDeclaredFields();
        Field[] allFields = model.getDeclaredFields();
        Arrays.sort(allFields, new Comparator<Field>() {
            @Override public int compare(Field lhs, Field rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });

        // Add the field headers to the table row
        TableRow headers = newTableRow();
        TextView view;
        for (Field field : allFields) {
            view = new TextView(this);
            view.setText(field.getName());
            headers.addView(view);
        }

        table.addView(headers);
    }
    
    public TextView newCell(String value) {
        TextView cell = new TextView(this);
        cell.setPadding(10, 10, 10, 10);
        cell.setText(value);
        return cell;
    }

    public TableRow newTableRow() {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(table.getChildCount() % 2 == 0 ? Color.parseColor("#CCCCCC"): Color.parseColor("#ffffff"));
        return row;
    }
}
