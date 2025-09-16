import { useEffect } from 'react';
import { useBlocker, useNavigate } from 'react-router-dom';

export function useUnsavedChangesWarning(hasUnsavedChanges: boolean) {
  const navigate = useNavigate();

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      hasUnsavedChanges && currentLocation.pathname !== nextLocation.pathname,
  );

  useEffect(() => {
    if (blocker.state === 'blocked') {
      const shouldLeave = window.confirm(
        'Wprowadzone zmiany mogą nie zostać zapisane. Czy na pewno chcesz opuścić tę stronę?',
      );

      if (shouldLeave) {
        blocker.proceed?.();
      } else {
        blocker.reset?.();
      }
    }
  }, [blocker]);

  const handleCancel = () => {
    void navigate(-1);
  };

  useEffect(() => {
    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      if (hasUnsavedChanges) {
        e.preventDefault();
        e.returnValue = '';
        return '';
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [hasUnsavedChanges]);

  return {
    handleCancel,
  };
}
