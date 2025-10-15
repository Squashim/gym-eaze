import PageLayout from '@/components/layout/page';
import usePageTitle from '@/hooks/utils/use-page-title';
import usePaginationQuery from '@/hooks/filters-pagination/use-pagination-query';
import WorkoutContent from '@/components/features/workouts/workout-content';
import useGetUserWorkouts from '@/hooks/query/workouts/use-get-user-workouts';
import { useUser } from '@/context/user';

const UserWorkouts = () => {
  const { params, setPage, setPageSize } = usePaginationQuery();
  const { user } = useUser();
  const { data: workouts, isLoading, isError } = useGetUserWorkouts(user!._id, params);
  usePageTitle('Moje plany treningowe');

  return (
    <PageLayout
      description="Tutaj znajdziesz wszystkie plany, które sam stworzyłeś. Możesz je przeglądać, edytować i zarządzać nimi według własnych potrzeb."
      title="Moje plany treningowe"
    >
      <WorkoutContent
        isError={isError}
        isLoading={isLoading}
        pageType="user"
        pagination={{
          params,
          setPage,
          setPageSize,
        }}
        workouts={workouts}
      />
    </PageLayout>
  );
};

export default UserWorkouts;
