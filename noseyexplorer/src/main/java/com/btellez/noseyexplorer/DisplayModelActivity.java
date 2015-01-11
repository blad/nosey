package com.btellez.noseyexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmResults;

public class DisplayModelActivity extends Activity {

    private final static String EXTRA_MODEL_KEY = "extra_model_key";
    private int textSize;
    private int padding;
    
    public static void startActivity(Context context, String modelName) {
        Intent intent = new Intent(context, DisplayModelActivity.class);
        intent.putExtra(EXTRA_MODEL_KEY, modelName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textSize = (int) getResources().getDimension(R.dimen.cell_text_size);
        padding = (int) getResources().getDimension(R.dimen.cell_padding);
        
        Nosey nosey = Nosey.getInstance(this);
        Inspector.ModelMapInspector inspector = new Inspector.ModelMapInspector();
        inspector.inspect(nosey);

        TableLayout table = new TableLayout(this);
        addModelFiledHeaders(table, inspector);
        addModelDataRows(table, inspector);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        scrollView.setHorizontalScrollBarEnabled(true);
        scrollView.setVerticalScrollBarEnabled(true);
        scrollView.setFillViewport(true);

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
        horizontalScrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        horizontalScrollView.setHorizontalScrollBarEnabled(true);
        horizontalScrollView.setVerticalScrollBarEnabled(true);
        horizontalScrollView.setFillViewport(true);
        
        scrollView.addView(horizontalScrollView);
        horizontalScrollView.addView(table);
        
        setContentView(scrollView);
    }

    private void addModelDataRows(TableLayout table, Inspector.ModelMapInspector inspector) {
        String modelKey = getIntent().getStringExtra(EXTRA_MODEL_KEY);
        Class model = inspector.getModelMap().get(modelKey);

        Realm realm = Realm.getInstance(this);
        RealmResults data = realm.where(model).findAll();

        Method[] methods = model.getDeclaredMethods();
        Arrays.sort(methods, new MemberComparator<Method>());
        
        for (Object target : data) {
            TableRow row = new ColoredTableRow(this, RowColors.getColor(table.getChildCount()));
            for (Method method : methods) {
                String value = "";
                if (isAccessor(method.getName())) {
                    try {
                        value = method.invoke(target).toString();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                row.addView(new CellTextView(this, value, padding, textSize));
            }
            table.addView(row);
        }
    }
    
    public boolean isAccessor(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }

    public void addModelFiledHeaders(TableLayout table, Inspector.ModelMapInspector inspector) {
        // Get Fields for a given model
        String modelKey = getIntent().getStringExtra(EXTRA_MODEL_KEY);
        Class model = inspector.getModelMap().get(modelKey);
        model.getDeclaredFields();
        Field[] allFields = model.getDeclaredFields();
        Arrays.sort(allFields, new MemberComparator<Field>());

        // Add the field headers to the table row
        TableRow headers = new ColoredTableRow(this, RowColors.getColor(table.getChildCount()));
        for (Field field : allFields) {
            headers.addView(new CellTextView(this, field.getName(), padding, textSize));
        }
        table.addView(headers);
    }
    
    public class CellTextView extends TextView {
        public CellTextView(Context context, String value, int padding, int textSize) {
            super(context);
            setPadding(padding, padding, padding, padding);
            setTextSize(textSize);
            setText(value);
        }
    }
    
    public class ColoredTableRow extends TableRow {
        public ColoredTableRow(Context context, int color) {
            super(context);
            setBackgroundColor(color);
        }
    }

    public class MemberComparator<T extends Member> implements Comparator<T> {
        @Override
        public int compare(T lhs, T rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }

    public static class RowColors {
        public static int OddRowColor = Color.parseColor("#CCCCCC");
        public static int EvenRowColor = Color.parseColor("#ffffff");

        public static int getColor(int index) {
            if (index % 2 == 0)
                return EvenRowColor;
            return OddRowColor;
        }
    }
}
