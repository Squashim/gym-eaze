import { Skeleton } from './skeleton';

const ItemsSkeleton = () => {
  return (
    <div className="flex flex-col w-full gap-4">
      <main className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 place-items-center">
        <Skeleton className="col-span-full h-7 w-1/2 sm:w-1/3 lg:w-1/6 justify-self-start" />
        {Array.from({ length: 9 }).map((_, idx) => (
          <Skeleton className="h-105 w-full max-w-md rounded-xl" key={idx} />
        ))}
      </main>
      <div className="flex justify-end">
        <Skeleton className="h-10 w-1/6" />
      </div>
      <Skeleton className="h-10 w-1/3 mx-auto" />
    </div>
  );
};

export default ItemsSkeleton;
