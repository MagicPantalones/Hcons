# Hcons
**A simple chat / content board with integrated Firebase services.**

## How to copy repo
- Clone repo
- Go to [Firebase](https://firebase.google.com/), register a new account and add a new project.
- Go to Database, click "Get Started" under Realtime Database.
  - Follow the guide, download and put the "google-services.json" file in the "HkonsApp\app\\" folder.
  - Copy and paste the following code under the Rules tab.
    ```json
    {
      "rules": {
        ".read": "auth != null",
        ".write": "auth != null"
      }
    }
    ```
- Go to Firebase Authentication and set up _**only**_ Email as the sign-in method.
  - You need to create user accounts manually for now. I have not implemented sign-up yet.
    I want to avoid unauthorized users until I write more secure rules, and test the database
    data scaling.



