export const USER_PROFILE = {
  FIRST_NAME: {
    MIN: 3,
    MAX: 25,
  },
  LAST_NAME: {
    MIN: 3,
    MAX: 25,
  },
  BIO_MAX_LENGTH: 150,
  MAX_PINNED_BADGES: 5,
  AGE: {
    MIN: 18,
    MAX: 120,
  },
  HEIGHT: {
    MIN: 50,
    MAX: 300,
  },
  WEIGHT: {
    MIN: 10,
    MAX: 500,
  },
} as const;
