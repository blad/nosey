package com.btellez.noseyexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

import io.realm.Realm;
import io.realm.RealmResults;

public class DisplayModelActivity extends Activity {

    private final static String EXTRA_MODEL_KEY = "extra_model_key";
    private int textSize;
    private int padding;
    private Method[] methods;
    private Class model;
    private Context context;

    public static void startActivity(Context context, String modelName) {
        Intent intent = new Intent(context, DisplayModelActivity.class);
        intent.putExtra(EXTRA_MODEL_KEY, modelName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        textSize = (int) getResources().getDimension(R.dimen.cell_text_size);
        padding = (int) getResources().getDimension(R.dimen.cell_padding);
        
        Nosey nosey = Nosey.getInstance(this);
        Inspector.ModelMapInspector inspector = new Inspector.ModelMapInspector();
        inspector.inspect(nosey);

        String modelKey = getIntent().getStringExtra(EXTRA_MODEL_KEY);
        model = inspector.getModelMap().get(modelKey);
        methods = model.getDeclaredMethods();
        Arrays.sort(methods, new MemberComparator<Method>());
        
        TableLayout table = new TableLayout(this);
        addModelFieldHeaders(table, inspector);
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
        Realm realm = Realm.getInstance(this);
        RealmResults data = realm.where(model).findAll();
        for (Object target : data) {
            TableRow row = new ColoredTableRow(this, RowColors.getColor(table.getChildCount()));
            int highlight = Color.parseColor("#336699");
            for (final Method method : methods) {
                String value = "";
                TextView cell;
                Object returnValue;
                try {
                    if (isAccessor(method.getName()) && !Modifier.isStatic(method.getModifiers())) {
                        returnValue = method.invoke(target);
                        value = returnValue == null ? "null" : returnValue.toString();
                        cell = new CellTextView(this, value, padding, textSize);
                        if (Nosey.getInstance(this).isRegistered(method.getReturnType().getSimpleName()) && returnValue != null) {
                            // Highlight Other Realm Models & Link to them
                            cell.setTextColor(highlight); 
                            cell.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DisplayModelActivity.startActivity(context, method.getReturnType().getSimpleName());
                                }
                            });
                        }
                        row.addView(cell);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            table.addView(row);
        }
    }
    
    public boolean isAccessor(String name) {
        return name.startsWith("get") || name.startsWith("is");
    }

    public void addModelFieldHeaders(TableLayout table, Inspector.ModelMapInspector inspector) {
        // Get Fields for a given model
        model.getDeclaredFields();
        Field[] allFields = model.getDeclaredFields();
        Arrays.sort(allFields, new MemberComparator<Field>());

        // Add the field headers to the table row
        TableRow headers = new ColoredTableRow(this, RowColors.getColor(table.getChildCount()));
        for (Field field : allFields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                headers.addView(new CellTextView(this, field.getName(), padding, textSize));
            }
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
