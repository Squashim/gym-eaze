import { Request, Response } from 'express';
import {
  registerUser,
  loginUser,
  refreshAccessToken,
  logoutUser,
  googleLogin,
  getCurrentUser,
  linkGoogleAccount,
} from '../services/authService';
import { AuthRequest } from '../types/auth';

/**
 * @swagger
 * /api/auth/register:
 *   post:
 *     summary: Rejestracja nowego użytkownika
 *     description: |
 *       Rejestracja nowego użytkownika w systemie.
 *       Dane do rejestracji użytkownika:
 *         - username (od 6 do 16 znaków)
 *         - email (od 2 do 320 znaków)
 *         - password (min. 14 znaków, 1 duża litera, 1 mała litera, 1 cyfra, 1 znak specjalny)
 *     tags:
 *       - Auth
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - username
 *               - email
 *               - password
 *             properties:
 *               username:
 *                 type: string
 *                 description: Nazwa użytkownika
 *                 example: jan_kowalski
 *               email:
 *                 type: string
 *                 format: email
 *                 description: Email użytkownika
 *                 example: jan.kowalski@example.com
 *               password:
 *                 type: string
 *                 description: Hasło użytkownika
 *                 example: SuperSecurePass123!
 *     responses:
 *       '201':
 *         description: Rejestracja udana - konto zostało utworzone
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                   example: Rejestracja udana
 *       '400':
 *         description: Błąd rejestracji - walidacja lub dane już istnieją
 */

export const register = async (req: Request, res: Response) => {
  const { username, email, password } = req.body;

  try {
    await registerUser(username, email, password);
    res.status(201).json({ message: 'Zarejestrowano pomyślnie' });
  } catch (err: unknown) {
    let message = 'Wystąpił nieznany błąd podczas rejestracji';
    if (err instanceof Error) {
      const messages: Record<string, string> = {
        REQUIRED_FIELDS: 'Wszystkie pola są wymagane',
        EMAIL: 'Użytkownik o takim emailu już istnieje',
        EMAIL_LENGTH: 'Email musi mieć od 2 do 320 znaków',
        USERNAME: 'Użytkownik o takiej nazwie już istnieje',
        USERNAME_LENGTH: 'Nazwa użytkownika musi mieć od 6 do 16 znaków',
        PASSWORD_LENGTH: 'Hasło musi miec co najmniej 14 znaków',
        INVALID_PASSWORD: 'Nieprawidłowe hasło.',
        GOOGLE_ID_ALREADY_USED: 'Ten adres email jest już powiązany z kontem Google',
      };

      message = messages[err.message] || message;
    }
    res.status(400).json({ message });
  }
};

/**
 * @swagger
 * /api/auth/login:
 *   post:
 *     summary: Logowanie użytkownika
 *     description: |
 *       Logowanie użytkownika za pomocą loginu (email lub username) oraz hasła.
 *       Zwraca refreshToken oraz accessToken w JSON oraz ustawia obydwa w ciasteczkach HttpOnly.
 *     tags:
 *       - Auth
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - login
 *               - password
 *             properties:
 *               login:
 *                 type: string
 *                 description: Login użytkownika (email lub username)
 *                 example: user@example.com
 *               password:
 *                 type: string
 *                 description: Hasło użytkownika
 *                 example: SuperSecurePassword123!
 *     responses:
 *       '200':
 *         description: Logowanie udane
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                   example: Logowanie udane
 *                 accessToken:
 *                   type: string
 *                   description: Token dostępu JWT
 *                   example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *                 refreshToken:
 *                   type: string
 *                   description: Token odświeżający JWT
 *                   example: dGhpc0lzQVRlc3RSZWZyZXNoVG9rZW4...
 *       '400':
 *         description: Brak loginu lub hasła
 *       '401':
 *         description: Nieprawidłowe dane logowania (np. błędny login lub hasło)
 */

export const login = async (req: Request, res: Response) => {
  const { login, password, rememberMe } = req.body;

  if (!login || !password) {
    res.status(400).json({ message: 'Nazwa użytkownika/email i hasło są wymagane' });
    return;
  }

  try {
    const { accessToken, refreshToken, isFirstLogin } = await loginUser(
      login,
      password,
      req.headers['user-agent'] || '',
      rememberMe,
    );

    res.cookie('refreshToken', refreshToken, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: rememberMe ? 30 * 24 * 60 * 60 * 1000 : 7 * 24 * 60 * 60 * 1000,
    });

    res.cookie('accessToken', accessToken, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: 15 * 60 * 1000,
    });

    res.status(200).json({
      message: 'Zalogowano pomyślnie',
      accessToken,
      refreshToken,
      isFirstLogin,
    });
  } catch (err: unknown) {
    if (err instanceof Error) {
      switch (err.message) {
        case 'USER_BLOCKED':
          return res.status(403).json({
            message: 'Twoje konto zostało zablokowane. Skontaktuj się z administratorem.',
          });
        case 'INVALID_CREDENTIALS':
          return res.status(401).json({ message: 'Nieprawidłowe dane logowania' });
        default:
          return res.status(401).json({ message: 'Błąd logowania' });
      }
    }

    res.status(401).json({ message: 'Błąd logowania' });
  }
};

