import { Moon, Sun } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { useTheme } from '@/context/theme';
import { Tooltip, TooltipContent, TooltipTrigger } from './tooltip';
import Typography from './typography';

function ThemeToggle() {
  const { theme, setTheme } = useTheme();
  const userTheme =
    theme === 'system'
      ? window.matchMedia('(prefers-color-scheme: dark)').matches
        ? 'dark'
        : 'light'
      : theme;

  const toggleTheme = () => {
    setTheme(userTheme === 'dark' ? 'light' : 'dark');
  };

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button
          aria-describedby="theme-tooltip-description"
          onClick={toggleTheme}
          size="icon"
          variant="ghost"
        >
          <Sun className="absolute size-5 scale-0 rotate-90  transition-all dark:scale-100 dark:rotate-0" />
          <Moon className="absolute size-5 transition-all scale-100 rotate-0 dark:scale-0 dark:-rotate-90" />
          <span className="sr-only">Zmiana motywu</span>
        </Button>
      </TooltipTrigger>
      <TooltipContent id="theme-tooltip-description">
        <Typography variant="small">
          Zmień motyw na {userTheme === 'dark' ? 'jasny' : 'ciemny'}
        </Typography>
      </TooltipContent>
    </Tooltip>
  );
}

export default ThemeToggle;
