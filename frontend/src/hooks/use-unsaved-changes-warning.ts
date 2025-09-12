import { useCallback, useState } from 'react';
import { useBeforeUnload, useNavigate } from 'react-router-dom';

export function useUnsavedChangesWarning(shouldWarn: boolean) {
  const [showDialog, setShowDialog] = useState(false);
  const navigate = useNavigate();

  useBeforeUnload(
    useCallback(
      (event: BeforeUnloadEvent) => {
        if (shouldWarn) {
          event.preventDefault();
          event.returnValue = '';
        }
      },
      [shouldWarn],
    ),
  );

  const tryNavigateBack = useCallback(() => {
    if (shouldWarn) {
      setShowDialog(true);
    } else {
      void navigate(-1);
    }
  }, [shouldWarn, navigate]);

  const confirmNavigation = useCallback(() => {
    setShowDialog(false);
    void navigate(-1);
  }, [navigate]);

  const cancelNavigation = useCallback(() => {
    setShowDialog(false);
  }, []);

  return { showDialog, confirmNavigation, cancelNavigation, tryNavigateBack };
}
