import mongoose from "mongoose";

export interface IEquipment extends mongoose.Document {
  name: string;
  image: string;
  imageCredit: string;
  description?: string;
}

const equipmentSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  image: {
    type: String,
    required: true,
  },
  imageCredit: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    maxLength: 500,
  }
});

const Equipment = mongoose.model<IEquipment>("Equipment", equipmentSchema);

export default Equipment;