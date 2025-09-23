import { Button } from '@/components/ui/button';
import {
  Sheet,
  SheetClose,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet';

import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { X } from 'lucide-react';
import { useState } from 'react';
import SocialExercisesTab from './tabs/social';
import UserExercisesTab from './tabs/user';
import FavoriteExercisesTab from './tabs/favorite';

interface ExercisesSheetProps {
  isOpen: boolean;
  isExerciseAdded: (id: string) => boolean;
  setIsOpen: (open: boolean) => void;
  onExerciseToggle: (id: string) => void;
}

function ExercisesSheet({
  isOpen,
  setIsOpen,
  onExerciseToggle,
  isExerciseAdded,
}: ExercisesSheetProps) {
  const [activeTab, setActiveTab] = useState('social');

  return (
    <Sheet onOpenChange={(open) => setIsOpen(open)} open={isOpen}>
      <SheetContent className="[&>button:first-of-type]:hidden sm:max-w-2xl scrollbar max-h-dvh overflow-y-auto h-screen">
        <SheetHeader className="pb-0">
          <SheetTitle className="scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight first:mt-0">
            Dodaj ćwiczenia
          </SheetTitle>
          <SheetClose asChild>
            <Button className="absolute right-4 top-4" size="icon" variant="outline">
              <X className="size-5" />
            </Button>
          </SheetClose>
          <SheetDescription className="text-accent-foreground/80">
            Wybierz zakładkę, aby przeglądać dostępne ćwiczenia. Kliknij na ćwiczenie, aby dodać je
            do treningu.
          </SheetDescription>
          <div className="flex flex-col gap-4 mt-2">
            <Tabs onValueChange={setActiveTab} value={activeTab}>
              <div className="relative overflow-x-auto h-12 scrollbar">
                <TabsList className="w-full gap-4">
                  <TabsTrigger className="h-8 px-4" value="social">
                    Społecznościowe
                  </TabsTrigger>
                  <TabsTrigger className="h-8 px-4" value="own">
                    Moje
                  </TabsTrigger>
                  <TabsTrigger className="h-8 px-4" value="favorite">
                    Ulubione
                  </TabsTrigger>
                </TabsList>
              </div>
              <TabsContent value="social">
                <SocialExercisesTab
                  isExerciseAdded={isExerciseAdded}
                  onExerciseToggle={onExerciseToggle}
                />
              </TabsContent>
              <TabsContent value="own">
                <UserExercisesTab
                  isExerciseAdded={isExerciseAdded}
                  onExerciseToggle={onExerciseToggle}
                />
              </TabsContent>
              <TabsContent value="favorite">
                <FavoriteExercisesTab
                  isExerciseAdded={isExerciseAdded}
                  onExerciseToggle={onExerciseToggle}
                />
              </TabsContent>
            </Tabs>
          </div>
        </SheetHeader>
      </SheetContent>
    </Sheet>
  );
}

export default ExercisesSheet;
