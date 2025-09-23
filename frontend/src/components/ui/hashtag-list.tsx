import { Badge } from './badge';
import { InfoField } from './creator-info-fields';

function HashtagList({ hashtags }: { hashtags: string[] }) {
  if (!hashtags.length) return null;

  return (
    <InfoField fullWidth label="Hasztagi">
      <div className="flex flex-wrap gap-1 max-h-16 overflow-y-auto scrollbar">
        {hashtags.map((hashtag) => (
          <Badge key={hashtag} variant="outline">
            {`#${hashtag}`}
          </Badge>
        ))}
      </div>
    </InfoField>
  );
}

export default HashtagList;
