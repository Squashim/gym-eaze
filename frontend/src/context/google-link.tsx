import type { GoogleLoginConflictResponse } from '@/types/api';
import { createContext, useContext, useState, type PropsWithChildren } from 'react';

type GoogleLinkContextType = {
  googleLinkData: GoogleLoginConflictResponse | undefined;
  setGoogleLinkData: (data: GoogleLoginConflictResponse | undefined) => void;
};

const GoogleLinkContext = createContext<GoogleLinkContextType | undefined>(undefined);

function GoogleLinkProvider({ children }: PropsWithChildren) {
  const [googleLinkData, setGoogleLinkData] = useState<GoogleLoginConflictResponse | undefined>(
    undefined,
  );

  return (
    <GoogleLinkContext.Provider value={{ googleLinkData, setGoogleLinkData }}>
      {children}
    </GoogleLinkContext.Provider>
  );
}

function useGoogleLink() {
  const ctx = useContext(GoogleLinkContext);
  if (!ctx) throw new Error('useGoogleLink() must be used within a GoogleLinkProvider');
  return ctx;
}

// eslint-disable-next-line react-refresh/only-export-components
export { GoogleLinkProvider, useGoogleLink };
