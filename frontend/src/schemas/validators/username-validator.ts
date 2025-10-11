import { z } from 'zod';

const usernameValidator = z
  .string()
  .min(1, 'Nazwa użytkownika jest wymagana.')
  .min(6, 'Nazwa użytkownika musi mieć co najmniej 6 znaków.')
  .max(16, 'Nazwa użytkownika nie może być dłuższa niż 16 znaków.')
  .regex(/[0-9a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]/, 'Nazwa użytkownika może zawierać tylko litery i cyfry');

export default usernameValidator;
