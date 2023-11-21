package com.example.findaseat;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignOutTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private String username = "loaf";
    private String password = "loafcat";

    private String username1 = "blep";
    private String password1 = "blepcat";

    @Test
    public void displaySignOutDialogTest() throws InterruptedException {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.uscidEditText), isDisplayed()));
        appCompatEditText.perform(replaceText(username), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.passwordEditText), isDisplayed()));
        appCompatEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
        materialButton.perform(click());

        Thread.sleep(10000);

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.logout_button), withText("Log Out"), isDisplayed()));
        materialButton2.perform(click());

        Thread.sleep(10000);

        ViewInteraction textView = onView(
                allOf(IsInstanceOf.<View>instanceOf(android.widget.TextView.class), withText("Confirm Logout"), isDisplayed()));
        textView.check(matches(isDisplayed()));
    }

    @Test
    public void cancelSignOutTest() throws InterruptedException {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.uscidEditText), isDisplayed()));
        appCompatEditText.perform(replaceText(username), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.passwordEditText), isDisplayed()));
        appCompatEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
        materialButton.perform(click());

        Thread.sleep(10000);

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.logout_button), withText("Log Out"), isDisplayed()));
        materialButton2.perform(click());

        Thread.sleep(10000);

        ViewInteraction textView = onView(
                allOf(IsInstanceOf.<View>instanceOf(android.widget.TextView.class), withText("Confirm Logout"), isDisplayed()));
        textView.check(matches(isDisplayed()));

        Thread.sleep(10000);

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button2), withText("Cancel"), isDisplayed()));
        materialButton3.check(matches(isDisplayed()));
        materialButton3.perform(click());

        Thread.sleep(10000);

        ViewInteraction button = onView(
                allOf(withId(R.id.manage_reservation_button), withText("Manage Reservations"), isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void successfullySignedOutTest() throws InterruptedException {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.uscidEditText), isDisplayed()));
        appCompatEditText.perform(replaceText(username), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.passwordEditText), isDisplayed()));
        appCompatEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
        materialButton.perform(click());

        Thread.sleep(10000);

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.logout_button), withText("Log Out"), isDisplayed()));
        materialButton2.perform(click());

        Thread.sleep(10000);

        ViewInteraction textView = onView(
                allOf(IsInstanceOf.<View>instanceOf(android.widget.TextView.class), withText("Confirm Logout"), isDisplayed()));
        textView.check(matches(isDisplayed()));

        Thread.sleep(10000);

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText("LOGOUT"), isDisplayed()));
        materialButton3.check(matches(isDisplayed()));
        materialButton3.perform(click());

        Thread.sleep(10000);

        ViewInteraction button = onView(
                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void signInAfterSigningOutTest() throws InterruptedException {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.uscidEditText), isDisplayed()));
        appCompatEditText.perform(replaceText(username), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.passwordEditText), isDisplayed()));
        appCompatEditText2.perform(replaceText(password), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
        materialButton.perform(click());

        Thread.sleep(10000);

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.logout_button), withText("Log Out"), isDisplayed()));
        materialButton2.perform(click());

        Thread.sleep(10000);

        ViewInteraction textView = onView(
                allOf(IsInstanceOf.<View>instanceOf(android.widget.TextView.class), withText("Confirm Logout"), isDisplayed()));
        textView.check(matches(isDisplayed()));

        Thread.sleep(10000);

        ViewInteraction materialButton3 = onView(
                allOf(withId(android.R.id.button1), withText("LOGOUT"), isDisplayed()));
        materialButton3.check(matches(isDisplayed()));
        materialButton3.perform(click());

        Thread.sleep(10000);

        ViewInteraction button = onView(
                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.uscidEditText), isDisplayed()));
        appCompatEditText3.perform(replaceText(username1), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.passwordEditText), isDisplayed()));
        appCompatEditText4.perform(replaceText(password1), closeSoftKeyboard());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
        materialButton4.perform(click());

        Thread.sleep(10000);

        ViewInteraction displayName1 = onView(
                allOf(withId(R.id.name_text), withText("Blep Cat"), isDisplayed()));
        displayName1.check(matches(isDisplayed()));
    }
}
