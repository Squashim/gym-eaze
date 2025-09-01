import mongoose from "mongoose";

export interface IEquipment extends mongoose.Document {
  name: string;
  image: string;
  imageCredit: string;
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
  }
});

const Equipment = mongoose.model<IEquipment>("Equipment", equipmentSchema);

export default Equipment;
