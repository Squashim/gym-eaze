import { UTILS as endpoints } from '@/constants/endpoints';
import API from './api-client';
import type { Equipment as EquipmentApiResponse } from '@/types/equipment';
import type { MuscleGroup as MuscleGroupApiResponse } from '@/types/muscle-group';

const getEquipment = async (): Promise<EquipmentApiResponse[]> => {
  const { data } = await API.get<EquipmentApiResponse[]>(endpoints.equipment);
  return data;
};

const getMuscleGroups = async (): Promise<MuscleGroupApiResponse[]> => {
  const { data } = await API.get<MuscleGroupApiResponse[]>(endpoints.muscleGroups);
  return data;
};

export { getEquipment, getMuscleGroups };
