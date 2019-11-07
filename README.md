
# 1 GamePlugin


# 2 Analytics

## 2.1 Flurry


strings.xml

```groovy
<string name="flurry_key">flurry_key</string>
```

## 2.2 kochava

Project level build.gradle

```
maven {
    url "http://kochava.bintray.com/maven"
}
```

Module  level build.gradle

```
dependencies {
    compile 'com.kochava.base:tracker:3.4.0'
    compile 'com.android.installreferrer:installreferrer:1.0'
}
```

strings.xml

```
<string name="kochava_key">kochava_key</string>
```

## 2.3 gameanalytics

Project level build.gradle

```
maven {
    url 'http://maven.gameanalytics.com/release'
}
```

Module  level build.gradle

```
dependencies {
    compile 'com.gameanalytics.sdk:gameanalytics-android:3.2.2'
}
```

strings.xml

```
<string name="gameanalytics_game_key">game_key</string>
<string name="gameanalytics_secret_key">secret_key</string>
```



# 3 Advertise

## 3.1 Admob

project level build.gradle

```groovy
allprojects {
    repositories {
        maven {
            url "https://maven.google.com"
        }
    }
}
```
strings.xml

```xml
    <string name="admob_app_id">ca-app-pub-9274282740568260~7977987594</string>
    <string name="admob_banner_id">banner_ad_unit_id</string>
    <string name="admob_interstitial_id">ca-app-pub-9274282740568260/1808342671</string>
    <string name="admob_video_id">ca-app-pub-9274282740568260/7235751563</string>
    <string name="admob_test_device_id">4258574fe1cc47ea897030ce840c886b</string>
```

### 3.1.1 Vungle-Adapter

strings.xml

```xml
    <string name="vungle_video_id">vungle_video_id</string>
    <string name="vungle_spot_id">vungle_spot_id</string>
```

### 3.1.2 Applovin-Adapter

none

# 4 Facebook

strings.xml

```xml
    <string name="facebook_app_id">facebook_app_id</string>
    <string name="fb_login_protocol_scheme">fb_login_protocol_scheme</string>
```



# 5 iap_googleplay

Strings.xml

```xml
<string name="google_iab_publickey">XXXXXXXXXXXXX</string>
```

# 6 Gameplugin_Base

## 6.1 SystemUtil

Add AndroidManifest.xml main activty 

```
<intent-filter>
    <action android:name="sys.notify"/>
    <category android:name="android.intent.category.DEFAULT" />
</intent-filter>
```


