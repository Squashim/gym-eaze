import { useUserProfile } from '@/hooks/query/user/use-user-profile';
import { zodResolver } from '@hookform/resolvers/zod';
import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useForm, useWatch } from 'react-hook-form';
import { TabButtons, TabContent } from '@/components/ui/reusable-tabs';
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import Typography from '@/components/ui/typography';
import { Goal } from '@/constants/enums/goals';
import useGetProfileDetails from '@/hooks/query/user/use-get-profile-details';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Image, Info, Loader2 } from 'lucide-react';
import { Separator } from '@/components/ui/separator';
import { IsPublicBadge } from '@/components/ui/item-info-badges';
import useGetBadges from '@/hooks/query/user/use-get-badges';
import type { BadgeApiResponse } from '@/types/badge';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import useOpen from '@/hooks/utils/use-open';
import { cn, extractDirtyFieldsData, getApiErrorMsg } from '@/lib/utils';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Checkbox } from '@/components/ui/checkbox';
import type { UserProfileDetailsPayload } from '@/types/user';
import { profileDetailsDefaultValues } from '@/constants/defaults/forms';
import { USER_PROFILE } from '@/constants/user';
import {
  profileDetailsSchema,
  type ProfileDetailsSchema,
} from '@/schemas/user/profile-details-schema';

