Android Samples App for built template library.
============================================================

* **This** is an Android Samples App using my-template libraries with glide, rxjava, dagger2, retrolambda and retrofit based on MVVM or MVP.
*  **Templates** is a base libary and sample to help developing of android apps rapidly, that contains MVP and MVC patterns' base classes, many useful widgets and custom views, and also utils including retrofit, rxjava, guava and etc.

## Samples
* "Bluetooth": It contains to control power, lamp(led) and fan etc on Arduino board.
* "Networking": It search Github and shows detail information by using Github API.
* "Camera": It shows methods using camera and gallary classes.



## Build Method

* Download libary.
* Import module in android studio.
* Add "multiDexEnabled true" in gradle file of app directory.
* Insert following section in build.gradle in app level.

````
....
buildscript {
    
    repositories {
        ....
        maven {
            url 'https://maven.fabric.io/public'
        }
    }
    dependencies {
        ....
        classpath 'io.fabric.tools:gradle:1.24.4'
    }
}
....
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
annotationProcessor "com.jakewharton:butterknife-compiler:8.5.1"
annotationProcessor "com.google.dagger:dagger-compiler:2.0.2"
````


Feature
-------

- [x] Android mini API 21.
- [x] Bluetooth Serial Read Write.
- [x] Arduino Uno R3.
- [x] HC-06 bluetooth module.
- [x] Kotlin.
- [x] Blockcanary, Crystalic crash log Helper.


Test
----

Remember to connect devices as follow. Have fun!

<p align='center'>
    <img src="https://github.com/happymario/BlueAutoHome/blob/master/screens/arduinoled3.png"/>
</p>

Branch
------

[OneWireArduino](https://github.com/happymario/OneWireArduino) for Android APP with Wired Sensors.

Acknowledgements
----------------

- [BluetoothSPP](https://github.com/akexorcist/Android-BluetoothSPPLibrary)


Sponsor
-------

- [VictoriaMobile] -- A fantastic mobile and web development company. 





