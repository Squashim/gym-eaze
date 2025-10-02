import { Alert, AlertTitle } from './alert';
import { Info, Loader2 } from 'lucide-react';

type Items = 'post' | 'comment' | 'reply';

interface ScrollLoaderProps {
  isFetchingNextPage: boolean;
  hasNextPage: boolean;
  itemType: Items;
  itemLength: number;
  showEndMessage?: boolean;
  ref: (node?: Element | null) => void;
}

const ITEM_TYPE_MESSAGES: Record<Items, { loading: string; noMore?: string }> = {
  post: {
    loading: 'Ładowanie kolejnych postów...',
    noMore: 'Brak kolejnych postów.',
  },
  comment: {
    loading: 'Ładowanie kolejnych komentarzy...',
  },
  reply: {
    loading: 'Ładowanie kolejnych odpowiedzi...',
  },
};

function ScrollLoader({
  isFetchingNextPage,
  hasNextPage,
  itemType,
  showEndMessage = false,
  itemLength,
  ref,
}: ScrollLoaderProps) {
  const messages = ITEM_TYPE_MESSAGES[itemType];

  if (isFetchingNextPage) {
    return (
      <Alert>
        <Loader2 className="animate-spin" />
        <AlertTitle>{messages.loading}</AlertTitle>
      </Alert>
    );
  }

  if (hasNextPage && itemLength > 0) {
    return <div className="h-2" ref={ref} />;
  }

  if (!hasNextPage && itemLength > 0 && showEndMessage) {
    return (
      <Alert>
        <Info />
        <AlertTitle>{messages.noMore}</AlertTitle>
      </Alert>
    );
  }

  return null;
}

export default ScrollLoader;
