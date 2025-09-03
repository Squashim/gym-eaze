import { useEffect } from 'react';

export function useUnsavedChangesWarning(shouldWarn: boolean) {
  useEffect(() => {
    const handleClose = (e: BeforeUnloadEvent) => {
      if (!shouldWarn) return;
      e.preventDefault();
      e.returnValue = '';
    };

    window.addEventListener('beforeunload', handleClose);
    return () => window.removeEventListener('beforeunload', handleClose);
  }, [shouldWarn]);
}
