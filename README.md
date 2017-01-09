# ROKO.Stickers.Android

Here at ROKO, we’ve discovered, it’s simple to add some extra delight in the user experience by integrating stickers with the camera component of your app. Interested in adding stickers to your app? We have a component for you! With ROKO Stickers, you can easily upload and deploy your own custom, branded stickers that users can add to photos in your app. Without any intervention from developers, you can set up your own custom sticker packs and release them to your users. Secure branding on content created in your app by adding a simple watermark with a sticker. You can even have your loyal users add stickers to their photos to create authentic engagement with your audience and original user generated content for future marketing endeavors.

## Portal settings
ROKO Mobi provides app developers and product owners with a suite of tools to accelerate mobile development, engage and track customers, and measure their app's success

See here for ROKO Mobi documentation, interaction guides, and instructions:
https://docs.roko.mobi/docs/setting-up-your-account

## Android Project Settings
Import project in Android studio:

1. Import project in Android Studio
2. Add a new key to your AndroidManifest.xml file
name: ROKOMobiAPIToken
value: API key of your ROKO Mobi portal application (see below for ROKO Mobi portal information)
(for example "<meta-data android:name="ROKOMobiAPIToken" android:value="Your ROKOMobi API Token key" />")
3. Update release signing config in build.gradle for release application build
