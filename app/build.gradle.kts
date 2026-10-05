import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val adsProperties=Properties().apply {
    val config=rootProject.file("ads.properties")
    if(config.exists())config.inputStream().use { load(it) }
}
val admobAppId=adsProperties.getProperty("ADMOB_APP_ID","ca-app-pub-3940256099942544~3347511713")
val admobInterstitialId=adsProperties.getProperty("ADMOB_INTERSTITIAL_ID","ca-app-pub-3940256099942544/1033173712")
require(admobAppId.matches(Regex("ca-app-pub-\\d{16}~\\d{10}"))) { "Invalid AdMob app ID" }
require(admobInterstitialId.matches(Regex("ca-app-pub-\\d{16}/\\d{10}"))) { "Invalid interstitial ad unit ID" }
val liveAds=adsProperties.getProperty("ADS_LIVE","false").toBoolean()
val rewardedId=adsProperties.getProperty("ADMOB_REWARDED_ID","")
val bannerId=adsProperties.getProperty("ADMOB_BANNER_ID","")
listOf(rewardedId,bannerId).filter { it.isNotBlank() }.forEach { require(it.matches(Regex("ca-app-pub-\\d{16}/\\d{10}"))) { "Invalid additional ad unit ID" } }
require(!liveAds || (!admobAppId.contains("3940256099942544") && !admobInterstitialId.contains("3940256099942544"))) { "Live ads require production AdMob IDs" }

android {
    namespace = "com.algokids"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.algokids"
        minSdk = 24
        targetSdk = 36
        versionCode = 7
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resValue("string","admob_app_id",admobAppId)
    }

    buildTypes {
        debug {
            buildConfigField("boolean","REWARDED_ENABLED","true")
            buildConfigField("boolean","BANNER_ENABLED","true")
            buildConfigField("String","ADMOB_REWARDED_ID","\"ca-app-pub-3940256099942544/5224354917\"")
            buildConfigField("String","ADMOB_BANNER_ID","\"ca-app-pub-3940256099942544/6300978111\"")
            buildConfigField("boolean","ADS_ENABLED","true")
            buildConfigField("String","ADMOB_INTERSTITIAL_ID","\"ca-app-pub-3940256099942544/1033173712\"")
            resValue("string","admob_app_id","ca-app-pub-3940256099942544~3347511713")
        }
        release {
            buildConfigField("boolean","REWARDED_ENABLED",(liveAds && rewardedId.isNotBlank() && !rewardedId.contains("3940256099942544")).toString())
            buildConfigField("boolean","BANNER_ENABLED",(liveAds && bannerId.isNotBlank() && !bannerId.contains("3940256099942544")).toString())
            buildConfigField("String","ADMOB_REWARDED_ID","\"$rewardedId\"")
            buildConfigField("String","ADMOB_BANNER_ID","\"$bannerId\"")
            buildConfigField("boolean","ADS_ENABLED",liveAds.toString())
            buildConfigField("String","ADMOB_INTERSTITIAL_ID","\"$admobInterstitialId\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
    implementation("androidx.fragment:fragment:1.8.9")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.gson)
}