/**
 * @swagger
 * /api/auth/google:
 *   post:
 *     summary: Logowanie lub rejestracja użytkownika przez Google
 *     description: |
 *       Uwierzytelnianie użytkownika za pomocą tokena Google.
 *       Jeżeli użytkownik nie istnieje, zostanie automatycznie zarejestrowany.
 *       Zwraca refreshToken oraz accessToken w formacie JSON oraz ustawia je w ciasteczkach HttpOnly.
 *     tags:
 *       - Auth
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - token
 *             properties:
 *               token:
 *                 type: string
 *                 description: Token ID otrzymany z Google OAuth
 *                 example: ya29.a0AfH6SMBv...
 *     responses:
 *       '200':
 *         description: Użytkownik został zalogowany lub zarejestrowany przez Google
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                   example: Logowanie przez Google udane
 *                 accessToken:
 *                   type: string
 *                   example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *                 refreshToken:
 *                   type: string
 *                   example: dGhpc0lzQVRlc3RSZWZyZXNoVG9rZW4...
 *       '400':
 *         description: Brak tokena Google w żądaniu
 *       '401':
 *         description: Nieprawidłowy token Google
 */

export const googleAuth = async (req: Request, res: Response) => {
  const { code } = req.body;

  if (!code) {
    return res.status(400).json({ message: 'Kod Google jest wymagany' });
  }

  try {
    const userAgent = req.headers['user-agent'] || '';
    const result = await googleLogin(code, userAgent);

    if (result.linkRequired) {
      return res.status(409).json({
        linkRequired: true,
        email: result.email,
        token: result.google_accessToken,
      });
    }

    res.cookie('refreshToken', result.refreshToken, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: 30 * 24 * 60 * 60 * 1000,
    });

    res.cookie('accessToken', result.accessToken, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: 15 * 60 * 1000,
    });

    res.status(200).json({
      message: 'Zalogowano pomyślnie',
      accessToken: result.accessToken,
      refreshToken: result.refreshToken,
      isFirstLogin: result.isFirstLogin,
    });
  } catch (error) {
    if (error instanceof Error) {
      switch (error.message) {
        case 'INVALID_TOKEN':
          return res.status(401).json({ message: 'Nieprawidłowy token Google' });
        case 'EMAIL_NOT_VERIFIED':
          return res
            .status(403)
            .json({ message: 'Adres email konta Google nie jest zweryfikowany.' });
        case 'INVALID_CODE':
          return res.status(400).json({ message: 'Nieprawidłowy kod autoryzacji Google' });
        case 'AUTH_FAILED':
          return res.status(401).json({ message: 'Autoryzacja Google nie powiodła się' });
        case 'GOOGLE_OAUTH_NOT_CONFIGURED':
          return res.status(500).json({ message: 'Konfiguracja Google OAuth jest niepełna' });
        default:
          return res.status(500).json({ message: 'Błąd serwera podczas autoryzacji' });
      }
    }
    return res.status(500).json({ message: 'Nieoczekiwany błąd serwera' });
  }
};

export const linkGoogle = async (req: Request, res: Response) => {
  const { email, password, token } = req.body;
  if (!email || !password || !token) {
    return res.status(400).json({ message: 'Email, hasło i token Google są wymagane' });
  }
  try {
    const userAgent = req.headers['user-agent'] || '';
    const result = await linkGoogleAccount(email, password, token, userAgent);

    res.cookie('refreshToken', result.refreshToken, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: 30 * 24 * 60 * 60 * 1000,
    });
    res.cookie('accessToken', result.accessToken, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: 15 * 60 * 1000,
    });

    res.status(200).json({
      message: 'Konto zostało połączone pomyślnie',
      accessToken: result.accessToken,
      refreshToken: result.refreshToken,
      isFirstLogin: result.isFirstLogin,
    });
  } catch (error) {
    let message = 'Błąd łączenia konta';
    if (error instanceof Error) {
      switch (error.message) {
        case 'INVALID_CREDENTIALS':
          message = 'Nieprawidłowe dane logowania';
          break;
        case 'INVALID_GOOGLE_ID':
          message = 'Nieprawidłowy Google ID lub nie należy do tego konta email';
          break;
        case 'GOOGLE_ID_ALREADY_USED':
          message = 'To konto Google jest już połączone z innym użytkownikiem';
          break;
        case 'ACCOUNT_ALREADY_LINKED':
          message = 'Konto jest już połączone z Google';
          break;
        case 'AUTH_FAILED':
          message = 'Autoryzacja Google nie powiodła się';
          break;
        case 'INVALID_TOKEN':
          message = 'Nieprawidłowy token Google';
          break;
        default:
          message = 'Błąd podczas łączenia konta';
          break;
      }
      return res.status(400).json({ message });
    }
    return res.status(500).json({ message: 'Wystąpił nieoczekiwany błąd serwera' });
  }
};

