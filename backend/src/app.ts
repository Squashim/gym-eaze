import express, { Request, Response } from 'express';
import cors from 'cors';
import cookieParser from 'cookie-parser';
import authRoutes from '../src/routes/authRoutes';
import { swaggerUi, swaggerSpec } from '../src/config/swagger';
import exerciseRoutes from './routes/exerciseRoutes';

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
app.use("/api/exercises", exerciseRoutes);

app.get('/', (req: Request, res: Response) => {
  res.send('API działa 🚀');
});

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

export default app;
