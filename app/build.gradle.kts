plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.umc.anddeul"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.umc.anddeul"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // bottom navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0")
    implementation ("com.google.android.material:material:1.11.0")

    implementation("com.kizitonwose.calendar:view:2.4.1")

    // 카카오 로그인
    implementation ("com.kakao.sdk:v2-user:2.19.0")

    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3") // 베어러 토큰 사용

    implementation ("com.squareup.retrofit2:converter-scalars:2.1.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.1.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.6.0")

    // swipe refresh layout 라이브러리
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")

    // circleImageView
    implementation ("de.hdodenhof:circleimageview:2.2.0")
    
    // ViewPager2
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")


    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.compose.foundation:foundation-android:1.6.0")

    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")

    //svg
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.12.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}