import cookieParser from 'cookie-parser';
import cors from 'cors';
import express, { Request, Response } from 'express';
import authRoutes from '../src/routes/authRoutes';
// import { swaggerUi, swaggerSpec } from '../src/config/swagger';
import adminRoutes from './routes/adminRoutes';
import commentRoutes from './routes/commentRoutes';
import equipmentRoutes from './routes/equipmentRoutes';
import exerciseRoutes from './routes/exerciseRoutes';
import muscleGroupRoutes from './routes/muscleGroupRoutes';
import notificationRoutes from './routes/notificationRoutes';
import postsRoutes from './routes/postsRoutes';
import reportRoutes from './routes/reportRoutes';
import trainingsRoutes from './routes/trainingsRoutes';
import userRoutes from './routes/userRoutes';
import userTrainingRoutes from './routes/userTrainingRoutes';

const app = express();

const allowedOrigins = process.env.ALLOWED_ORIGINS ? process.env.ALLOWED_ORIGINS.split(',') : [];

app.use(cookieParser());
app.use(
  cors({
    origin: allowedOrigins,
    credentials: true,
  }),
);

app.use(express.json());
app.use('/api/auth', authRoutes);
app.use('/api/exercises', exerciseRoutes);
app.use('/api/equipment', equipmentRoutes);
app.use('/api/muscleGroup', muscleGroupRoutes);
app.use('/api/user', userRoutes);
app.use('/api/training-plans', trainingsRoutes);
app.use('/api/posts', postsRoutes);
app.use('/api/comments', commentRoutes);
app.use('/api/reports', reportRoutes);
app.use('/api/started-training-plans', userTrainingRoutes);
app.use('/api/notifications', notificationRoutes);
app.use('/api/admin', adminRoutes);

app.get('/', (req: Request, res: Response) => {
  res.send('API działa 🚀');
});

// app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

export default app;
