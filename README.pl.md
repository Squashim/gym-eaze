<div align="left">
  <a href="README.md">
    <img src="https://img.shields.io/badge/Lang-English-blue.svg?style=for-the-badge&logo=united-kingdom&logoColor=white" alt="English version" />
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
        <h3 style="margin: 0; font-weight: bold;">Kompleksowa platforma społecznościowa<br>wspomagająca treningi.</h3>
      </td>
    </tr>
  </table>

  <p>
    Łączymy entuzjastów fitnessu, umożliwiając tworzenie planów, śledzenie postępów i rywalizację.
  </p>

![Version](https://img.shields.io/badge/wersja-1.0.0-blue.svg?style=for-the-badge)
![Thesis](https://img.shields.io/badge/typ-Praca_Inżynierska-orange.svg?style=for-the-badge)
![Year](https://img.shields.io/badge/rok-2025-lightgrey.svg?style=for-the-badge)

  <p align="center">
    <a href="#-o-projekcie">O projekcie</a> •
    <a href="#-technologie">Technologie</a> •
    <a href="#-funkcjonalności">Funkcjonalności</a> •
    <a href="#-galeria">Galeria</a> •
    <a href="#-uruchomienie">Uruchomienie</a> •
    <a href="#-testy">Testy</a> •
    <a href="#-autorzy">Autorzy</a>
  </p>
</div>

---

## 📖 O projekcie

**GymEaze** to nowoczesny system fitness, który łączy funkcje zaawansowanego dziennika treningowego z platformą społecznościową. Celem aplikacji jest nie tylko śledzenie wyników, ale także budowanie społeczności, która motywuje się nawzajem poprzez dodawanie treści, komentarzy, reagowanie na zawartość umieszczaną przez innych użytkowników na platformę, system odznak, komentarzy, tworzenie spersonalizowaych planó treningowych i wiele innych.

### Architektura Systemu

Projekt został zrealizowany w architekturze trójwarstwowej, zapewniającej skalowalność i separację odpowiedzialności:

1.  **Warstwa Serwerowa (Backend):** Wydajne i skalowalne API realizujące kluczową logikę biznesową systemu. Zapewnia wysoki standard bezpieczeństwa dzięki integracji z Google OAuth. Dane składowane są w chmurowej bazie MongoDB Atlas, a zarządzanie multimediami (zdjęcia, wideo) odbywa się za pośrednictwem serwisu Cloudinary.
2.  **Warstwa Internetowa (Frontend):** Nowoczesna aplikacja typu SPA zbudowana w oparciu o bibliotekę React i TypeScript. Wykorzystuje architekturę komponentową oraz bibliotekę TailwindCSS i shadcn/ui. Projekt kładzie duży nacisk na dostępność cyfrową (WCAG). Zadbano o odpowiedni kontrast kolorystyczny oraz pełną obsługę interfejsu za pomocą klawiatury. Za komunikację z API i zarządzanie stanem serwera odpowiada React Query, co gwarantuje płynność działania i optymalizację zapytań sieciowych.
3.  **Warstwa Mobilna (Mobile):** Natywna aplikacja na system Android, stworzona w języku Kotlin przy użyciu nowoczesnego deklaratywnego frameworka UI Jetpack Compose. Projekt oparty jest o wzorzec architektoniczny MVVM, co zapewnia czystość kodu i łatwość testowania.

---

## 🛠️ Technologie

Projekt wykorzystuje nowoczesny stack technologiczny zapewniający wydajność i skalowalność.

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

## ✨ Funkcjonalności

### Dostęp i Personalizacja

- **System ról**:
  - **Gość**: Możliwość przeglądania bazy ćwiczeń, planów i postów (tryb _read-only_).
  - **Użytkownik zalogowany**: Pełny dostęp do interakcji, kreatorów i śledzenia postępów.
- **Cross-Platform**: Możliwość bycia zalogowanym jednocześnie na aplikacji webowej i mobilnej.
- **Profil Użytkownika**: Personalizacja danych (waga, wzrost, cele, BMI), zarządzanie widocznością danych (prywatność), avatar, przypinanie odznak oraz dodawanie innych informacji o profilu.
- **Motywy**: Pełne wsparcie dla ciemnego i jasnego motywu na obu platformach.

### Zaawansowany Planer i Trening

- **Kompleksowy Kreator Planów**: Tworzenie planów do 30 dni, z podziałem na tygodnie, z opcją duplikowania dni/tygodni dla szybszej edycji.
- **Własne Ćwiczenia**: Możliwość dodawania niestandardowych ćwiczeń do globalnej lub prywatnej bazy.
- **Widoczność Treści**: Użytkownik decyduje, czy jego plan/ćwiczenie jest publiczne czy prywatne.
- **Ulubione**: System zapisywania interesujących planów i ćwiczeń na później.

### Asystent Mobilny (Mobile Only)

- **Tryb "Krok po kroku"**: Interaktywny tryb wykonywania treningu. Aplikacja prowadzi przez ćwiczenia, odlicza czas przerw i serii.
- **Śledzenie Progresu**: Oznaczanie dni treningowych, resetowanie postępów lub usuwanie rozpoczętych planów.
- **Synchronizacja**: Postępy w aplikacji mobilnej są widoczne na profilu webowym.

### Społeczność

- **Interakcje**: System polubień (like) oraz dwupoziomowy system komentarzy (komentarz główny + odpowiedz) pod postami, planami i ćwiczeniami.
- **Obserwacje**: Możliwość śledzenia innych użytkowników (Follow/Unfollow) i przeglądania ich aktywności oraz profili.
- **Grywalizacja**: Zdobywanie odznak widocznych w profilu (np. za długość posiadania konta czy stworzone plany).

### Panel Administracyjny (Web Only)

- **Moderacja**: Usuwanie nieodpowiednich treści (posty, ćwiczenia, plany, komentarze).
- **Zarządzanie Użytkownikami**: Blokowanie kont, wysyłanie globalnych powiadomień systemowych.

---

## 📸 Galeria

> Poniżej przedstawione są najważniejsze widoki aplikacji.

### 🖥️ Aplikacja Webowa

<div align="center">

|                             Kreator Planu (30 dni)                              |                           Profil i Statystyki                            |                               Panel Admina                               |
| :-----------------------------------------------------------------------------: | :----------------------------------------------------------------------: | :----------------------------------------------------------------------: |
|                  _Widok edycji planu z podziałem na tygodnie_                   |                _Widok profilu z odznakami i wykresem BMI_                |                _Zarządzanie zgłoszeniami i użytkownikami_                |
| <img src="images/web-planner.png" width="100%" alt="Edytor planu treningowego"> | <img src="images/web-profile.png" width="100%" alt="Profil użytkownika"> | <img src="images/web-admin.png" width="100%" alt="Panel administratora"> |

</div>

### 📱 Aplikacja Mobilna

<div align="center">

|                            Tryb Treningu (Krok po kroku)                            |                          Social Feed & Komentarze                          |                               Ustawienia i Motywy                                |
| :---------------------------------------------------------------------------------: | :------------------------------------------------------------------------: | :------------------------------------------------------------------------------: |
|                             _Ekran aktywnego ćwiczenia_                             |                      _Widok dyskusji pod ćwiczeniem_                       |                      _Tryb jasny i ustawienia prywatności_                       |
| <img src="images/mobile-active-workout.png" width="200" alt="Tryb treningu mobile"> | <img src="images/mobile-comments.png" width="200" alt="Komentarze mobile"> | <img src="images/mobile-settings-light.png" width="200" alt="Ustawienia mobile"> |

## </div>

## 🚀 Uruchomienie

Aby uruchomić projekt lokalnie, postępuj zgodnie z poniższymi instrukcjami dla każdego modułu.

### Wymagania wstępne

- Node.js (v18+)
- Android Studio (dla modułu mobile)
- MongoDB (lokalnie lub Atlas)

### Krok 0: Pobranie projektu

```bash
git clone [https://github.com/nazwa_uzytkownika/gym-eaze.git](https://github.com/nazwa_uzytkownika/gym-eaze.git)
cd gym-eaze
```

### Krok 1: Konfiguracja Google OAuth 2.0

Aby logowanie działało poprawnie, musisz skonfigurować projekt w [Google Cloud Console](https://console.cloud.google.com/):

1. Przejdź do **API & Services** > **Credentials** (Dane logowania).
2. Kliknij **Create Credentials** > **OAuth client ID**.
3. **Stwórz Klienta Android**:
   - **Nazwa pakietu**: `com.example.gymeaze`
   - **SHA-1**: Twój odcisk klucza debuggowania (uzyskasz go w Android Studio > Gradle > Tasks > android > signingReport).
4. **Stwórz Klienta Aplikacji Internetowej (Web Client)**:
   - **Authorized JavaScript origins**:
     - `http://localhost:5173`
     - `http://localhost`
   - **Authorized redirect URIs**:
     - `http://localhost:5173`
     - `https://developers.google.com/oauthplayground`
5. **Ważne:** Skopiuj wygenerowany `CLIENT_ID` (Typu "Web Application") – będzie on potrzebny w konfiguracji backendu, frontendu oraz aplikacji mobilnej.

### Krok 2: Backend (API)

```bash
cd backend
npm install
```

1. Zmień nazwę pliku .env.example na .env.

2. Uzupełnij klucze w .env

3. Uruchom serwer:

```bash
npm run dev
```

### Krok 3: Frontend (Web)

```bash
cd frontend
npm install
```

1. Zmień nazwę pliku .env.example na .env.

2. Uzupełnij klucze w .env

3. Uruchom serwer:

```bash
npm run dev
```

Aplikacja dostępna pod adresem: http://localhost:5173

### Krok 4: Mobile (Android)

1. Przejdź do katalogu mobile/.

2. Utwórz plik local.properties (jeśli nie istnieje) w głównym katalogu mobile/.

3. Wklej i uzupełnij poniższą konfigurację:

```bash
## Ten plik nie jest wersjonowany. Zawiera lokalne ścieżki i klucze.

# Ścieżka do SDK (zazwyczaj Android Studio generuje ją automatycznie)
# Windows: C:\Users\TwojaNazwa\AppData\Local\Android\Sdk
# Mac/Linux: /Users/TwojaNazwa/Library/Android/sdk
sdk.dir=TWOJA_SCIEZKA_DO_SDK

# Adres backendu
# (Dla emulatora Androida localhost to zazwyczaj 10.0.2.2)
API_URL="[http://10.0.2.2:5000](http://10.0.2.2:5000)"

# Klient ID z Google Cloud Console (Web Client ID)
GOOGLE_CLIENT_ID="TWOJ_GOOGLE_CLIENT_ID"
```

4. Otwórz projekt w Android Studio, poczekaj na synchronizację Gradle (może to chwilę potrwać) i uruchom aplikację na emulatorze.

## 🧪 Testy

Weryfikacja poprawności działania systemu została przeprowadzona w procesie **testów manualnych**, obejmujących zarówno warstwę logiczną, jak i interfejs użytkownika.

### ✅ Zakres testów

- **Backend & API**:
  Weryfikacja endpointów REST API została przeprowadzona przy użyciu narzędzia **Insomnia**. Sprawdzono:

  - Poprawność kodów odpowiedzi HTTP (200, 400, 401, 500).
  - Działanie zabezpieczeń (JWT, middleware autoryzacji).
  - Walidację danych wejściowych (np. przy tworzeniu planów).

- **Kluczowe scenariusze manualne**:
  - **Synchronizacja Cross-Platform**: Utworzenie planu w aplikacji webowej i natychmiastowe podjęcie treningu w aplikacji mobilnej.
  - **OAuth 2.0**: Logowanie przez Google oraz odświeżanie sesji użytkownika.
  - **Obsługa Multimediów**: Przesyłanie zdjęć i wideo do Cloudinary oraz ich poprawne renderowanie na różnych urządzeniach.
  - **Panel Administratora**: Próba dostępu do panelu admina z poziomu zwykłego użytkownika (blokada dostępu).
  - **Cykl Treningowy**: Przejście pełnego trybu "krok po kroku" na mobile, od startu do zapisania historii treningu.

> 💡 **Oraz wiele więcej...**
> Zadbaliśmy o dziesiątki drobnych detali UX/UI, które odkryjesz podczas korzystania z aplikacji.

## 👥 Autorzy

Za projektem GymEaze stoi trójka entuzjastów kodu i sportu. Każdy z nas wniósł inny ciężar, aby dowieźć ten projekt do końca.

<div align="center">

|                 <a href="https://github.com/evee03"><img src="https://github.com/evee03.png" width="100px;" alt=""/></a>                  |                <a href="https://github.com/Squashim"><img src="https://github.com/Squashim.png" width="100px;" alt=""/></a>                 |                 <a href="https://github.com/Kuba27x"><img src="https://github.com/Kuba27x.png" width="100px;" alt=""/></a>                 |
| :---------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------------------: |
|                                                           **Ewelina Musińska**                                                            |                                                             **Konrad Piekarz**                                                              |                                                            **Jakub Pieńkowski**                                                            |
| [![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/evee03) | [![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Squashim) | [![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Kuba27x) |

</div>