function ProfileDetailsTab() {
  const badgesDialog = useOpen();
  const form = useForm<ProfileDetailsSchema>({
    resolver: zodResolver(profileDetailsSchema),
    defaultValues: profileDetailsDefaultValues,
  });
  const { data: profileDetails, isLoading, isError, error } = useGetProfileDetails();
  const {
    data: allBadges,
    isLoading: allBadgesLoading,
    isError: isAllBadgesError,
    error: allBadgesError,
  } = useGetBadges();
  const { updateProfileDetails } = useUserProfile();

  const pinnedBadgesIds = useWatch({
    control: form.control,
    name: 'pinnedBadges',
    defaultValue: [],
  });

  const profilePictureRef = useRef<HTMLInputElement>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  const avatarSrc = useMemo(() => {
    if (previewUrl) return previewUrl;
    return profileDetails?.profilePicture || undefined;
  }, [previewUrl, profileDetails]);

  const displayedPinnedBadges = useMemo(() => {
    if (!allBadges || !pinnedBadgesIds || pinnedBadgesIds.length === 0) return [];
    return allBadges.filter((badge) => pinnedBadgesIds.includes(badge.id));
  }, [allBadges, pinnedBadgesIds]);

  useEffect(() => {
    if (profileDetails) {
      form.reset({
        profilePicture: undefined,
        description: profileDetails.description ?? '',
        goal: profileDetails.goal,
        pinnedBadges: profileDetails?.pinnedBadges?.map((badge) => badge.id) ?? [],
      });
    }
  }, [profileDetails, form]);

  useEffect(() => {
    return () => {
      if (previewUrl) {
        URL.revokeObjectURL(previewUrl);
      }
    };
  }, [previewUrl]);

  const onSubmit = (formData: ProfileDetailsSchema) => {
    const dirtyFields = form.formState.dirtyFields;

    const payload = extractDirtyFieldsData<ProfileDetailsSchema, UserProfileDetailsPayload>(
      dirtyFields,
      formData,
    );

    updateProfileDetails.mutate(payload, {
      onSuccess: () => {
        if (previewUrl) {
          URL.revokeObjectURL(previewUrl);
          setPreviewUrl(null);
        }
      },
    });
  };

  const handleFormReset = useCallback(() => {
    form.reset();
    if (previewUrl) {
      URL.revokeObjectURL(previewUrl);
      setPreviewUrl(null);
    }
  }, [form, previewUrl]);

  const handleRemoveFile = useCallback(() => {
    form.setValue('profilePicture', undefined, { shouldDirty: true, shouldValidate: true });
    if (profilePictureRef.current) profilePictureRef.current.value = '';

    if (previewUrl) {
      URL.revokeObjectURL(previewUrl);
      setPreviewUrl(null);
    }
  }, [form, previewUrl]);

  const handleSetPinnedBadges = useCallback(
    (ids: string[]) => {
      form.setValue('pinnedBadges', ids, { shouldDirty: true, shouldValidate: true });
    },
    [form],
  );

  return (
    <section className="grid w-full grid-cols-1 gap-4 max-w-lg mx-auto md:max-w-4xl">
      <TabContent
        description="Zaktualizuj informacje wyświetlane na Twoim profilu. Pamiętaj, że możesz zmienić te ustawienia w dowolnym momencie. Jeśli nie chcesz, aby te informacje były publicznie widoczne, możesz dostosować ustawienia prywatności."
        error={error}
        isError={isError}
        isLoading={isLoading}
        title="Edytuj informacje o profilu"
      >
        <Form {...form}>
          <form
            className="grid grid-cols-1 md:grid-cols-[1fr_0.1fr_1fr] gap-4"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <div className="space-y-4">
              <FormField
                control={form.control}
                name="profilePicture"
                render={({ field }) => (
                  <FormItem>
                    <div className="flex items-center gap-2 justify-between">
                      <FormLabel className="font-semibold" htmlFor="image-input">
                        Zdjęcie profilowe
                      </FormLabel>
                      <IsPublicBadge isLoading={false} isPublic={true} />
                    </div>
                    <FormControl>
                      <div className="flex items-center gap-4">
                        <Avatar className="size-16">
                          <AvatarImage alt="Zdjęcie profilowe" src={avatarSrc} />
                          <AvatarFallback className="border text-muted-foreground">
                            <Image />
                          </AvatarFallback>
                        </Avatar>

                        <div className="flex items-center gap-2 w-full flex-wrap">
                          <Button
                            className="flex-1"
                            datatype="file"
                            onClick={() => profilePictureRef.current?.click()}
                            type="button"
                            variant="outline"
                          >
                            {avatarSrc ? 'Zmień zdjęcie' : 'Dodaj zdjęcie'}
                          </Button>
                          {previewUrl && (
                            <Button onClick={handleRemoveFile} type="button" variant="ghost">
                              Usuń zdjęcie
                            </Button>
                          )}
                        </div>
                        <input
                          {...field}
                          accept="image/jpeg, image/webp, image/png"
                          className="hidden"
                          id="image-input"
                          onChange={(e) => {
                            const file = e.target.files?.[0];
                            field.onChange(file);

                            if (file) {
                              const url = URL.createObjectURL(file);
                              setPreviewUrl(url);
                            }
                          }}
                          ref={profilePictureRef}
                          type="file"
                          value=""
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                    <FormDescription>
                      Maksymalny rozmiar pliku to 5MB. Obsługiwane formaty to: .jpg, .png, .webp
                    </FormDescription>
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="font-semibold">Biogram</FormLabel>
                    <FormControl>
                      <Textarea
                        {...field}
                        autoComplete="off"
                        autoFocus
                        className="resize-none dark:bg-card bg-card min-h-24 max-h-48 scrollbar overflow-y-auto break-words"
                        maxLength={USER_PROFILE.BIO_MAX_LENGTH}
                        placeholder="Napisz coś o sobie..."
                      />
                    </FormControl>
                    <div className="flex justify-between items-center min-h-5">
                      <FormMessage />
                      <Typography className="text-xs text-muted-foreground ml-auto" variant="small">
                        {field.value?.length ?? 0}/{USER_PROFILE.BIO_MAX_LENGTH}
                      </Typography>
                    </div>
                  </FormItem>
                )}
              />
            </div>

            <Separator className="mx-auto hidden md:block" orientation="vertical" />
            <Separator className="md:hidden" />

            <div className="space-y-4">
              <FormField
                control={form.control}
                name="goal"
                render={({ field }) => (
                  <FormItem>
                    <div className="flex items-center gap-2 justify-between">
                      <FormLabel className="font-semibold" htmlFor={field.name}>
                        Cel treningowy
                      </FormLabel>
                      <IsPublicBadge
                        isLoading={isLoading}
                        isPublic={profileDetails?.isGoalPublic}
                      />
                    </div>

                    <FormControl>
                      <Select
                        name={field.name}
                        onValueChange={(value) => value && field.onChange(value)}
                        value={field.value}
                      >
                        <SelectTrigger className="w-full" id={field.name}>
                          <SelectValue placeholder="Wybierz cel treningowy..." />
                        </SelectTrigger>
                        <SelectContent>
                          {Object.entries(Goal).map(([key, value]) => (
                            <SelectItem key={key} value={value}>
                              {value}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </FormControl>
                  </FormItem>
                )}
              />

              <Separator />

              <div className="space-y-4 w-full">
                <div className="flex items-center gap-2 justify-between">
                  <Typography
                    className="text-sm leading-none font-semibold select-none text-card-foreground my-0"
                    variant="lead"
                  >
                    Przypięte odznaki ({displayedPinnedBadges?.length ?? 0}/
                    {USER_PROFILE.MAX_PINNED_BADGES})
                  </Typography>
                  <IsPublicBadge
                    isLoading={isLoading}
                    isPublic={profileDetails?.isBadgesDetailsPublic}
                  />
                </div>
                {displayedPinnedBadges.length === 0 ? (
                  <Alert>
                    <Info />
                    <AlertTitle className="line-clamp-2">
                      Nie masz przypiętych żadnych odznak!
                    </AlertTitle>
                    <AlertDescription>
                      Przypnij odznaki, które chcesz wyróżnić na swoim profilu. Możesz przypiąć
                      maksymalnie {USER_PROFILE.MAX_PINNED_BADGES} odznak.
                    </AlertDescription>
                  </Alert>
                ) : (
                  <div className="flex flex-col gap-2 max-h-[150px] overflow-y-auto scrollbar">
                    {displayedPinnedBadges.map((badge) => (
                      <BadgeItem badge={badge} key={badge.id} />
                    ))}
                  </div>
                )}

                <Button
                  className="w-full"
                  disabled={allBadgesLoading}
                  onClick={badgesDialog.open}
                  type="button"
                  variant="outline"
                >
                  {allBadgesLoading ? (
                    <>
                      <Loader2 className="animate-spin" />
                      <span>Ładowanie odznak...</span>
                    </>
                  ) : (
                    <span>Zarządzaj odznakami</span>
                  )}
                </Button>
              </div>
            </div>

            <TabButtons
              disabled={!form.formState.isDirty}
              isLoading={updateProfileDetails.isPending}
              onReset={handleFormReset}
            />
          </form>

          <BadgesDialog
            allBadges={allBadges ?? []}
            error={allBadgesError}
            isError={isAllBadgesError}
            onClose={badgesDialog.close}
            onOpenChange={badgesDialog.setIsOpen}
            open={badgesDialog.isOpen}
            pinnedBadgesIds={pinnedBadgesIds ?? []}
            setPinnedBadges={handleSetPinnedBadges}
          />
        </Form>
      </TabContent>
    </section>
  );
}

interface BadgesDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onClose: () => void;
  allBadges: BadgeApiResponse[];
  isError: boolean;
  error: Error | null;
  pinnedBadgesIds: string[];
  setPinnedBadges: (ids: string[]) => void;
}

const BadgesDialog = ({
  open,
  onOpenChange,
  onClose,
  allBadges,
  isError,
  error,
  setPinnedBadges,
  pinnedBadgesIds,
}: BadgesDialogProps) => {
  const isPinned = useCallback(
    (badgeId: string) => pinnedBadgesIds.includes(badgeId),
    [pinnedBadgesIds],
  );

  const handleToggle = useCallback(
    (badgeId: string) => {
      const current = pinnedBadgesIds ?? [];
      const isAlreadyPinned = current.includes(badgeId);

      let updated: string[];
      if (isAlreadyPinned) {
        updated = current.filter((id) => id !== badgeId);
      } else {
        if (current.length >= USER_PROFILE.MAX_PINNED_BADGES) return;
        updated = [...current, badgeId];
      }
      setPinnedBadges(updated);
    },
    [pinnedBadgesIds, setPinnedBadges],
  );

  return (
    <Dialog onOpenChange={onOpenChange} open={open}>
      <DialogContent className="[&>button]:hidden max-h-[80vh] overflow-hidden flex flex-col">
        <DialogHeader className="text-left">
          <DialogTitle>Wybierz odznaki do przypięcia</DialogTitle>
          <DialogDescription>
            Możesz przypiąć do {USER_PROFILE.MAX_PINNED_BADGES} odznak, które będą widoczne na Twoim
            profilu.
          </DialogDescription>
        </DialogHeader>
        <div className="flex-1 overflow-y-auto scrollbar">
          {isError ? (
            <div className="flex items-center justify-center h-48">
              <Typography className="text-center" variant="h4">
                {getApiErrorMsg(error) || 'Wystąpił nieoczekiwany błąd. Spróbuj ponownie później.'}
              </Typography>
            </div>
          ) : !allBadges || allBadges.length === 0 ? (
            <div className="flex items-center justify-center">
              <Alert>
                <Info />
                <AlertTitle className="line-clamp-2">Nie masz jeszcze żadnej odznaki!</AlertTitle>
                <AlertDescription>
                  Zdobywaj odznaki uczestnicząc w treningach, tworząc plany, ćwiczenia i udzielając
                  się społecznościowo.
                </AlertDescription>
              </Alert>
            </div>
          ) : (
            <div className="space-y-2 p-0.5">
              {allBadges.map((badge) => (
                <BadgeItem
                  badge={badge}
                  disabled={
                    !isPinned(badge.id) && pinnedBadgesIds.length >= USER_PROFILE.MAX_PINNED_BADGES
                  }
                  isPinned={isPinned(badge.id)}
                  key={badge.id}
                  onToggle={() => handleToggle(badge.id)}
                />
              ))}
            </div>
          )}
        </div>

        <div className="flex justify-end">
          <Button onClick={onClose} type="button" variant="outline">
            Zamknij
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

interface BadgeItemProps {
  badge: BadgeApiResponse;
  onToggle?: () => void;
  isPinned?: boolean;
  disabled?: boolean;
}

function BadgeItem({ badge, onToggle, isPinned, disabled }: BadgeItemProps) {
  const handleClick = () => {
    if (onToggle && !(disabled && !isPinned)) {
      onToggle();
    }
  };

  return (
    <div
      aria-pressed={onToggle ? isPinned : undefined}
      className={cn(
        'rounded-md p-3 shadow-md flex items-center gap-2 border bg-card text-card-foreground relative transition-all',
        onToggle && 'cursor-pointer hover:border-primary/70',
        isPinned && 'border-secondary hover:border-secondary/70',
        disabled && !isPinned && 'opacity-50 pointer-events-none',
      )}
      onClick={handleClick}
      onKeyDown={(e) => {
        if (onToggle && (e.key === 'Enter' || e.key === ' ')) {
          e.preventDefault();
          handleClick();
        }
      }}
      role={onToggle ? 'button' : undefined}
      tabIndex={onToggle && !(disabled && !isPinned) ? 0 : -1}
    >
      <div className="flex-shrink-0 overflow-hidden h-full aspect-square max-h-16">
        <img
          alt={`zdjęcie odznaki ${badge.title}`}
          className="w-full h-full object-cover rounded-md bg-primary/5 dark:grayscale-[0.2] dark:contrast-110"
          src={badge.image}
        />
      </div>

      <div className="flex flex-col items-start justify-between flex-1 min-w-0">
        <Typography className="text-sm font-semibold">{badge.title}</Typography>
        <Typography className="text-sm text-balance" variant="muted">
          {badge.description}
        </Typography>
      </div>

      {onToggle && (
        <Checkbox
          aria-label={isPinned ? 'Odepnij odznakę' : 'Przypnij odznakę'}
          checked={isPinned}
          className="ml-auto pointer-events-none dark:data-[state=checked]:bg-secondary data-[state=checked]:bg-primary data-[state=checked]:border-secondary data-[state=checked]:text-secondary-foreground"
          disabled={disabled && !isPinned}
          tabIndex={-1}
        />
      )}
    </div>
  );
}

export default ProfileDetailsTab;
