import { OAuth2Client } from 'google-auth-library';

const client = new OAuth2Client();

export const verifyGoogleToken = async (token: string) => {
  try {
    const ticket = await client.verifyIdToken({
      idToken: token,
      audience: [
        process.env.GOOGLE_CLIENT_ID!,
        process.env.GOOGLE_CLIENT_SECRET!, 
      ],
    });

    const payload = ticket.getPayload();
    if (!payload) {
      throw new Error('INVALID_GOOGLE_TOKEN');
    }

    return {
      email: payload.email!,
      googleId: payload.sub,
      name: payload.name!,
      email_verified: payload.email_verified!,
    };
  } catch (error) {
    console.error('Google token verification failed:', error);
    throw new Error('INVALID_GOOGLE_TOKEN');
  }
};