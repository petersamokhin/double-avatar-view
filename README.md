# Double Avatar View
Instagram-like (from broadcasts) double avatar view with cropping

<img src="https://petersamokhin.com/files/projects/doubleavatar/demo.gif" width="402" height="705" />

Demo APK: https://petersamokhin.com/files/projects/doubleavatar/dav.apk

# Install

1. Add `jitpack` repo to your project-level `build.gradle`:
```groovy
allprojects {
    repositories {
        // other repos
	    maven { url 'https://jitpack.io' }
    }
}
```

2. Add library to your dependencies:
```groovy
dependencies {
    implementation 'com.github.petersamokhin:double-avatar-view:$ACTUAL_VERSION'
}
```
See last version: https://github.com/petersamokhin/double-avatar-view/releases

# Usage

Configure view in your xml layout file:

```xml
<com.petersamokhin.android.doubleavatarview.DoubleAvatarView
        android:id="@+id/dav"
        android:layout_width="200dp"
        android:layout_height="200dp"
        app:cut_size_coeff="1.115"
        app:second_size_coeff="2"
        app:horizontal_offset="0.15"
        app:vertical_offset="0.15" />
```

Or configure view from Java/Kotlin:

```kotlin
val config = DoubleAvatarView.Config(
            "https://back.avatar/",
            "https://front.avatar/",
            0.15f,                     // horizontal offset in percents of back image's width
            0.15f,                     // vertical offset in percents of back image's height
            2f,                        // front image radius multiplier
            1.115f                     // front image's cropped background radius multiplier
)
dav.updateConfig(config)
```
