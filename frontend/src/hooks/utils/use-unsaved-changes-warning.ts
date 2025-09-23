import { useEffect, useRef } from 'react';
import { useBlocker } from 'react-router-dom';

export function useUnsavedChangesWarning(hasUnsavedChanges: boolean) {
  const shouldBypassBlocker = useRef(false);

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      hasUnsavedChanges &&
      !shouldBypassBlocker.current &&
      currentLocation.pathname !== nextLocation.pathname,
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

  const bypassBlocker = () => {
    shouldBypassBlocker.current = true;
  };

  const handleCancel = () => {
    window.history.back();
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
    onCancel: handleCancel,
    bypassBlocker,
  };
}
