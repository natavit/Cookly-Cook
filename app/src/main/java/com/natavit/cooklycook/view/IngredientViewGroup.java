package com.natavit.cooklycook.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.inthecheesefactory.thecheeselibrary.view.state.BundleSavedState;
import com.natavit.cooklycook.R;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class IngredientViewGroup extends BaseCustomViewGroup {

    EditText etIngName;
    EditText etIngAmount;

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

    public void setEtIngName(String name) {
        this.etIngName.setText(name);
    }

    public void setEtIngAmount(String amount) {
        this.etIngAmount.setText(amount);
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

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        BundleSavedState ss = (BundleSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        Bundle bundle = ss.getBundle();
        // Restore State from bundle here
        etIngName.setText(bundle.getString("name"));
        etIngAmount.setText(bundle.getString("amount"));
    }

}
