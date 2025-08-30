import Equipment, { IEquipment } from "../models/equipmentModel";


export const getAllEquipment = async (): Promise<IEquipment[]> => {
  return await Equipment.find();
};

export const getEquipmentById = async (id: string): Promise<IEquipment | null> => {
  return await Equipment.findById(id);
};