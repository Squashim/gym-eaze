import LogoPath from '@/assets/images/logo.webp';
import { useTheme } from '../../context/theme';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/utils';
import { Link } from 'react-router-dom';

const logoVariants = cva('transition-all duration-300 ease-in-out ', {
  variants: {
    size: {
      default: 'h-16 w-16 ',
      sm: 'w-8 h-8',
      lg: 'h-32 w-32',
    },
    darkMode: {
      true: 'filter invert',
      false: 'filter invert-0',
    },
  },
  defaultVariants: {
    size: 'default',
    darkMode: false,
  },
});

function Logo({
  className,
  size,
  redirect = false,
  ...props
}: React.ComponentProps<'img'> &
  VariantProps<typeof logoVariants> & {
    redirect?: boolean;
  }) {
  const { theme } = useTheme();
  const isDarkMode =
    theme === 'dark' ||
    (theme === 'system' && window.matchMedia('(prefers-color-scheme: dark)').matches);

  const logoImage = (
    <img
      alt="logo strony "
      className={cn(logoVariants({ className, size, darkMode: isDarkMode }))}
      loading="eager"
      src={LogoPath}
      {...props}
    />
  );

  return redirect ? <Link to="/">{logoImage}</Link> : logoImage;
}

export default Logo;
