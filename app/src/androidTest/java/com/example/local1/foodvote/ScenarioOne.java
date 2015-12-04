package com.example.local1.foodvote;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.ViewInteraction.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.*;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.Before;

import java.util.List;

public class ScenarioOne extends ActivityInstrumentationTestCase2<LoginSignUpActivity> {

    private LoginSignUpActivity LSA;

    public ScenarioOne() {
        super(LoginSignUpActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        LSA = getActivity();
    }

    public void testSigningIn() {
        onView(withId(R.id.usernameText))
                .perform(typeText("angela"), closeSoftKeyboard());
        onView(withId(R.id.passwordText))
                .perform(typeText("1234"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
    }
}

