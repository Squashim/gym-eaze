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
    _id: '68dbc0a6dc01d019885ec1d7',
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
    _id: '6888da3e88ab14770092a2ec',
    content:
      'Dzięki wielkie Monika! Cieszymy się, że jesteś z nami i że możemy wspierać Cię na Twojej drodze do lepszej formy. 💪😊',
    author: userIds.admin,
    likes: 1,
    parentType: CommentParentType.comment,
    parentId: '68d6e63561af19c2e1535b88',
  },
  {
    _id: '68dbbbdb1f3de3aa550c22f8',
    content:
      'Zumba? To bardziej skomplikowane niż synteza metylamfetaminy. Ale skoro spala 500 kcal... to brzmi jak czysta efektywność.',
    author: userIds.walterWhite,
    likes: 0,
    repliesCount: 2,
    parentType: CommentParentType.post,
    parentId: '68d0313a0515b24d71e60cc2',
  },
  {
    _id: '68dbbc4b52e634ea0d7d25d4',
    content:
      'Yo, Mr. White! Zumba to jak... cardio science 💃🔥 Jeszcze dorzuć mi bachaty i mam full trening.',
    author: userIds.jessiePinkman,
    likes: 0,
    parentType: CommentParentType.comment,
    parentId: '68dbbbdb1f3de3aa550c22f8',
  },
  {
    _id: '68dbbd403d7cf6cf880bda09',
    content:
      'Jeśli tańczysz, to upewnij się, że ktoś nagrywa. Materiał dowodowy zawsze może się przydać... zwłaszcza w sądzie roztańczonych przestępstw. 😉',
    author: userIds.saulGoodman,
    likes: 0,
    parentType: CommentParentType.post,
    parentId: '68d0313a0515b24d71e60cc2',
  },
  {
    _id: '68dbbd804e621457b3bd63d1',
    content: 'Zumba uczy dyscypliny. A dyscyplina to fundament... zarówno biznesu, jak i tańca.',
    author: userIds.gusFring,
    likes: 0,
    parentType: CommentParentType.comment,
    parentId: '68dbbbdb1f3de3aa550c22f8',
  },
  {
    _id: '68dbbe65d8958e232ea5cb5d',
    content: '35 km w 1h 25min… czyli średnia prędkość 24,7 km/h. Imponujące. 🧮',
    author: userIds.walterWhite,
    likes: 0,
    parentType: CommentParentType.post,
    parentId: '68d0312a59b14122341d5073',
  },
];
