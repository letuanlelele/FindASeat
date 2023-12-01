# FindASeat
Now that everyone is back on campus, people are having a hard time finding a table to study or work at.
FindASeat is an app that helps them find available seats on campus and reserve them in advance

## Emulator Setup

To run the project:
- Open the project in Android Studio
- Click on "Tools" and navigate to "Firebase" in Android Studio
- Click "Authentication," then choose "Authenticate using a custom authentication system," and click "Connect your app to Firebase"
- Click "Cloud Storage for Database," then select "Get Started with Cloud Storage," and click "Connect your app to Firebase"
- Ensure that you are set to start using FindASeat

Requirements:
Device: Use the Pixel 2 API 24 to run this app

To provide you with the best information, here are the details of our Android emulator setup:

- **Emulator Version:** Pixel 2 API 24
- **Google API Key:**

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="AIzaSyBZwfBY8CkpD05pli49Jwu9GBuCkWz_MTE"></meta-data>
```

## Login Information

For your convenience, we have provided login credentials to access our reservation information:

Username: [loaf, thinker, blep, sandwich, polite]

Password: [loafcat, thinker, blepcat, sandwichcat, politecat]

## Sprint Summary (2.5)
Additional features implemented:
- If no seats are available at specific time, display a toast saying there are no seats instead of just showing a blank page
- Email verification when creating account

Bug fixes:
- BUG: app crashing when trying to book a reservation while logged out
- BUG: log in → book reservation → log out → book reservation → ERROR: “you already have a reservation” message shown
- BUG: log in → cancel reservation → log out → book reservation → ERROR: confirmation page shown AND app crashes
- BUG: Create account: selecting cat image always results in thinker cat
- BUG: After booking a reservation and click on “Return to map”, app sometimes crashes
- BUG: When creating account, user can enter an invalid USC ID
- BUG: If you don’t have a current reservation and you try to click on “Cancel Reservation”, the app crashes
- BUG: Do not allow a user to book a reservation in the past.
- BUG: click “Modify Reservation” → click “Cancel” → ERROR: “Current Reservation” field displays two duplicate reservations



Please don't hesitate to reach out if you have any questions.

Minh, Tuan, and Nikki
