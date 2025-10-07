import { cn } from '@/lib/utils';
import type { PostQueryParams } from '@/types/filters';
import { Switch } from '@/components/ui/switch';
import { TagsInput } from '@/components/ui/tags-input';
import Typography from '@/components/ui/typography';
import { useMemo, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Eraser, Filter } from 'lucide-react';
import { Badge } from '@/components/ui/badge';

interface PostFiltersProps {
  params: PostQueryParams;
  isLogged: boolean;
  setParam: (key: keyof PostQueryParams, value: string | boolean | null) => void;
  setHashtags: (tags: string[]) => void;
  clearParams: () => void;
}

const PostFilters = ({
  params,
  isLogged,
  setParam,
  setHashtags,
  clearParams,
}: PostFiltersProps) => {
  const [isFiltersOpen, setIsFiltersOpen] = useState(false);

  const activeFiltersCount = useMemo(() => {
    return (Number(params.hashtags?.length) || 0) + (params.followedOnly ? 1 : 0);
  }, [params.hashtags, params.followedOnly]);

  const handleFiltersToggle = () => {
    setIsFiltersOpen(!isFiltersOpen);
  };

  return (
    <div className="bg-card text-card-foreground rounded-xl border p-4 shadow-sm flex flex-col w-full">
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <Typography className="font-semibold" variant="h4">
          Dopasuj wyświetlane posty
        </Typography>
        <div className="flex gap-4 flex-wrap flex-row-reverse sm:flex-row">
          <Button
            className={cn(activeFiltersCount > 0 ? 'flex' : 'hidden')}
            onClick={clearParams}
            variant="ghost"
          >
            <Eraser />
            Wyczyść filtry
          </Button>
          <Button className="relative" onClick={handleFiltersToggle} variant="outline">
            <Filter />
            {isFiltersOpen ? 'Ukryj filtry' : 'Pokaż filtry'}
            {activeFiltersCount > 0 && (
              <Badge
                className="absolute -top-1 -right-1 rounded-full min-w-5 h-5 px-1 tabular-nums"
                variant="secondary"
              >
                {activeFiltersCount}
              </Badge>
            )}
          </Button>
        </div>
      </div>

      {isFiltersOpen && (
        <div className="flex flex-col gap-3 w-full transition-opacity duration-300 opacity-100">
          <div className="grid gap-2">
            <Typography className="font-semibold" variant="small">
              Wyszukaj po hasztagach:
            </Typography>
            <TagsInput
              id="post-search-hashtags"
              onChange={(tags) => setHashtags(tags)}
              placeholder="Podaj hasztagi, np. biceps, triceps - użyj Entera lub przecinka"
              value={params.hashtags || []}
            />
          </div>
          {isLogged && (
            <div className="grid gap-2">
              <Typography className="font-semibold" variant="small">
                Pokaż posty obserwowanych użytkowników:
              </Typography>
              <div className="flex flex-row items-center justify-between rounded-md border border-input p-3 bg-transparent dark:bg-input/30 shadow-xs">
                <div className="grid gap-1">
                  <Typography variant="muted">
                    Włącz, aby wyświetlać tylko posty osób, które obserwujesz
                  </Typography>
                </div>
                <Switch
                  checked={params.followedOnly === true}
                  onCheckedChange={(checked) => setParam('followedOnly', checked)}
                />
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default PostFilters;
