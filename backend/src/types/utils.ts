import mongoose from 'mongoose';

export type LeanDocument<T> = Omit<T, keyof mongoose.Document>;
