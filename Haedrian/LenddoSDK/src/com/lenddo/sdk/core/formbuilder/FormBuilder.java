package com.lenddo.sdk.core.formbuilder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.lenddo.sdk.R;

/**
 * Created by joseph on 9/3/14.
 */
public class FormBuilder {

    private final ViewGroup container;
    private final Activity context;

    public FormBuilder(Activity context, int resId) {
        this.container = (ViewGroup)context.findViewById(resId);
        this.context = context;
    }

    public FormBuilder(Activity context, ViewGroup container) {
        this.container = container;
        this.context = context;
    }

    public TextFieldBuilder addTextField(String name, String label) {
        TextFieldBuilder builder = new TextFieldBuilder(context, this);
        builder.setLabel(label);
        return builder;
    }

    public Activity getContext() {
        return context;
    }

    public class TextFieldBuilder {
        private final FormBuilder builder;
        private final EditText fieldView;
        ViewGroup view;
        TextView labelView;

        protected TextFieldBuilder(Activity context, FormBuilder builder) {
            this.builder = builder;
            view = (ViewGroup)context.getLayoutInflater().inflate(R.layout.text_field_template, null);
            fieldView = (EditText)view.findViewById(R.id.field);
            labelView = (TextView)view.findViewById(R.id.label);
            builder.add(view);
        }

        public void setLabel(String label) {
            labelView.setText(label);
        }

    }

    private void add(View view) {
        container.addView(view);
    }
}
