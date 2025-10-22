import { BmiBadge } from '@/components/ui/item-info-badges';
import Typography from '@/components/ui/typography';
import { Gender } from '@/constants/enums/gender';
import { parseAgeLabel } from '@/lib/parsers';
import { calculateBMI } from '@/lib/utils';
import type { UserFitnessDetailsPayload } from '@/types/user';
import { Mars, Venus } from 'lucide-react';
import React from 'react';

interface FitnessRowProps {
  details: UserFitnessDetailsPayload;
}

function FitnessRow({ details }: FitnessRowProps) {
  if (!details) return null;

  const items: React.ReactNode[] = [];

  if (details.gender && details.gender !== Gender.none) {
    items.push(
      <span className="flex gap-1 items-center text-muted-foreground" key="gender">
        {details.gender === Gender.female ? (
          <Venus className="size-4 text-secondary" />
        ) : (
          <Mars className="size-4 text-secondary" />
        )}
        <Typography className="text-xs font-semibold" variant="small">
          {details.gender}
        </Typography>
      </span>,
    );
  }

  if (details.age) {
    items.push(
      <Typography className="text-xs font-semibold text-muted-foreground" key="age" variant="small">
        {parseAgeLabel(details.age)}
      </Typography>,
    );
  }

  if (details.height) {
    items.push(
      <Typography
        className="text-xs font-semibold text-muted-foreground"
        key="height"
        variant="small"
      >
        {details.height} cm
      </Typography>,
    );
  }

  if (details.weight) {
    items.push(
      <Typography
        className="text-xs font-semibold text-muted-foreground"
        key="weight"
        variant="small"
      >
        {details.weight} kg
      </Typography>,
    );
  }

  if (details.weight && details.height) {
    const bmi = calculateBMI(details.weight, details.height);
    items.push(<BmiBadge bmi={bmi} key="bmi" />);
  }

  if (items.length === 0) return null;

  return (
    <div className="flex gap-0.5 items-center flex-wrap">
      {items.map((item, idx) => (
        <React.Fragment key={idx}>
          {item}
          {idx < items.length - 1 && <span className="mx-1 text-muted-foreground">•</span>}
        </React.Fragment>
      ))}
    </div>
  );
}

export default FitnessRow;
