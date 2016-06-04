package com.alburivan.slicksignform;

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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alburivan.slicksignform.animators.ProgressBarAnimation;
import com.alburivan.slicksignform.interfaces.IOnCustomValidation;
import com.alburivan.slicksignform.interfaces.IOnProcessChange;
import com.alburivan.slicksignform.tooltip.SimpleTooltip;
import com.eftimoff.androipathview.PathView;

import java.util.ArrayList;
import java.util.List;

import static com.alburivan.slicksignform.FieldsType.PASSWORD;
import static com.alburivan.slicksignform.FieldsType.TEXT;

/**
 * (っ･_･)っ
 * SlickForm is a custom array of EditTexts with the purpose of handling a form in a cool animated way.
 * The original designer is Josh Cummings https://dribbble.com/shots/2718066-Daily-UI-001
 *
 *  @author Iván Alburquerque
 *  @version 1.0.0
 */
public class SlickForm extends LinearLayout {


    /** Custom tag used by the SlickForm to output logging information. */
    private final String DEBUG_TAG = SlickForm.class.getCanonicalName();

    /*
    |--------------------------------------------------------------------------
    | Default values
    |--------------------------------------------------------------------------
    |
    | The following language lines are used during compile execution. These
    | are the default values used along the different elements that
    | compose this custom time picker slickEndAnimationContainer.
    |
    */
    private Context mContext;
    private RelativeLayout slickFieldContainer;
    private RelativeLayout slickEndAnimationContainer;
    private ProgressBar slickFormProgressBar;
    private ProgressBar slickEndAnimationProgressBar;
    private Button slickFormSubmitButton;
    private PathView slickSVGIcon;


    private int currentFieldPosition                  = -1;
    private final int MINIMUM_CHARACTERS_INPUT        = 4;
    private final int MINIMUM_NAME_CHARACTERS_INPUT   = 1;


    /*
    |--------------------------------------------------------------------------
    | Custom View Properties
    |--------------------------------------------------------------------------
    |
    | The following language lines are used during compile execution. These
    | are the principal properties that FlingTimePicker can adquire during
    | runtime execution.
    |
    */

    private List<FormField> formFields = new ArrayList<>();
    private IOnProcessChange mActionListener;
    private boolean isTooltipEnabled = true;
    private int slickButtonBackgroundColor;
    private int slickButtonForegroundColor;




    /**
     * Instantiates a new PairingCode field.
     *
     * @param context the context used to create this view
     */
    public SlickForm(Context context) {
        super(context);
    }

    /**
     * Instantiates a new PairingCode field.
     *
     * @param context the context used to create this view
     * @param attrs The attributes used to initialize fields
     */
    public SlickForm(Context context, AttributeSet attrs){
        super(context,attrs);
        initAttrs(context, attrs);
    }

    /**
     * Instantiates a new PairingCode field.
     *
     * @param context The context used to create this view
     * @param attrs The attributes used to initialize fields
     * @param defStyle the default style attributes
     */
    public SlickForm(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
    }

    /**
     * Method that initializes this custom view's default values and sets listeners
     *
     * @param context The context used to create this view
     * @param attrs The attributes used to initialize fields
     */
    private void initAttrs(final Context context, final AttributeSet attrs) {
        if (isInEditMode())
            return;

        final TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.SlickForm);

