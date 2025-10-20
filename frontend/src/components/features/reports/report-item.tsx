import { Button } from '@/components/ui/button';
import { InfoField } from '@/components/ui/info-fields';
import ReusableAvatar from '@/components/ui/reusable-avatar';
import Typography from '@/components/ui/typography';
import type { GetReportApiResponse } from '@/types/report';

function Report({ report }: { report: GetReportApiResponse }) {
  return (
    <div className="rounded-md flex flex-wrap items-start justify-between gap-4 p-4 shadow-md border bg-accent/50 text-card-foreground">
      <div className="flex flex-col gap-3">
        <InfoField label="Powód zgłoszenia">{report.reportType}</InfoField>
        {report.reason && (
          <InfoField label="Dodatkowe informacje">
            <Typography className="break-words whitespace-pre-wrap max-h-48 overflow-y-auto scrollbar px-2 py-1 rounded-md bg-accent text-sm text-accent-foreground">
              {report.reason}
            </Typography>
          </InfoField>
        )}
        <Typography className="text-xs" variant="muted">
          {`Zgłoszono: ${new Date(report.createdAt).toLocaleString()}`}
        </Typography>
      </div>

      <InfoField label="Zgłoszone przez">
        <Button asChild className="p-2 mt-2" variant="ghost">
          <a
            className="hover:ring-ring/50 hover:ring-[3px]"
            href={`/profile/${report.reporterId.username}`}
            rel="noreferrer"
            target="_blank"
            title="Zobacz profil zgłaszającego"
          >
            <ReusableAvatar
              className="cursor-pointer shadow-sm size-8"
              profilePicture={report.reporterId.profileDetails?.profilePicture}
              username={report.reporterId.username}
            />
            {report.reporterId.username}
          </a>
        </Button>
      </InfoField>
    </div>
  );
}

export default Report;
