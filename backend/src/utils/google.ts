export const getGoogleUser = async (code: string) => {
  const accessToken = await exchangeCodeForAccessToken(code);
  const userDetails = await verifyGoogleToken(accessToken);
  return { ...userDetails, google_accessToken: accessToken };
};

export const verifyGoogleToken = async (token: string) => {
  const response = await fetch('https://www.googleapis.com/oauth2/v3/userinfo', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!response.ok) {
    throw new Error(response.status === 401 ? 'INVALID_TOKEN' : 'AUTH_FAILED');
  }

  const user = await response.json();

  return {
    email: user.email,
    googleId: user.sub,
    name: user.name,
    email_verified: user.email_verified,
  };
};

const exchangeCodeForAccessToken = async (code: string) => {
  if (
    !process.env.GOOGLE_CLIENT_ID ||
    !process.env.GOOGLE_CLIENT_SECRET ||
    !process.env.GOOGLE_REDIRECT_URI
  ) {
    throw new Error('GOOGLE_OAUTH_NOT_CONFIGURED');
  }

  const response = await fetch('https://oauth2.googleapis.com/token', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({
      code,
      client_id: process.env.GOOGLE_CLIENT_ID,
      client_secret: process.env.GOOGLE_CLIENT_SECRET,
      redirect_uri: process.env.GOOGLE_REDIRECT_URI,
      grant_type: 'authorization_code',
    }),
  });

  if (!response.ok) {
    throw new Error('INVALID_CODE');
  }

  const tokens = await response.json();

  return tokens.access_token;
};
