package com.natavit.cooklycook.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.natavit.cooklycook.R;
import com.natavit.cooklycook.view.state.BundleSavedState;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class IngredientViewGroup extends BaseCustomViewGroup {

    private EditText etIngName;
    private EditText etIngAmount;
    private Spinner spinner;

    private String[] units;
    private String spinnerItem;

    private ArrayAdapter<String> aa;

    public IngredientViewGroup(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public IngredientViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs, 0, 0);
    }

    public IngredientViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public IngredientViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInflate();
        initInstances();
        initWithAttrs(attrs, defStyleAttr, defStyleRes);
    }

    private void initInflate() {
        inflate(getContext(), R.layout.ingredient_layout, this);
    }

    private void initInstances() {
        // findViewById here
        etIngName = (EditText) findViewById(R.id.etIngName);
        etIngAmount = (EditText) findViewById(R.id.etIngAmount);
        spinner = (Spinner) findViewById(R.id.spinner);
        createSpinnerUnit();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerItem = units[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinnerItem = units[0];
            }
        });
    }

    private void createSpinnerUnit() {
        units = getResources().getStringArray(R.array.unit);
        aa = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        units);
        spinner.setAdapter(aa);
    }

    private void initWithAttrs(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        /*
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StyleableName,
                defStyleAttr, defStyleRes);

        try {

        } finally {
            a.recycle();
        }
        */
    }

    public String getIngredientName() {
        return etIngName.getText().toString();
    }

    public String getIngredientAmount() {
        return etIngAmount.getText().toString();
    }

    public String getIngredientUnit() {
        return spinnerItem;
    }

    public void setIngredientName(String name) {
        this.etIngName.setText(name);
    }

    public void setIngredientAmount(String amount) {
        this.etIngAmount.setText(amount);
    }

    public void setIngredientUnit(String item) {
        if (!item.equals(null)) {
            int pos = aa.getPosition(item);
            this.spinner.setSelection(pos);
            spinnerItem = item;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        BundleSavedState savedState = new BundleSavedState(superState);
        // Save Instance State(s) here to the 'savedState.getBundle()'
        // for example,
        // savedState.getBundle().putString("key", value);
        savedState.getBundle().putString("name", getIngredientName());
        savedState.getBundle().putString("amount", getIngredientAmount());
        savedState.getBundle().putString("unit", getIngredientUnit());

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
        setIngredientName(bundle.getString("name"));
        setIngredientAmount(bundle.getString("amount"));
        setIngredientUnit(bundle.getString("unit"));

    }

}