/**
 * @swagger
 * /api/auth/me:
 *   get:
 *     summary: Pobieranie danych o aktualnie zalogowanym użytkowniku
 *     description: |
 *       Zwraca informacje o profilu aktualnie zalogowanego użytkownika na podstawie accesstokenu
 *       Token musi być przesłany w ciasteczku `accessToken` lub w nagłówku Authorization (dla mobilki).
 *     tags:
 *       - Auth
 *     responses:
 *       '200':
 *         description: Dane użytkownika pobrane pomyślnie
 *       '404':
 *         description: Użytkownik nie znaleziony w bazie danych
 */

export const me = async (req: AuthRequest, res: Response) => {
  try {
    const user = await getCurrentUser(req.userId!);
    res.json(user);
  } catch {
    res.status(404).json({ message: 'Użytkownik nie znaleziony' });
  }
};

/**
 * @swagger
 * /api/auth/refresh:
 *   post:
 *     summary: Odświeżenie accessToken
 *     description: |
 *       Odświeża accesToken  na podstawie refreshTokenu, który jest przeslany w cookies oraz JSONIE.
 *       Nowy accessToken jest  ustawiany w ciasteczku oraz wysyłany w JSONIE.
 *     tags:
 *       - Auth
 *     requestBody:
 *       required: false
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               token:
 *                 type: string
 *                 description: Refresh token (opcjonalnie, jeżeli nie jest przesyłany w cookie)
 *                 example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *     responses:
 *       '200':
 *         description: AccessToken został wygenerowany
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 accessToken:
 *                   type: string
 *                   example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *       '401':
 *         description: Brak refresh tokena
 *       '403':
 *         description: Nieprawidłowy lub wygasły refresh token
 */

export const refresh = async (req: Request, res: Response) => {
  const token = req.cookies?.refreshToken || req.body?.token;

  if (!token) {
    return res.status(401).json({ message: 'Brak tokenu odświeżającego' });
  }

  try {
    const newAccessToken = await refreshAccessToken(token);

    res.cookie('accessToken', newAccessToken, {
      httpOnly: true,
      secure: true,
      sameSite: 'strict',
      maxAge: 15 * 60 * 1000,
    });

    res.json({ accessToken: newAccessToken });
  } catch (error: unknown) {
    if (error instanceof Error) {
      return res.status(403).json({ message: error.message });
    }
    return res.status(403).json({ message: 'Nieprawidłowy lub wygasły token odświeżający' });
  }
};

/**
 * @swagger
 * /api/auth/logout:
 *   post:
 *     summary: Wylogowanie użytkownika
 *     description: |
 *       Wylogowuje użytkownika, usuwając refresh token z bazy i czyszcząc ciasteczka `refreshToken` oraz `accessToken`.
 *       Refresh token może być przekazany w treści żądania lub jako cookie.
 *     tags:
 *       - Auth
 *     requestBody:
 *       required: false
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               token:
 *                 type: string
 *                 description: Refresh token (opcjonalnie, jeśli nie jest przesyłany w cookie)
 *                 example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *     responses:
 *       '200':
 *         description: Użytkownik został pomyślnie wylogowany
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                   example: Wylogowano
 *       '400':
 *         description: Brak refreshTokenu
 *       '403':
 *         description: Nieprawidłowy/wygasly refresh token
 */

export const logout = async (req: Request, res: Response) => {
  const token = req.cookies.refreshToken || req.body.token;
  if (!token) return res.status(400).json({ message: 'Brak tokenu odświeżającego' });

  try {
    await logoutUser(token);
    res.clearCookie('refreshToken');
    res.clearCookie('accessToken');
    res.json({ message: 'Wylogowano pomyślnie' });
  } catch (error: unknown) {
    if (error instanceof Error) {
      return res.status(403).json({ message: error.message });
    }
    return res.status(403).json({ message: 'Nieprawidłowy/wygasły token odświeżający' });
  }
};
