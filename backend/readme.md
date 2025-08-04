# GymEaze Backend

## Uruchamianie backendu

1. **Przejdź do katalogu backend:**

   ```sh
   cd backend
   ```

2. **Utwórz plik `.env` w glownym folderze czyli `backend` i ustaw zmienne środowiskowe:**

   ```env
   MONGODB_URI=tu_wstaw_swoje_URI_do_MongoDBAtlas
   PORT=standard_to_3000
   ```

3. **Zainstaluj zależności:**

   ```sh
   npm install
   ```

4. **Aby uruchomić backend wpisz:**
   ```sh
   npm run dev
   ```

## Uruchamianie seeda

1. **Uruchom seeda:**

   Jeśli chcesz sprawdzić dane lub je dodac do bazy to odkometuj lub zakomentuj funkcje w `seed.ts`

   ```sh
   npm run seed
   ```

## Dokumentacja

1. **Dokumentacja API: **

   Dokumentacja znajduje się pod `/api-docs`
