import ExerciseContent from '@/components/features/exercises/exercise-content';
import PageLayout from '@/components/layout/page';
import useGetFavoriteExercises from '@/hooks/query/exercises/use-get-favorite-exercises';
import usePageTitle from '@/hooks/utils/use-page-title';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';

const FavoriteExercises = () => {
  const { params, setPage, setPageSize } = usePaginationQuery();
  const { data: exercises, isLoading, isError } = useGetFavoriteExercises(params);
  usePageTitle('Ulubione ćwiczenia');

  return (
    <PageLayout
      description="To miejsce, w którym zebraliśmy Twoje ulubione ćwiczenia. Przeglądaj je i zarządzaj nimi wygodnie."
      title="Ulubione ćwiczenia"
    >
      <ExerciseContent
        exercises={exercises}
        isError={isError}
        isLoading={isLoading}
        pageType="favorite"
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
      />
    </PageLayout>
  );
};

export default FavoriteExercises;
