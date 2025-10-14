import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import type { GetReportsApiResponse } from '@/types/report';

interface ReportGroupProps {
  report: GetReportsApiResponse;
}

function ReportGroup({ report }: ReportGroupProps) {
  // const getStatusIcon = (status: string) => {
  //   switch (status) {
  //     case ReportStatus.pending:
  //       return <AlertTriangle className="w-4 h-4 text-yellow-500" />;
  //     case ReportStatus.resolved:
  //       return <CheckCircle className="w-4 h-4 text-green-500" />;
  //     case ReportStatus.dismissed:
  //       return <XCircle className="w-4 h-4 text-red-500" />;
  //     default:
  //       return <AlertTriangle className="w-4 h-4 text-gray-500" />;
  //   }
  // };

  // const getStatusVariant = (status: string) => {
  //   switch (status) {
  //     case ReportStatus.pending:
  //       return 'destructive';
  //     case ReportStatus.resolved:
  //       return 'default';
  //     case ReportStatus.dismissed:
  //       return 'secondary';
  //     default:
  //       return 'outline';
  //   }
  // };

  return (
    <Card>
      <CardHeader className="gap-2 ">
        <div>
          {/* {exercise.advancedLevel !== AdvancedLevel.none && (
              <AdvancedLevelBadge advancedLevel={exercise.advancedLevel} />
            )}
          </div>

          <LikesBadge likes={exercise.likes} /> */}
        </div>

        {/* <div className="flex w-full aspect-[4/3] rounded-xl overflow-hidden relative">
          <img
            alt={`zdjęcie poglądowe ćwiczenia ${exercise.name}`}
            className="w-full h-full object-cover rounded-xl transition-transform duration-500 ease-out group-hover:scale-105 dark:grayscale-[0.2] dark:contrast-110 bg-primary/5"
            src={exercise.image}
          />
        </div> */}

        <CardTitle className="text-xl line-clamp-2">
          {report.reportedItem.name || report.reportedItem.content}
        </CardTitle>
        {/* <CardDescription>{displayMuscleGroups}</CardDescription> */}
        <CardContent className="p-0 flex flex-col gap-3">
          {/* {exercise.hashtags && exercise.hashtags.length > 0 && (
            <>
              <Separator />
              <div className="flex flex-wrap gap-1">
                {visibleHashtags.map((hashtag) => (
                  <Badge key={hashtag} variant="outline">
                    {`#${hashtag}`}
                  </Badge>
                ))}
                {hiddenHashtagsCount > 0 && (
                  <Badge variant="outline">+{hiddenHashtagsCount} więcej</Badge>
                )}
              </div>
            </>
          )} */}
        </CardContent>
      </CardHeader>
      {/* {pageType !== 'user' && pageType !== 'profile' && (
        <CardFooter className="mt-auto ml-auto text-sm text-muted-foreground">
          Dodane przez: {exercise.author.username}
        </CardFooter>
      )} */}
    </Card>
  );
}

export default ReportGroup;
