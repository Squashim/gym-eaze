import { Link } from 'react-router-dom';

const Signup = () => {
  return (
    <div>
      {' '}
      <div className="text-muted-foreground *:[a]:hover:text-accent-foreground transition-color text-center text-xs text-balance *:[a]:underline *:[a]:underline-offset-4">
        Tworząc konto zgadzasz się na nasze <Link to="#">Warunki świadczenia usług</Link> i{' '}
        <Link to="#">Politykę prywatności</Link>.
      </div>
    </div>
  );
};

export default Signup;
