export const verifyGoogleToken = async (token: string) => {
  try {
    const response = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      throw new Error('INVALID_GOOGLE_TOKEN');
    }

    const userInfo = await response.json();
    return {
      email: userInfo.email,
      googleId: userInfo.sub,
      name: userInfo.name,
      email_verified: userInfo.email_verified,
    };
  } catch {
    throw new Error('INVALID_GOOGLE_TOKEN');
  }
};
