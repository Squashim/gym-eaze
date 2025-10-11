import PageLayout from '@/components/layout/page';
import useGetUserExercises from '@/hooks/query/exercises/use-get-user-exercises';
import usePageTitle from '@/hooks/utils/use-page-title';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import ExerciseContent from '@/components/features/exercises/exercise-content';

const UserExercises = () => {
  const { params, setPage, setPageSize } = usePaginationQuery();
  const {
    data: exercises,
    isLoading,
    isError,
  } = useGetUserExercises(params, { ownExercises: true });
  usePageTitle('Moje ćwiczenia');

  return (
    <PageLayout
      description="Tutaj znajdziesz wszystkie ćwiczenia, które sam stworzyłeś. Możesz je przeglądać, edytować i zarządzać nimi według własnych potrzeb."
      title="Moje ćwiczenia"
    >
      <ExerciseContent
        exercises={exercises}
        isError={isError}
        isLoading={isLoading}
        pageType="user"
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
      />
    </PageLayout>
  );
};

export default UserExercises;
