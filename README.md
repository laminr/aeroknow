# AeroKnow

AeroKnow is small Android App to practice Airline Pilot theorical questions.
The project has been started as Proof-Of-Concept to test development with Jetbrain Koltin language for Android.
It includes:
  - Kotlin 1.1
  - [Dagger 2][dagger] for Depedencies injection
  - [RxJava / RxAndroid 2][rxLink] for asynch task
  - [Square Retrofit][retrofitLink] as HTTP Client
  - [Square Moshi][moshiLink] for JSON parsing
  - [Square Picasso][picassoLink] for Image display
  - [Realm][realmLink] to store user progression
  - [Realm Extension][realmExtLink] extension by Víctor Manuel Pineda Murcia to help with Realm

# Current
  - 1st screen : Source list
  - 2nd screen : Subject and Topic for a source
  - 3rd screen : Questions campain screen for a topic

Realm is used to store important or disinterest in question. Question are then sorted by important question, then usual one and eventually the none important ones

# To compile
You need to create your keystore.properties at the root of the project. A file keystore.properties.temp is present to get the requested format

A fabric.properties file must be as well created at the root of APP folder. A file fabric.properties.temp is present to get the requested format

Continious work in progress ;-)

# Known bug
After having shuffled the questions, the answers are not synchronize anymore (wtf)

License
----

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [rxLink]: <https://github.com/ReactiveX/RxAndroid>
   [dagger]: <https://google.github.io/dagger/>
   [retrofitLink]: <http://square.github.io/retrofit/>
   [moshiLink]: <https://github.com/square/moshi>
   [picassoLink]: <https://github.com/square/picasso>
   [realmLink]: <https://realm.io/>
   [realmExtLink]: <https://github.com/vicpinm/Kotlin-Realm-Extensions>

