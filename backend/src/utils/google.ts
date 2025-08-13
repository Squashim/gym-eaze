export const verifyGoogleToken = async (token: string) => {
  try {
    // Weryfikacja przez Google API
    const response = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      return null;
    }

    const userInfo = await response.json();
    return {
      email: userInfo.email,
      googleId: userInfo.sub,
      name: userInfo.name,
    };
  } catch (error) {
    return null;
  }
};
