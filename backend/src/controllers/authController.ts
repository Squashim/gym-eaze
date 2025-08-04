import { Request, Response } from "express";
import {
  registerUser,
  loginUser,
  refreshAccessToken,
  logoutUser,
  googleLogin,
} from "../services/authService";

export interface JwtPayload {
  userId: string;
}

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
    res.status(201).json({ message: "Rejestracja udana" });
  } catch (err: unknown) {
    let message = "Błąd rejestracji";
    if (err instanceof Error) {
      const messages: Record<string, string> = {
        FIELDS_REQUIRED: "Wszystkie pola są wymagane",
        EMAIL: "Użytkownik o takim emailu już istnieje",
        EMAIL_LENGTH: "Email musi mieć od 2 do 320 znaków",
        USERNAME: "Użytkownik o takim username już istnieje",
        USERNAME_LENGTH: "Username musi mieć od 6 do 16 znaków",
        PASSWORD_LENGTH: "Hasło musi",
        PASSWORD: "Nieprawidłowe hasło.",
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

export const login = async (req: Request, res: Response): Promise<void> => {
  const { login, password } = req.body;

  if (!login || !password) {
    res.status(400).json({ message: "Username/email i hasło są wymagane" });
    return;
  }

  try {
    const { accessToken, refreshToken } = await loginUser(login, password);

    res.cookie("refreshToken", refreshToken, {
      httpOnly: true,
      secure: true,
      sameSite: "strict",
      maxAge: 7 * 24 * 60 * 60 * 1000,
    });

    res.cookie("accessToken", accessToken, {
      httpOnly: true,
      secure: true,
      sameSite: "strict",
      maxAge: 15 * 60 * 1000,
    });

    res.status(200).json({
      message: "Logowanie udane",
      accessToken,
      refreshToken,
    });
  } catch (err: unknown) {
    const message =
      err instanceof Error && err.message === "INVALID_CREDENTIALS"
        ? "Nieprawidłowe dane logowania"
        : "Błąd logowania";

    res.status(401).json({ message });
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
  const { token } = req.body;

  if (!token) {
    return res.status(400).json({ message: "Token Google jest wymagany" });
  }

  try {
    const result = await googleLogin(token);
    const { accessToken, refreshToken } = result;

    res.cookie("refreshToken", refreshToken, {
      httpOnly: true,
      secure: true,
      sameSite: "strict",
      maxAge: 7 * 24 * 60 * 60 * 1000,
    });

    res.cookie("accessToken", accessToken, {
      httpOnly: true,
      secure: true,
      sameSite: "strict",
      maxAge: 15 * 60 * 1000,
    });

    res.status(200).json({
      message: "Logowanie przez Google udane",
      accessToken,
      refreshToken,
    });
  } catch (err: unknown) {
    let message = "Błąd logowania przez Google";
    if (err instanceof Error) {
      if (err.message === "INVALID_GOOGLE_TOKEN") {
        message = "Nieprawidłowy token Google";
      }
    }
    res.status(401).json({ message });
  }
};

/**
 * @swagger
 * /api/auth/refresh:
 *   post:
 *     summary: Odświeżenie tokenu dostępowego
 *     description: |
 *       Odświeża token dostępowy (`accessToken`) na podstawie `refreshToken`, który może być przesłany w ciasteczku lub w treści żądania.
 *       Nowy accessToken jest również ustawiany w ciasteczku HttpOnly.
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
 *         description: Nowy token dostępowy został wygenerowany
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
  const token = req.cookies.refreshToken || req.body.token;

  if (!token) {
    return res.status(401).json({ message: "Brak tokenu odświeżania" });
  }

  try {
    const newAccessToken = await refreshAccessToken(token);

    res.cookie("accessToken", newAccessToken, {
      httpOnly: true,
      secure: true,
      sameSite: "strict",
      maxAge: 15 * 60 * 1000,
    });

    res.json({ accessToken: newAccessToken });
  } catch {
    res.status(403).json({ message: "Nieprawidlowy lub wygasły refreshToken" });
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
 *         description: Brak tokena
 *       '403':
 *         description: Nieprawidłowy lub wygasły refresh token
 */

export const logout = async (req: Request, res: Response) => {
  const token = req.cookies.refreshToken || req.body.token;
  if (!token) return res.sendStatus(400);

  try {
    await logoutUser(token);
    res.clearCookie("refreshToken");
    res.clearCookie("accessToken");
    res.json({ message: "Wylogowano" });
  } catch {
    res.sendStatus(403);
  }
};
