## SlickForm

[![CircleCI](https://img.shields.io/circleci/project/BrightFlair/PHP.Gt.svg?maxAge=2592000)](https://circleci.com/gh/AlburIvan/SlickForm/1)
[![Twitter](https://img.shields.io/badge/Twitter-%40AlburIvan-blue.svg?style=flat)](https://twitter.com/AlburIvan)


Based on [This awesome design][slick-form-page] from [Josh Cummings][dribbble-profile]. SlickForm is an Android library where you define a custom array of EditTexts with the purpose of handling a form in a cool animated way.


#### Table of Contents

* [Demo](#demo)
* [Integration](#integration)
* [Usage](#usage)
* [Thats it?](#thats-it)
* [Credits](#credits)


## Demo

## Integration

To try this library into your build:

Step 1. Add the JitPack repository to your project build.gradle:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
    
Step 2. Add the dependency

	dependencies {
		compile '....'
	}


## Usage

In XML:
```xml
<com.alburivan.slicksignform.SlickSignForm
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

 slickSignForm.withDefaultFields()
                    .setOnProcessChangeListener(new IOnProcessChange() {
                         @Override
                        public void onAsyncStart(List<FormField> param) {
                            final String message = String.format(Locale.ENGLISH,
                                    "This form is finished and the values are: first field: %s - second field: %s  - third field: %s",
                                    param.get(0).getInputFieldText(), param.get(1).getInputFieldText(), param.get(2).getInputFieldText()
                            );

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onAsyncFinished() {
                            Log.d("TAG", "done");
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
            .withLabel("hit me"); // optional - default: Next

    FormField emailField = new FormField(getApplicationContext())
            .withType(FieldsType.EMAIL)
            .withHint("Email");

    FormField passField = new FormField(getApplicationContext())
            .withType(FieldsType.PASSWORD) // can be ommitted, read below...
            .withCustomValidation(new IOnCustomValidation() {

            	// add your own custom validation if neccesarry. WARNING: it will override the FieldType to CUSTOM
                @Override
                public boolean withCustomValidation(FormField field) {

                    String password 	 = field.getInputField().getText().toString();

                    boolean hasUppercase = !password.equals(password.toLowerCase());
                    boolean hasLowercase = !password.equals(password.toUpperCase());

                    boolean isAtLeast8   =  password.length() >= 8;
                    boolean hasSpecial   = !password.matches("[A-Za-z0-9 ]*");

                    if( (!hasUppercase && !hasLowercase) || !isAtLeast8 || !hasSpecial )
                        return false;
                    
                    return true;
                }
            })
            .withHint("Password");
```

2. Add them your SlickForm Object

```java

 	SlickSignForm slickSignForm =
                	(SlickSignForm) findViewById(R.id.slick_form);

 	slickSignForm
            .withField(userField)
            .withField(emailField)
            .withField(passField) // chain any number of fields in the order of appearance
            .setOnProcessChangeListener(new IOnProcessChange() {
                    @Override
                    public void onAsyncStart(List<FormField> param) {
                        final String message = String.format(Locale.ENGLISH,
                                "This form is finished and the values are: first field: %s - second field: %s  - third field: %s",
                                param.get(0).getInputFieldText(), param.get(1).getInputFieldText(), param.get(2).getInputFieldText()
                        );

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onAsyncFinished() {
                        Log.d("TAG", "done");
                    }
                })
                .ready();

```


## Extras

withType

withHint

withIcon

withLabel

## Credits
Thanks to [Josh Cummings][dribbble-profile] for the [UI][slick-form-page] inspiration  
Thanks to [Douglas Nassif Roma Junior][tooltip-library] for the awesome Tooltip Effect library


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