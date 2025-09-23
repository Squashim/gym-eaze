import { useState } from 'react';

export function useSessionFlag(key: string) {
  const [isSet, setIsSet] = useState(() => !!sessionStorage.getItem(key));

  const removeFlag = () => {
    sessionStorage.removeItem(key);
    setIsSet(false);
  };

  const setFlag = () => {
    sessionStorage.setItem(key, 'true');
    setIsSet(true);
  };

  return { isSet, setFlag, removeFlag };
}
