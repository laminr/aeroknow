# ![Aero Know Google Play Icon](https://raw.githubusercontent.com/laminr/aeroknow/master/app/src/main/res/mipmap-xhdpi/ic_launcher.png) AeroKnow

AeroKnow is small Android App to practice Airline Pilot theoretical questions.
The project has been started as Proof-Of-Concept to test development with Jetbrain Koltin language for Android.

It includes:
  - Kotlin 1.1
  - [Dagger 2][dagger] for Depedencies injection
  - [RxJava / RxAndroid 2][rxLink] for asynch task
  - [Square Retrofit][retrofitLink] as HTTP Client
  - [Square Moshi][moshiLink] for JSON parsing
  - [Square Picasso][picassoLink] for Image display
  - [Google Room][roomLink] to handle offline
  - [GuillotineMenu-Android][GuillotineLink] by Yalantis for a "guillotine" style screen appearance


Been Used:
  - [Realm][realmLink] to store user progression
  - [Realm Extension][realmExtLink] extension by Víctor Manuel Pineda Murcia to help with Realm

# Current
  - Source list
  - Subject and Topic for a source
  - Questions campaign screen for a topic
  - About page 
  - Parameters screen

# To compile
You need to create your keystore.properties at the root of the project. A file keystore.properties.temp is present to get the requested format

A fabric.properties file must be as well created at the root of APP folder. A file fabric.properties.temp is present to get the requested format

Continuous work in progress ;-)

# Known bug
... (none reported yet, yahooo)

License
----

Copyright 2017 Thibault de Lambilly
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [rxLink]: <https://github.com/ReactiveX/RxAndroid>
   [dagger]: <https://google.github.io/dagger/>
   [retrofitLink]: <http://square.github.io/retrofit/>
   [moshiLink]: <https://github.com/square/moshi>
   [picassoLink]: <https://github.com/square/picasso>
   [realmLink]: <https://realm.io/>
   [realmExtLink]: <https://github.com/vicpinm/Kotlin-Realm-Extensions>
   [GuillotineLink]: <https://github.com/Yalantis/GuillotineMenu-Android>
   [roomLink]: <https://developer.android.com/topic/libraries/architecture/room.html>

