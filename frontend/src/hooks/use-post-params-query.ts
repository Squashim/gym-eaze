import type { PostQueryParams } from '@/types/filters';
import { useCallback, useMemo } from 'react';
import { useSearchParams } from 'react-router-dom';

function usePostParamsQuery() {
  const [searchParams, setSearchParams] = useSearchParams();

  const hashtags = searchParams.getAll('hashtags');
  const followedOnly = searchParams.get('followedOnly') === 'true';
  const cursor = searchParams.get('cursor') ?? undefined;

  const params: PostQueryParams = useMemo(() => {
    return {
      hashtags,
      followedOnly,
      cursor,
    };
  }, [hashtags, followedOnly, cursor]);

  const setHashtags = useCallback(
    (tags: string[]) => {
      const urlParams = new URLSearchParams(searchParams);
      urlParams.delete('hashtags');
      tags.forEach((tag) => urlParams.append('hashtags', tag));
      setSearchParams(urlParams);
    },
    [searchParams, setSearchParams],
  );

  const setParam = useCallback(
    (key: keyof PostQueryParams, value: string | boolean | null) => {
      const urlParams = new URLSearchParams(searchParams);
      urlParams.set(String(key), String(value));
      setSearchParams(urlParams);
    },
    [searchParams, setSearchParams],
  );

  const clearParams = useCallback(() => {
    setSearchParams(new URLSearchParams());
  }, [setSearchParams]);

  return {
    params,
    setHashtags,
    setParam,
    clearParams,
  };
}

export default usePostParamsQuery;
