# Paryavaran-Kavalu 🌱

### Community Waste Reporting & Cleanliness Monitoring App

Paryavaran-Kavalu is an Android application developed to help citizens report illegal garbage dumping locations (**Waste Blackspots**) using geo-tagging, image uploads, and real-time map visualization.

The application enables volunteers to identify waste locations, navigate to them, and update cleanup status efficiently.

---

# 🚀 Features

## 🔐 Authentication

* Firebase Email/Password Authentication
* Google Sign-In Authentication
* Secure Login & Registration

### Role-Based Access

* Citizen
* Volunteer

---

## 📍 Waste Reporting

* Report waste locations instantly
* Upload images using:

  * Camera
  * Gallery
* Automatic GPS location capture
* Waste type categorization
* Description support

---

## 🗺️ Google Maps Integration

* Real-time cleanliness map
* Red markers → Pending reports
* Green markers → Cleaned reports
* Google Maps navigation support for volunteers

---

## 👥 Volunteer Workflow

Volunteers can:

* View all waste reports
* Navigate to waste locations
* Mark reports as cleaned

---

## 🌟 Eco-Karma System

* +100 Eco-Karma points per report

### Dynamic Medal System

* Eco Beginner
* Eco Warrior
* Green Guardian

---

## 👤 Profile System

* View user details
* Edit profile
* View personal reports
* Logout functionality with confirmation dialog

---

## 🔔 Notifications

Notification when:

* New report is submitted
* Report is marked as cleaned

---

# 🏗️ Tech Stack

## Frontend

* Kotlin
* Jetpack Compose
* Material 3

## Architecture

* MVVM Architecture
* StateFlow / Coroutines

## Backend & Cloud

* Firebase Authentication
* Firebase Firestore
* Firebase Storage

## Maps & Location

* Google Maps SDK
* Google Maps Compose
* FusedLocationProviderClient

## Database
- Firebase Firestore (Cloud Database)
- Room Database (Initial prototype/local simulation phase)

---

# 📱 Screens

* Splash Screen
* Login Screen
* Register Screen
* Home Dashboard
* New Report Screen
* Map Screen
* Report Detail Screen
* Profile Screen
* Edit Profile Screen
* My Reports Screen

---

# ⚙️ Installation & Setup

## 1. Clone Repository

```bash
git clone <your-repository-url>
```

## 2. Open in Android Studio

Open the project in:

* Android Studio Flamingo or later

---

## 3. Configure Firebase

Add:

```plaintext
google-services.json
```

inside:

```plaintext
app/
```

---

## 4. Enable Firebase Services

Enable:

* Firebase Authentication
* Firestore Database
* Firebase Storage

---

## 5. Configure Google Maps API

Add your Google Maps API key in:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY"/>
```

inside:

```plaintext
AndroidManifest.xml
```

---

## 6. Run the App

Connect:

* Emulator or Physical Device

Then run the app ▶

---

# 📂 Project Structure

```plaintext
com.example.paryavaran_kavalu/

├── data/
├── ui/
├── viewmodel/
├── navigation/
├── repository/
├── utils/
└── firebase/
```

---

# 🎯 Project Objectives

* Promote cleaner surroundings
* Support Swachh Bharat mission
* Enable real-time waste reporting
* Improve volunteer coordination
* Encourage environmental responsibility through gamification

---

# 🔮 Future Enhancements

* AI-based waste classification
* Push notifications using FCM
* Admin dashboard
* Route optimization for volunteers
* Multi-city support

---

# 👨‍💻 Developer Details

**Sohail Mateen**
**sohailmateen001@gmail.com**
