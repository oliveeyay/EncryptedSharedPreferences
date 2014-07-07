EncyptedSharedPreferences
======================

Android library using Android Studio 0.8.0

Provides encryption to SharedPreferences integrated system.
It uses AES 128bits encryption to secure datas, and is compatible with UTF-8 compliants elements.

The important part to deploy this library to my maven repository is:

- General build.gradle:

```
artifacts {
    archives file('build/outputs/aar/app.aar')
}

apply plugin: 'maven'
uploadArchives {
    repositories {
        mavenDeployer {
            // You luckily want that this path leads to my maven hosted here https://github.com/vincentbrison/vb-maven/
            repository(url: uri("../../og-maven/release"))
            pom.groupId = 'com.og.encryptedsharedpreferences'
            pom.artifactId = 'encryptedsharedpreferences'
            pom.version = '1.0.0'
        }
    }
}
 ```
 
======================

Quality for Android
-------
You will find under the directory /config the base configuration to run quality test on the project.
The followings tools are used:
 - Checkstyle.
 - Findbugs.
 - PMD.
 - Lint.
 
By default, all the reports will be generated in the folder app/build/reports.
 
======================

License

    Copyright 2013 Olivier Goutay.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.