import { Button } from '@/components/ui/button';
import Typography from '@/components/ui/typography';
import { isRouteErrorResponse, useRouteError } from 'react-router-dom';

const Error = () => {
  const error = useRouteError();
  return (
    <div className="flex flex-col gap-6 w-full h-dvh justify-center items-center">
      <header className="grid gap-4">
        <Typography variant="h1">Oops! Coś poszło nie tak.</Typography>
        {isRouteErrorResponse(error) && error.status === 404 ? (
          <>
            <Typography variant="h3">Błąd 404 - Nie znaleziono strony.</Typography>
            <pre className="bg-muted p-4 rounded text-xs max-w-xl overflow-auto">{error.data}</pre>
          </>
        ) : (
          <Typography variant="h3">Wystąpił nieznany błąd.</Typography>
        )}
      </header>
      <Button asChild size="lg">
        <a href="/">Powrót do strony głównej</a>
      </Button>
    </div>
  );
};

export default Error;
