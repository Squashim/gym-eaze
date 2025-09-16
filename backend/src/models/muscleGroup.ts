import mongoose from 'mongoose';

export interface IMuscleGroup extends mongoose.Document {
  name: string;
  image: string;
}

const muscleGroupSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
  },
  image: {
    type: String,
    required: true,
  },
});

const MuscleGroup = mongoose.model<IMuscleGroup>('MuscleGroup', muscleGroupSchema);

export default MuscleGroup;
