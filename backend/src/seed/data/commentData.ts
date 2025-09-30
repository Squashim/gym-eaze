import { CommentParentType } from '../../constants/commentType';
import Comment from '../../models/commentModel';
import { exercisesIds } from './exerciseData';
import { userIds } from './userData';
export const seedComments = async () => {
  await Comment.deleteMany();
  await Comment.insertMany(comments);
  console.log('Komentarze dodane');
};

const comments = [
  {
    _id: '6888da1a43fc6c0b89e26606',
    content: 'Świetne ćwiczenie!',
    author: userIds.monikaGym,
    likes: 24,
    parentType: CommentParentType.exercise,
    parentId: exercisesIds.barbellCurl,
    repliesCount: 1,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '6888da3e88ab14770092a2ec',
    content: 'Zgadzam się z Moniką!',
    author: userIds.admin,
    parentType: CommentParentType.comment,
    parentId: '6888da1a43fc6c0b89e26606',
  },
  {
    _id: '68d6e63561af19c2e1535b88',
    content:
      'Też się z tego bardzo cieszę! Świetna strona polecam wszystkim. Dzięki niej udało mi się osiągnąć moje cele treningowe i poprawić formę. 🤩',
    author: userIds.monikaGym,
    likes: 4,
    parentType: CommentParentType.post,
    parentId: '68d0308f5a016ed4aef6267a',
    repliesCount: 1,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68dadbd433172047abc8636e',
    content:
      'Dzięki wielkie Monika! Cieszymy się, że jesteś z nami i że możemy wspierać Cię na Twojej drodze do lepszej formy. 💪😊',
    author: userIds.admin,
    likes: 1,
    parentType: CommentParentType.comment,
    parentId: '68d6e63561af19c2e1535b88',
  },
];
