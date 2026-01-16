<div align="left">
  <a href="README.pl.md">
    <img src="https://img.shields.io/badge/Lang-Polski-red.svg?style=for-the-badge&logo=poland&logoColor=white" alt="Polska wersja" />
  </a>
</div>

<br />
<div align="center">

  <table align="center" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td style="border: none; padding-right: 15px;">
        <img src="images/logo_GymEaze.png" alt="GymEaze Logo" width="120" height="120" style="vertical-align: middle;"/>
      </td>
      <td style="border: none; vertical-align: middle;">
        <h3 style="margin: 0; font-weight: bold;">Comprehensive social platform<br>supporting your workouts.</h3>
      </td>
    </tr>
  </table>

  <p>
    We connect fitness enthusiasts, enabling plan creation, progress tracking, and friendly competition.
  </p>

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg?style=for-the-badge)
![Thesis](https://img.shields.io/badge/type-Engineering_Thesis-orange.svg?style=for-the-badge)
![Year](https://img.shields.io/badge/year-2025-lightgrey.svg?style=for-the-badge)

  <p align="center">
    <a href="#-about-the-project">About</a> •
    <a href="#️-technologies">Technologies</a> •
    <a href="#-features">Features</a> •
    <a href="#-gallery">Gallery</a> •
    <a href="#-getting-started">Getting Started</a> •
    <a href="#-testing">Testing</a> •
    <a href="#-authors">Authors</a>
  </p>
</div>

---

## 📖 About the Project

**GymEaze** is a modern fitness system that combines an advanced workout journal with a social media platform. The application's goal is not only to track results but also to build a community that motivates each other through content sharing, commenting, reacting to content posted by other users, a badge system, personalized training plans, and much more.

### System Architecture

The project was implemented in a three-tier architecture, ensuring scalability and separation of concerns:

1.  **Server Layer (Backend):** Efficient and scalable API implementing core business logic. Ensures high security standards through integration with Google OAuth. Data is stored in a cloud-based MongoDB Atlas database, and multimedia management (photos, videos) is handled through Cloudinary service.
2.  **Web Layer (Frontend):** Modern SPA application built on React and TypeScript. Utilizes component architecture with TailwindCSS and shadcn/ui libraries. The project emphasizes digital accessibility (WCAG) with proper color contrast and full keyboard interface support. React Query handles API communication and server state management, ensuring smooth operation and network query optimization.
3.  **Mobile Layer (Mobile):** Native Android application created in Kotlin using the modern declarative UI framework Jetpack Compose. The project is based on the MVVM architectural pattern, ensuring clean code and easy testing.

---

## 🛠️ Technologies

The project uses a modern technology stack ensuring performance and scalability.

### Backend

![NodeJS](https://img.shields.io/badge/node.js-6DA55F?style=for-the-badge&logo=node.js&logoColor=white)
![Express.js](https://img.shields.io/badge/express.js-%23404d59.svg?style=for-the-badge&logo=express&logoColor=%2361DAFB)
![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![Mongoose](https://img.shields.io/badge/Mongoose-880000?style=for-the-badge&logo=mongoose&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)
![PDFKit](https://img.shields.io/badge/PDFKit-EC1C24?style=for-the-badge&logo=adobeacrobatreader&logoColor=white)
![Cloudinary](https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white)
![Google Cloud](https://img.shields.io/badge/Google_Cloud-4285F4?style=for-the-badge&logo=google-cloud&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

### Frontend

![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)
![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/vite-%23646CFF.svg?style=for-the-badge&logo=vite&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/tailwindcss-%2338B2AC.svg?style=for-the-badge&logo=tailwind-css&logoColor=white)
![React Query](https://img.shields.io/badge/-React%20Query-FF4154?style=for-the-badge&logo=react%20query&logoColor=white)
![Shadcn UI](https://img.shields.io/badge/shadcn%2Fui-000000?style=for-the-badge&logo=shadcnui&logoColor=white)
![React Router](https://img.shields.io/badge/React_Router-CA4245?style=for-the-badge&logo=react-router&logoColor=white)
![React Hook Form](https://img.shields.io/badge/React_Hook_Form-EC5990?style=for-the-badge&logo=reacthookform&logoColor=white)
![Google Sign-In](https://img.shields.io/badge/Google_Sign--In-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Zod](https://img.shields.io/badge/Zod-3E67B1?style=for-the-badge&logo=zod&logoColor=white)
![Axios](https://img.shields.io/badge/axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white)

### Mobile

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84.svg?style=for-the-badge&logo=android-studio&logoColor=white)
![Retrofit](https://img.shields.io/badge/Retrofit-2C8EBB?style=for-the-badge&logo=square&logoColor=white)
![OkHttp](https://img.shields.io/badge/OkHttp-000000?style=for-the-badge&logo=square&logoColor=white)
![Coroutines](https://img.shields.io/badge/Kotlin_Coroutines-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Material Design 3](https://img.shields.io/badge/Material_Design_3-757575?style=for-the-badge&logo=materialdesign&logoColor=white)
![Google Sign-In](https://img.shields.io/badge/Google_Sign--In-4285F4?style=for-the-badge&logo=google&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)

---

## ✨ Features

### Access and Personalization

- **Role System**:
  - **Guest**: Ability to browse exercise database, plans, and posts (read-only mode).
  - **Logged-in User**: Full access to interactions, creators, and progress tracking.
- **Cross-Platform**: Ability to be logged in simultaneously on web and mobile applications.
- **User Profile**: Personalization of data (weight, height, goals, BMI), privacy management, avatar, badge pinning, and additional profile information.
- **Themes**: Full support for dark and light themes on both platforms.

### Advanced Planner and Training

- **Comprehensive Plan Creator**: Create plans up to 30 days, divided into weeks, with options to duplicate days/weeks for faster editing.
- **Custom Exercises**: Ability to add custom exercises to global or private database.
- **Content Visibility**: Users decide whether their plan/exercise is public or private.
- **Favorites**: System for saving interesting plans and exercises for later.

### Mobile Assistant (Mobile Only)

- **Step-by-Step Mode**: Interactive workout execution mode. The app guides through exercises, counts rest periods and sets.
- **Progress Tracking**: Marking training days, resetting progress, or removing started plans.
- **Synchronization**: Progress in the mobile app is visible on the web profile.

### Community

- **Interactions**: Like system and two-level comment system (main comment + reply) under posts, plans, and exercises.
- **Following**: Ability to follow other users (Follow/Unfollow) and view their activity and profiles.
- **Gamification**: Earning badges visible in profile (e.g., for account longevity or created plans).

### Admin Panel (Web Only)

- **Moderation**: Removing inappropriate content (posts, exercises, plans, comments).
- **User Management**: Blocking accounts, sending global system notifications.

---

## 📸 Gallery

> Below are the most important views of the application.

### 🖥️ Web Application

<div align="center">

|                           Plan Creator (30 days)                           |                       Profile and Statistics                       |                           Admin Panel                           |
| :------------------------------------------------------------------------: | :----------------------------------------------------------------: | :-------------------------------------------------------------: |
|                   _Plan editing view divided into weeks_                   |              _Profile view with badges and BMI chart_              |                  _Managing reports and users_                   |
| <img src="images/web-planner.png" width="100%" alt="Training plan editor"> | <img src="images/web-profile.png" width="100%" alt="User profile"> | <img src="images/web-admin.png" width="100%" alt="Admin panel"> |

</div>

### 📱 Mobile Application

<div align="center">

|                            Training Mode (Step-by-step)                             |                          Social Feed & Comments                          |                              Settings and Themes                               |
| :---------------------------------------------------------------------------------: | :----------------------------------------------------------------------: | :----------------------------------------------------------------------------: |
|                              _Active exercise screen_                               |                     _Discussion view under exercise_                     |                       _Light mode and privacy settings_                        |
| <img src="images/mobile-active-workout.png" width="200" alt="Mobile training mode"> | <img src="images/mobile-comments.png" width="200" alt="Mobile comments"> | <img src="images/mobile-settings-light.png" width="200" alt="Mobile settings"> |

</div>

---

## 🚀 Getting Started

To run the project locally, follow the instructions below for each module.

### Prerequisites

- Node.js (v18+)
- Android Studio (for mobile module)
- MongoDB (local or Atlas)

### Step 0: Clone the Project

```bash
git clone https://github.com/username/gym-eaze.git
cd gym-eaze
```

### Step 1: Configure Google OAuth 2.0

For login to work properly, you need to configure a project in [Google Cloud Console](https://console.cloud.google.com/):

1. Go to **API & Services** > **Credentials**.
2. Click **Create Credentials** > **OAuth client ID**.
3. **Create Android Client**:
   - **Package name**: `com.example.gymeaze`
   - **SHA-1**: Your debug key fingerprint (get it in Android Studio > Gradle > Tasks > android > signingReport).
4. **Create Web Application Client**:
   - **Authorized JavaScript origins**:
     - `http://localhost:5173`
     - `http://localhost`
   - **Authorized redirect URIs**:
     - `http://localhost:5173`
     - `https://developers.google.com/oauthplayground`
5. **Important:** Copy the generated `CLIENT_ID` (Web Application type) – it will be needed in backend, frontend, and mobile app configuration.

### Step 2: Backend (API)

```bash
cd backend
npm install
```

1. Rename the file .env.example to .env.

2. Fill in the keys in .env

3. Start the server:

```bash
npm run dev
```

### Step 3: Frontend (Web)

```bash
cd frontend
npm install
```

1. Rename the file .env.example to .env.

2. Fill in the keys in .env

3. Start the server:

```bash
npm run dev
```

Application available at: http://localhost:5173

### Step 4: Mobile (Android)

1. Navigate to the mobile/ directory.

2. Create a local.properties file (if it doesn't exist) in the main mobile/ directory.

3. Paste and fill in the configuration below:

```bash
## This file is not versioned. Contains local paths and keys.

# Path to SDK (usually auto-generated by Android Studio)
# Windows: C:\Users\YourName\AppData\Local\Android\Sdk
# Mac/Linux: /Users/YourName/Library/Android/sdk
sdk.dir=YOUR_PATH_TO_SDK

# Backend address
# (For Android emulator, localhost is usually 10.0.2.2)
API_URL="http://10.0.2.2:5000"

# Client ID from Google Cloud Console (Web Client ID)
GOOGLE_CLIENT_ID="YOUR_GOOGLE_CLIENT_ID"
```

4. Open the project in Android Studio, wait for Gradle synchronization (this may take a moment), and run the application on an emulator.

## 🧪 Testing

Verification of system functionality was conducted through **manual testing**, covering both business logic and user interface.

### ✅ Testing Scope

- **Backend & API**:
  REST API endpoint verification was performed using **Insomnia** tool. We checked:
  - Correctness of HTTP response codes (200, 400, 401, 500).
  - Security functionality (JWT, authorization middleware).
  - Input data validation (e.g., when creating plans).

- **Key Manual Scenarios**:
  - **Cross-Platform Synchronization**: Creating a plan in the web application and immediately starting a workout in the mobile application.
  - **OAuth 2.0**: Google login and user session refresh.
  - **Multimedia Handling**: Uploading photos and videos to Cloudinary and their proper rendering on different devices.
  - **Admin Panel**: Attempting to access the admin panel from a regular user account (access blocked).
  - **Training Cycle**: Complete step-by-step mode on mobile, from start to saving workout history.

> 💡 **And much more...**
> We've taken care of dozens of small UX/UI details that you'll discover while using the application.

## 👥 Authors

Behind the GymEaze project is a trio of code and sports enthusiasts. Each of us brought different strengths to deliver this project to completion.

<div align="center">

|                 <a href="https://github.com/evee03"><img src="https://github.com/evee03.png" width="100px;" alt=""/></a>                  |                <a href="https://github.com/Squashim"><img src="https://github.com/Squashim.png" width="100px;" alt=""/></a>                 |                 <a href="https://github.com/Kuba27x"><img src="https://github.com/Kuba27x.png" width="100px;" alt=""/></a>                 |
| :---------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------------------: |
|                                                           **Ewelina Musińska**                                                            |                                                             **Konrad Piekarz**                                                              |                                                            **Jakub Pieńkowski**                                                            |
| [![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/evee03) | [![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Squashim) | [![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Kuba27x) |

</div>