        try {

            mContext                    = context;

            slickButtonForegroundColor  = typedArray.getColor(R.styleable.SlickForm_slick_buttonFgColor, context.getResources().getColor(R.color.colorWhite));
            slickButtonBackgroundColor  = typedArray.getColor(R.styleable.SlickForm_slick_buttonBgColor, context.getResources().getColor(R.color.colorPurple));
            isTooltipEnabled            = typedArray.getBoolean(R.styleable.SlickForm_slick_tooltipEnabled, true);

            LinearLayout mRootView      = (LinearLayout) inflate(context, R.layout.library_main_layout, this);
            slickFieldContainer         = (RelativeLayout) mRootView.findViewById(R.id.slick_form_field_container);
            slickFormProgressBar        = (ProgressBar) mRootView.findViewById(R.id.slick_form_progress);
            slickSVGIcon                = (PathView) mRootView.findViewById(R.id.svgIcon);
            slickEndAnimationContainer  = (RelativeLayout) mRootView.findViewById(R.id.slick_form_end_anim_container);
            slickEndAnimationProgressBar = (ProgressBar) mRootView.findViewById(R.id.slick_form_end_progress_bar);


            slickFormSubmitButton       = (Button) mRootView.findViewById(R.id.slick_form_submit_button);
            slickFormSubmitButton.setBackgroundColor(slickButtonBackgroundColor);
            slickFormSubmitButton.setTextColor(slickButtonForegroundColor);
            slickFormSubmitButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentFieldPosition >= formFields.size()) {
                        return;
                    }
                    processFormState();
                }
            });
        } finally {
            typedArray.recycle();
        }

    }

    /**
     * This method controls the form's state flow and subsequently process all fields added to this
     * form with their respective validations. Once the list gets to the last element it will
     * initiate the process to end this form and send the control to the developer for
     * implementation purposes.
     *
     */
    private void processFormState() {

        if(currentFieldPosition == -1) {
            Log.d(DEBUG_TAG,"process(): begin");
            processFormFieldBegin();
            return;
        }

        FormField field =  formFields.get(currentFieldPosition);

        if (field != null)
            processFormField(field);
        else
            processFormFieldEnd();
    }

    /**
     * Initialize the form submition progress by enabling the input field
     *
     */
    private void processFormFieldBegin() {

        currentFieldPosition++;
        FormField field = formFields.get(currentFieldPosition);

        slickFormSubmitButton.setText(field.getStepLabel());

        slickFieldContainer.setVisibility(View.VISIBLE);
        slickFieldContainer.setAlpha(0.0f);

        slickFieldContainer.animate()
                .translationY(slickFormSubmitButton.getY())
                .alpha(1.0f);

        applySlideDownAnimationTo(slickFormSubmitButton);

        slickFieldContainer.addView(field);
    }


    /**
     * This method process the fields values given, after validation it will change on to the
     * next target in the list.
     *
     * @param field The current form field to be evaluated
     */
    private void processFormField(FormField field) {

        if(!validateView(field, field.getFormFieldType())){
            showWarningDialog();
            return;
        }

        applySlideLeftAnimationTo(slickFieldContainer.getChildAt(0))
                .setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        changeTarget();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
    }


    /**
     *
     *
     */
    public void processFormFieldEnd() {
        slickFormSubmitButton.setText(R.string.form_field_progress);
        slickFieldContainer.setVisibility(View.GONE);
        slickFormProgressBar.setVisibility(View.VISIBLE);

        // TODO: 5/23/2016 replace with async task
        new DutyAsyncTask().execute(getFields());
    }


    /**
     *
     *
     */
    private void changeTarget() {
        currentFieldPosition++;

        if(currentFieldPosition >= formFields.size()) {
            processFormFieldEnd();
            return;
        }

        if(currentFieldPosition == formFields.size() -1) {
            slickFormSubmitButton.setText(
                    formFields.get(currentFieldPosition).getStepLabel().equals("Next") ?
                            "Submit" : formFields.get(currentFieldPosition).getStepLabel()
            );

            slickFieldContainer.removeViewAt(0);
            slickFieldContainer.addView(formFields.get(currentFieldPosition));
        } else {
            slickFieldContainer.removeViewAt(0);
            slickFieldContainer.addView(formFields.get(currentFieldPosition));
            slickFormSubmitButton.setText(formFields.get(currentFieldPosition).getStepLabel());
        }

        slickFormSubmitButton.invalidate();
    }



    /**
     * Initialize the SlickSignForm with the 3 default steps which are:
     * Username, Email, and Password
     *
     * @return This instance of SlickForm
     */
    public SlickForm withDefaultFields(){

        FormField userField = new FormField(mContext)
                .withType(TEXT)
                .withIcon(R.raw.ic_slick_user)
                .withHint(mContext.getString(R.string.form_field_username));

        FormField emailField = new FormField(mContext)
                .withType(TEXT)
                .withIcon(R.raw.ic_slick_email)
                .withHint(mContext.getString(R.string.form_field_email));

        FormField passField = new FormField(mContext)
                .withType(PASSWORD)
                .withIcon(R.raw.ic_slick_lock)
                .withHint(mContext.getString(R.string.form_field_password));

        getFields().add(userField);
        getFields().add(emailField);
        getFields().add(passField);

        return this;
    }

    /**
     * Adds a new field to the SlickForm's fields collection
     *
     * @param field The {@link FormField} object to add
     * @return This instance of SlickForm
     */
    public SlickForm withField(FormField field){
        getFields().add(field);
        return this;
    }

    /**
     * Adds a new fields to the SlickForm's fields collection
     *
     * @param fields The {@link FormField} array object to add
     * @return This instance of SlickForm
     */
    public SlickForm withFields(FormField[] fields){
        for(FormField field : fields)
            getFields().add(field);

        return this;
    }


    /** does nothing, but looks cool huh? */
    public void ready() {
        // Do something... ?
    }



    public SlickForm setOnProcessChangeListener(IOnProcessChange listener) {
        this.mActionListener = listener;
        return this;
    }



    public List<FormField> getFields(){
        return formFields;
    }


    /**
     *
     */
    private class DutyAsyncTask extends AsyncTask<List<FormField>, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(List<FormField>... params) {
            if (Looper.myLooper() == null)
                Looper.prepare();

            return mActionListener != null && mActionListener.workInBackground(params[0]);
        }

        @Override
        protected void onPostExecute(final Boolean state) {
            super.onPostExecute(state);

            ProgressBarAnimation anim = new ProgressBarAnimation(slickFormProgressBar, 0.0f, 100.0f);
            anim.setInterpolator(new AccelerateInterpolator());
            anim.setDuration(1000);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {

                    Animation fadeOut = new AlphaAnimation(1, 0);
                    fadeOut.setInterpolator(new AccelerateInterpolator());
                    fadeOut.setDuration(200);

                    AnimationSet set = new AnimationSet(true);
                    set.addAnimation(fadeOut);
                    set.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            slickFieldContainer.setVisibility(GONE);
                            slickFormSubmitButton.setVisibility(GONE);
                            slickFormProgressBar.setVisibility(GONE);
                            slickEndAnimationContainer.setVisibility(VISIBLE);

                            ObjectAnimator anims = ObjectAnimator.ofInt (slickEndAnimationProgressBar, "progress", 0, 500);
                            anims.setDuration (450);
                            anims.setInterpolator (new DecelerateInterpolator());
                            anims.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {}

                                @Override
                                public void onAnimationEnd(Animator animation) {

                                    slickSVGIcon = (PathView) findViewById(R.id.svgIcon);
                                    slickSVGIcon.setVisibility(VISIBLE);

                                    if(!state)
                                        slickSVGIcon.setSvgResource(R.raw.ic_cross_mark);

                                    slickSVGIcon.getPathAnimator()
                                            .delay(20)
                                            .duration(500)
                                            .interpolator(new AccelerateDecelerateInterpolator())
                                            .start();

                                    slickSVGIcon.setFillAfter(true);

                                    mActionListener.workFinished();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {}

                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                            });

                            anims.start();
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });

                    startAnimation(set);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            slickFormProgressBar.startAnimation(anim);
        }
    }



    /**
     * Displays a warning tooltip at the end of the view, showing to the user that the input has
     * some errores that need to be resolved before continuing.
     */
    private void showWarningDialog() {
        if(isTooltipEnabled) {

            SimpleTooltip.Builder builder = new SimpleTooltip.Builder(mContext);
            builder.anchorView(slickFieldContainer.getChildAt(0));
            builder.text("     !     ");
            builder.gravity(Gravity.END);
            builder.animated(true);
            builder.transparentOverlay(true);

            SimpleTooltip mTooltip = builder.build();

            if (!mTooltip.isShowing())
                mTooltip.show();
        }
    }

    /**
     * This method validate the view given with the state that they represent. That means that it will
     * return false if the {@link FormField} is empty or contains illegal characters or does
     * not match a pattern, and if enabled, it will present a warning as a tooltip.
     *
     * <p>
     *     In addition you can add your own custom validation if needed by calling
     *     {@link FormField#withCustomValidation(IOnCustomValidation)} on the field(s)
     *     required and implementing your own validation logic.
     * </p>
     *
     * @param field The view supplied for validation
     * @param type The type comparition
     * @return {@code false} if invalid or {@code true} otherwise
     */
    private boolean validateView(FormField field, FieldsType type) {

        if(type.equals(FieldsType.TEXT)) {
            if (field.getInputField().getText().toString().trim().length() <= MINIMUM_NAME_CHARACTERS_INPUT)
                return false;
        }

        else if(type.equals(FieldsType.EMAIL)) {
            if(field.getInputField().getText().toString().trim().length() <= MINIMUM_CHARACTERS_INPUT)
                return false;

            if(!isEmailValid(field.getInputField().getText().toString()))
                return false;
        }

        else if(type.equals(FieldsType.PASSWORD)) {
            if(field.getInputField().getText().toString().trim().length() <= MINIMUM_CHARACTERS_INPUT)
                return false;
        }

        else if(type.equals(FieldsType.CUSTOM) && field.getCallback() != null) {
            return field.getCallback().withCustomValidation(field);
        }

        return true;
    }

    /**
     * Checks whether the supplied string matches an Email address pattern or not and return
     * if its valid.
     *
     * @param email The String to validate
     * @return {@code true } if string matches email patterns, {@code false} otherwise
     */
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /** Applies a slide down animation relative to itself to the view supplied */
    private Animation applySlideDownAnimationTo(View view) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );

        animation.setDuration(100);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);

        return animation;
    }

    /** Applies a slide down animation relative to itself to the view supplied */
    private Animation applySlideLeftAnimationTo(View view) {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        );
        animation.setDuration(200);
        animation.setInterpolator(new AccelerateInterpolator());
        view.startAnimation(animation);

        return animation;
    }
}
