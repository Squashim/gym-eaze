import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

type GuardFunction = () => boolean | Promise<boolean> | 'wait';

interface Guard {
  check: GuardFunction;
  redirectTo: string;
  errorMessage: string;
}

export function useSequentialGuards(guards: Guard[]): {
  status: 'checking' | 'passed' | 'failed';
  ready: boolean;
  passed: boolean;
} {
  const navigate = useNavigate();
  const [status, setStatus] = useState<'checking' | 'passed' | 'failed'>('checking');

  useEffect(() => {
    let cancelled = false;

    const runGuards = async () => {
      for (const { check, redirectTo, errorMessage } of guards) {
        const result = await check();

        if (cancelled) return;

        if (result === true) continue;

        if (result === false) {
          toast.error(errorMessage);
          await navigate(redirectTo, { replace: true });
          setStatus('failed');
          return;
        }

        return;
      }

      setStatus('passed');
    };

    void runGuards();
    return () => {
      cancelled = true;
    };
  }, [guards, navigate]);

  return {
    status,
    ready: status !== 'checking',
    passed: status === 'passed',
  };
}
