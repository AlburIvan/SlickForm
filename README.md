## SlickForm

[![CircleCI](https://img.shields.io/appveyor/ci/gruntjs/grunt/master.svg)](https://circleci.com/gh/AlburIvan/SlickForm/19)
[![Twitter](https://img.shields.io/badge/Twitter-%40AlburIvan-blue.svg?style=flat)](https://twitter.com/AlburIvan)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7b66c5b5651044bb9c7a941bec7f0efc)](https://www.codacy.com/app/albur-ivan/SlickForm?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AlburIvan/SlickForm&amp;utm_campaign=Badge_Grade)
[![Jitpack Badge](https://jitpack.io/v/AlburIvan/SlickForm.svg)](https://jitpack.io/#AlburIvan/SlickForm)


Based on [This awesome design][slick-form-page] from [Josh Cummings][dribbble-profile]. SlickForm is an Android library where you define a custom array of EditTexts with the purpose of handling a form in a cool animated way.


#### Table of Contents

* [Demo](#demo)
* [Integration](#integration)
* [Usage](#usage)
* [Thats it?](#thats-it)
* [Credits](#credits)


## Demo

![][slick-form-demo]


## Integration

To try this library into your build:

Step 1. Add the JitPack repository to your project build.gradle:

```groovy
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```    
Step 2. Add the dependency
```groovy
	dependencies {
		compile 'com.github.AlburIvan:SlickForm:v1.2'
	}
```

## Usage

In XML:
```xml
<com.alburivan.slickform.SlickForm
        android:id="@+id/slick_form"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:slick_tooltipEnabled="true" />
```

Extra attributes available:
```xml
 <declare-styleable name="SlickSignForm">
        <attr name="slick_buttonBgColor" format="color" />
        <attr name="slick_buttonFgColor" format="color" />
        <attr name="slick_tooltipEnabled" format="boolean" />
        <attr name="slick_tooltipColor" format="color" />
</declare-styleable>
```    


Default behavior (3 fields: email, user, & password):
```java
 SlickForm slickForm =
                (SlickForm) findViewById(R.id.slick_form);
                
 slickForm.withDefaultFields()
            .setOnProcessChangeListener(new IOnProcessChange() {
                @Override
                public boolean workInBackground(List<FormField> param) {

                    final String message = String.format(Locale.ENGLISH,
                            "This form is doing work in background and the values are: first field: %s - second field: %s  - third field: %s",
                            param.get(0).getInputFieldText(), param.get(1).getInputFieldText(), param.get(2).getInputFieldText()
                    );

                    Log.d("TAG", message);

                    // if all goes good, return true, if it failed return false
                    return true;
                }

                @Override
                public void workFinished() {
                    Log.d("TAG", "Done");
                }
            })
            .ready();
```

## Thats it?

Yeah basically...

Only those fields?? Is it for signing in only??

Not really... You can extend it to your needs

1. Create FormFields Objects

```java
	FormField userField = new FormField(getApplicationContext())
            .withType(FieldsType.TEXT)
            .withHint("Username")
            .withLabel("Hit me"); // optional - default: Next

    FormField emailField = new FormField(getApplicationContext())
            .withType(FieldsType.EMAIL)
            .withHint("Email");

    FormField passField = new FormField(getApplicationContext())
            .withType(FieldsType.PASSWORD)
            .withCustomValidation(new IOnCustomValidation() {

                    // Add your own custom validation if neccesarry.
                    @Override
                    public boolean withCustomValidation(FormField field) {

                        String password      = field.getInputFieldText();

                        boolean hasUppercase = !password.equals(password.toLowerCase());
                        boolean hasLowercase = !password.equals(password.toUpperCase());

                        boolean isAtLeast8   =  password.length() >= 8;

                        // return true if validation is successful, otherwise false
                        return (hasUppercase && hasLowercase) && isAtLeast8;
                    }
                })
            .withHint("Password");
```

2. Add them your SlickForm Object

```java
 	SlickForm slickForm =
                	(SlickForm) findViewById(R.id.slick_form);

 	slickForm
            .withField(userField)
            .withField(emailField)
            .withField(passField) // chain any number of fields in the order of appearance
            .setOnProcessChangeListener(new IOnProcessChange() {
                    @Override
                    public boolean workInBackground(List<FormField> param) {

                        final String message = String.format(Locale.ENGLISH,
                                "This form is doing work in background and the values are: first field: %s - second field: %s  - third field: %s",
                                param.get(0).getInputFieldText(), param.get(1).getInputFieldText(), param.get(2).getInputFieldText()
                        );

                        Log.d("TAG", message);

                        // if all goes good, return true, if it failed return false
                        return true;
                    }

                    @Override
                    public void workFinished() {
                        Log.d("TAG", "Done");
                    }
                })
            .withProcessingLabel("Sending")    
            .ready();
```


## Extras

FormField available methods

|  Method   | Description 																																					|  Usage     | 
|-----------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|------------|
| withType  |  Add this form field's type so it can get validated correctly													 												| FieldType  |
| withHint  |  Add this form field's hint to let the user know what needs to be filled in. 																					| String  	 |
| withIcon  |  Add this form field's icon for avisual cue of what needs to be filled in. 																					| Drawable/SVG   |
| withLabel	|  Customize this form field's button label. Current default is "Next"																							| String     |
| withProcessingLabel   | Changes the form's is label when its doing background work                                                                                        | String |
| withCustomValidation |  Assign this FormField an unique validation 																										| IOnCustomValidation |




## Credits
Thanks to [Josh Cummings][dribbble-profile] for the [UI][slick-form-page] design  
Thanks to [Douglas Nassif Roma Junior][tooltip-library] for the awesome Tooltip Effect library	
Thanks to [Georgi Eftimov][svg-library] for the SVG path view library	
Thanks to [Perxis][perxis-link] for the SVG line icons


## License

	Copyright 2016 AlburIvan
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	


[dribbble-profile]: https://dribbble.com/joshcummingsdesign
[slick-form-page]: http://www.materialup.com/posts/sign-up-e226cb9b-e06d-4e8c-ba28-3e5837e1cd41
[tooltip-library]: https://github.com/douglasjunior/android-simple-tooltip
[svg-library]: https://github.com/geftimov/android-pathview
[perxis-link]: https://perxis.com 
[slick-form-demo]: https://raw.githubusercontent.com/AlburIvan/SlickForm/master/slick_form_demo.gif
