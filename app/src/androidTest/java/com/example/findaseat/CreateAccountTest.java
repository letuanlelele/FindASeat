package com.example.findaseat;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import static java.lang.Thread.sleep;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CreateAccountTest {
    public static String generateRandomString(int length) {
        // Define the characters you want to include in the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Create a StringBuilder to build the random string
        StringBuilder randomString = new StringBuilder();

        // Create a Random object
        Random random = new Random();

        // Generate the random string
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }


    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void navigateToCreateAccountTest() throws InterruptedException {
        sleep(7000);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.createAccountButton), withText("Create New Account"), isDisplayed()));
        materialButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.createAccountButton), withText("Create Account"), isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void emptyFieldsTest() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.createAccountButton), withText("Create New Account"), isDisplayed()));
        materialButton.perform(click());


        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.createAccountButton), withText("Create Account"), isDisplayed()));
        materialButton2.perform(click());

        onView(withId(R.id.errorCreateAccountMessage)).check(matches(isDisplayed()));

    }

    @Test
    public void failPasswordMatchTest() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.createAccountButton), withText("Create New Account"), isDisplayed()));
        materialButton.perform(click());

        onView(withId(R.id.newName)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newUsername)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newUSCID)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newPasswordEditText)).perform(typeText("randomPassword"), closeSoftKeyboard());
        onView(withId(R.id.confrimPasswordEditText)).perform(typeText("notRandomPassword"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.createAccountButton), withText("Create Account"), isDisplayed()));
        materialButton2.perform(click());

        onView(withId(R.id.errorCreateAccountMessage)).check(matches(isDisplayed()));
    }

    @Test
    public void usernameAlreadyExistsTest() {

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.createAccountButton), withText("Create New Account"), isDisplayed()));
        materialButton.perform(click());

        onView(withId(R.id.newName)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newUsername)).perform(typeText("loaf"), closeSoftKeyboard());
        onView(withId(R.id.newUSCID)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newPasswordEditText)).perform(typeText("randomPassword"), closeSoftKeyboard());
        onView(withId(R.id.confrimPasswordEditText)).perform(typeText("randomPassword"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.createAccountButton), withText("Create Account"), isDisplayed()));
        materialButton2.perform(click());

        // wait for error message to be displayed
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.errorCreateAccountMessage)).check(matches(isDisplayed()));
    }

    @Test
    public void successfullyCreatedAccountTest() {
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_profile), withContentDescription("My Profile"), isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.createAccountButton), withText("Create New Account"), isDisplayed()));
        materialButton.perform(click());

        onView(withId(R.id.newName)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newUsername)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newUSCID)).perform(typeText(generateRandomString(10)), closeSoftKeyboard());
        onView(withId(R.id.newPasswordEditText)).perform(typeText("randomPassword"), closeSoftKeyboard());
        onView(withId(R.id.confrimPasswordEditText)).perform(typeText("randomPassword"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.createAccountButton), withText("Create Account"), isDisplayed()));
        materialButton2.perform(click());

        // wait for error message to be displayed
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.successCreateAccountMessage)).check(matches(isDisplayed()));
    }
}
