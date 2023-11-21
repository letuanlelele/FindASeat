package com.example.findaseat;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import org.junit.Test;


import android.app.Activity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.activity.result.ActivityResult;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private String username = "loaf";
    private String password = "loafcat";
    @Test
    public void testLoginSuccess() throws InterruptedException {
//        FragmentScenario.launchInContainer(LoginFragment.class, null, R.style.AppThemeLight, new FragmentFactory());
//
//        // Type in the username and password
//        onView(withId(R.id.uscidEditText)).perform(typeText(username), closeSoftKeyboard());
//        onView(withId(R.id.passwordEditText)).perform(typeText(password), closeSoftKeyboard());
//
//        // Click on the login button
////        onView(withId(R.id.loginButton)).perform(click());
//        ViewInteraction materialButton = onView(
//                allOf(withId(R.id.loginButton), withText("Login"), isDisplayed()));
//        materialButton.perform(click());
//
//        // Check for successful login
//        Thread.sleep(10000);

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

        ViewInteraction button = onView(
                allOf(withId(R.id.manage_reservation_button), withText("Manage Reservations"), isDisplayed()));
        button.check(matches(isDisplayed()));
    }
    @Test
    public void testWrongPassword() throws InterruptedException {
        FragmentScenario.launchInContainer(LoginFragment.class, null, R.style.AppThemeLight, new FragmentFactory());

        String test = "wrong password";
        // Type in the username and password
        onView(withId(R.id.uscidEditText)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(test), closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.loginButton)).perform(click());

        // Check for failed login
        Thread.sleep(10000);
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
    }
    @Test
    public void testWrongUsername() throws InterruptedException {
        FragmentScenario.launchInContainer(LoginFragment.class, null, R.style.AppThemeLight, new FragmentFactory());

        String test = "wrong username";
        // Type in the username and password
        onView(withId(R.id.uscidEditText)).perform(typeText(test), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(password), closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.loginButton)).perform(click());

        // Check for failed login
        Thread.sleep(10000);
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
    }
    @Test
    public void testEmptyUsernameAndPassword() throws InterruptedException {
        FragmentScenario.launchInContainer(LoginFragment.class, null, R.style.AppThemeLight, new FragmentFactory());

        // Type in the username and password
        onView(withId(R.id.uscidEditText)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(""), closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.loginButton)).perform(click());

        // Check for failed login
        Thread.sleep(10000);
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
    }
}