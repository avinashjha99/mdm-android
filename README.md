MDM-android
===========

MDM client code for android

* Configuring and running the project

This is a standard Android project with no special third party dependencies. However you must refer the Google Play Services to build this project, which is available in android sdk itself, just add it as android reference project. Once you import the project you must add configuration to Config.java file for you GCM server project number, your central server ip. After that you can build the apk as normally done in eclipse (Eclipse uses ant right now for building android projects). Once apk is available install it on any device, accept any Device permissions and now your device will become managed.

* Understanding the project

There is one activity for registering the device, rest are Helper and utility classes. You would find utility for Uploading App analytics data, call analytics data. Also control module for locking phone, chaging password, unintalling could be found in Broadcast receiver. We don't force the user to upload the data, because testing was dont on personal devices, but we can force this behaviour.

