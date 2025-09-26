import { useEffect, useState } from 'react';

function useScrollToTop(vh: number = 150) {
  const [show, setShow] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setShow(window.scrollY > window.innerHeight * (vh / 100));
    };
    window.addEventListener('scroll', handleScroll);
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, [vh]);

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return { show, scrollToTop };
}

export default useScrollToTop;
