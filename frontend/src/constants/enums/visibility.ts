export const Visibility = {
  public: 'Publiczny',
  private: 'Prywatny',
} as const;

export type Visibility = (typeof Visibility)[keyof typeof Visibility];
