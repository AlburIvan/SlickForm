package com.alburivan.slickform;
/*
* Copyright 2016 AlburIvan [Ivan Alberto Alburquerque Mejia]
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alburivan.slickform.interfaces.IOnCustomValidation;

import static com.alburivan.slickform.FieldsType.CUSTOM;
import static com.alburivan.slickform.FieldsType.PASSWORD;
import static com.alburivan.slickform.FieldsType.TEXT;

/**
 * (っ･_･)っ
 *  FormField is a custom RelativeLayout that contains a compound view consisting of an icon and
 *  a normal EditText for inputs
 *
 *  @author Iván Alburquerque
 *  @version 1.0.0
 */
public class FormField extends RelativeLayout {

    /** Custom tag used by the PairingCodeText to output logging information. */
    protected final String DEBUG_TAG = SlickForm.class.getCanonicalName();

    private IOnCustomValidation callback = null;


    private RelativeLayout mRootView;
    private ImageView mIconView;
    private EditText mFieldInput;
    private FieldsType formFieldType;
    private String stepLabel = "Next";


    /**
     * Instantiates a new FormField view.
     *
     * @param context the context used to create this view
     */
    public FormField(Context context) {
        super(context);
        initAttrs(context, TEXT, R.drawable.ic_slick_user, "Username");
    }

    /**
     * Instantiates a new FormField view.
     *
     * @param context the context used to create this view
     * @param attrs The attributes used to initialize fields
     */
    public FormField(Context context, AttributeSet attrs){
        super(context, attrs);
        initAttrs(context, TEXT, R.drawable.ic_slick_user, "Username");
    }

    /**
     * Instantiates a new FormField view.
     *
     * @param context The context used to create this view
     * @param attrs The attributes used to initialize fields
     * @param defStyle the default style attributes
     */
    public FormField(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initAttrs(context, TEXT, R.drawable.ic_slick_user, "Username");
    }

    /**
     * Instantiates a new FormField view.
     *
     * @param context  The context used to create this view
     * @param type The field's type
     * @param resId Resource Id for the icon
     * @param hint The user hint for the EditText
     */
    public FormField(Context context, FieldsType type, int resId, String hint){
        super(context);
        initAttrs(context, type, resId, hint );
    }

    /**
     * Method that initializes this custom view's default values and sets listeners
     *
     * @param context The context used to create this view
     * @param type The field's type
     * @param resId Resource Id for the icon
     * @param hint The user hint for the EditText
     */
    private void initAttrs(Context context, FieldsType type, int resId, String hint) {
        try {

            this.mRootView                   = (RelativeLayout) inflate(context, R.layout.library_form_field_layout, this);
            this.mIconView                   = (ImageView) mRootView.findViewById(R.id.slick_form_text_icon);
            this.mFieldInput                 = (EditText) mRootView.findViewById(R.id.slick_form_text_input);

            LayoutParams params =
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            params.addRule(RelativeLayout.RIGHT_OF, mIconView.getId());

            this.mIconView.setImageResource(resId);
            this.mFieldInput.setLayoutParams(params);
            this.mFieldInput.setHint(hint);
            this.formFieldType = type;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Add this form field's type so it can get validated correctly according to the type.
     * if no type is selected it will automatically be {@code FieldsType.TEXT}.
     *
     * <p>
     *     If {@code FieldsType.PASSWORD} is used, it will set the EditText is input type
     *     to {@code TYPE_TEXT_VARIATION_PASSWORD}, which means every character will appear
     *     as an asterisk.
     * </p>
     *
     * @param type The specified type to validate this input against
     * @return This FormField instance
     */
    public FormField withType(FieldsType type) {
        this.formFieldType = type;

        if(type.equals(PASSWORD))
            setPasswordFieldEnabled(true);

        return this;
    }

    /**
     * Add this form field's hint to let the user know what needs to be filled in.
     *
     * @param hint A string to help the user fill the input
     * @return This FormField instance
     */
    public FormField withHint(String hint) {
        this.mFieldInput.setHint(hint);
        return this;
    }

    /**
     * Add this form field's icon to give the user a visual cue of what needs to be filled in.
     *
     * @param resId Resource id of the image drawable
     * @return This FormField instance
     */
    public FormField withIcon(int resId) {
        this.mIconView.setImageResource(resId);
        return this;
    }

    /**
     * Customize this form field's button label. Current default is "Next"
     *
     * @param label The button's new label
     * @return This FormField instance
     */
    public FormField withLabel(String label) {
        this.stepLabel = label;
        return this;
    }

    /**
     * Assign this FormField an unique validation with {@link IOnCustomValidation}. This will
     * override any previous call to {@link #withType}
     *
     * @param callback The button's new label
     * @return This FormField instance
     */
    public FormField withCustomValidation(IOnCustomValidation callback) {
        this.callback = callback;
        this.formFieldType = CUSTOM;

        return this;
    }

    /**
     * Converts this FormField's EditText into a password field
     *
     * @param state {@code true} to set the field as password type, {@code false} otherwise
     * @return This FormField instance
     */
    public FormField setPasswordFieldEnabled(boolean state) {
        if(state)
            this.mFieldInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        else
            this.mFieldInput.setInputType(InputType.TYPE_CLASS_TEXT);

        return this;
    }


    public FieldsType getFormFieldType() {
        return formFieldType;
    }

    public void setFormFieldType(FieldsType formFieldType) {
        this.formFieldType = formFieldType;
    }


    public String getStepLabel() {
        return stepLabel;
    }

    public void setStepLabel(String stepLabel) {
        this.stepLabel = stepLabel;
    }

    public ImageView getIconField() {
        return mIconView;
    }

    public void setmIconView(ImageView mIconView) {
        this.mIconView = mIconView;
    }

    public EditText getInputField() {
        return mFieldInput;
    }

    public String getInputFieldText() {
        return mFieldInput.getText().toString();
    }

    public void setInputField(EditText mFieldInput) {
        this.mFieldInput = mFieldInput;
    }

    public IOnCustomValidation getCallback() {
        return callback;
    }
}