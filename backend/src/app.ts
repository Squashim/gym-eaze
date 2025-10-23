import express, { Request, Response } from 'express';
import cors from 'cors';
import cookieParser from 'cookie-parser';
import authRoutes from '../src/routes/authRoutes';
import { swaggerUi, swaggerSpec } from '../src/config/swagger';
import equipmentRoutes from './routes/equipmentRoutes';
import exerciseRoutes from './routes/exerciseRoutes';
import muscleGroupRoutes from './routes/muscleGroupRoutes';
import userRoutes from './routes/userRoutes';
import trainingsRoutes from './routes/trainingsRoutes';
import postsRoutes from './routes/postsRoutes';
import commentRoutes from './routes/commentRoutes';
import reportRoutes from './routes/reportRoutes';
import userTrainingRoutes from './routes/userTrainingRoutes';
import notificationRoutes from './routes/notificationRoutes';
import adminRoutes from './routes/adminRoutes';

const app = express();

app.use(cookieParser());
app.use(
  cors({
    origin: 'http://localhost:5173',
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

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

export default app;
