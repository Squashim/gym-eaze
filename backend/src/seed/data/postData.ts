import Post from '../../models/postModel';

export const seedPosts = async () => {
  await Post.deleteMany();
  await Post.insertMany(posts);
  console.log('Posty dodane');
};

const posts = [
  {
    _id: '68d03088cef6312f5db20e6c',
    author: '68aadfbccc15cf46f25867ad', // admin
    content:
      'Cześć! To Twój pierwszy krok w stronę lepszej formy. GymEaze to społeczność, dzięki której osiągniesz swoje cele i poczujesz radość z każdego treningu. 💪. Motywujemy się nawzajem, dzielimy poradami i wspieramy w trudniejszych chwilach. 🚀. W GymEaze chodzi o to, żeby każdy czuł, że nie trenuje sam. ',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1758393730/admin_post_viiswk.png',
    likes: 182,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d0308f5a016ed4aef6267a',
    author: '68aadfbccc15cf46f25867ad', // admin
    content:
      'Wow! 😃💪 Nie mogę uwierzyć, jak szybko rośnie nasza społeczność GymEaze! Każdy nowy użytkownik, każdy dodany trening i każda motywująca wiadomość sprawiają, że to miejsce staje się coraz bardziej inspirujące. 🌟 To dzięki Wam GymEaze staje się przestrzenią, gdzie ludzie naprawdę wspierają się w osiąganiu celów. Widząc Wasze postępy i zaangażowanie, wiem, że razem możemy osiągnąć naprawdę wiele! 🚀 Dziękuję, że jesteście częścią tej społeczności. Nie przestawajcie dzielić się swoimi sukcesami i motywować innych - razem rośniemy silniejsi! 💥',
    hashtags: ['społeczność', 'motywacja', 'sukces'],
    likes: 77,
    commentsCount: 1,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d030986d15791d3290871e',
    author: '68aadfbccc15cf46f25867ad', // admin
    content:
      '🔥 Pochwalcie się dzisiaj! 🔥 Ile kalorii udało Wam się dziś spalić? Napiszcie swoje wyniki w komentarzu i zobaczmy, kto dzisiaj króluje na siłowni! 💪🥵',
    video: 'https://res.cloudinary.com/dtglpgvzi/video/upload/v1758475408/293079_small_kgxoh1.mp4',
    hashtags: ['fitness', 'kalorie', 'trening'],
    likes: 25,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d0309e60b0903569587609',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Dzisiaj na treningu total body udało mi się spalić aż 500 kalorii! 💪🔥 Czuję się niesamowicie zmotywowana i gotowa na kolejne wyzwania. A Wy? Ile kalorii spaliliście podczas swojego treningu? Podzielcie się swoimi wynikami w komentarzach! P.S Wrzucam filmik z treningu!',
    video:
      'https://res.cloudinary.com/dtglpgvzi/video/upload/v1758383040/posts/videos/ce7q6pkusjrnbuwhotmq.mp4',
    hashtags: ['fitness', 'kalorie', 'trening'],
    likes: 5,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d030d509a2f6adc2474914',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Ranek z intensywnym treningiem interwałowym HIIT! 💥 Po 45 minutach na liczniku aż 620 spalonych kalorii. To najlepszy zastrzyk energii na cały dzień. Kto dziś razem ze mną? #HIIT #motywacja',
    hashtags: ['HIIT', 'motywacja', 'trening', 'kalorie', 'energia', 'poranek'],
    likes: 12,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d030e2bd1491f772bf9fde',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Dzisiejszy bieg przełajowy w lesie to czysta przyjemność! 🏃‍♂️🌲 10 km za mną, a Garmin pokazuje 780 kcal. Czy może być coś lepszego niż endorfiny po biegu? Pytanie do biegaczy - jaka wasza ulubiona trasa?',
    hashtags: ['bieganie', 'endorfiny', 'las', 'garmin', 'kardio', 'zdrowie'],
    likes: 24,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d030ec9090a6d1e602fa16',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Session done! ✅ Dziś mocny trening siłowy nóg + 20 minut cardio na finisz. Łącznie ponad 700 kcal poszło w powietrze! 🔥 Czuję, że rosnę! Jakie było wasze największe osiągnięcie tygodnia?',
    hashtags: ['treningnóg', 'silownia', 'cardio', 'progress', 'motivation', 'fitness'],
    likes: 18,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d030f33c6f4a6d84a009af',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Yoga Flow na powitanie słońca ☀️✨ 90 minut głębokiego rozciągania i oddechu. Spalone kalorie? Mniej, bo tylko 280, ale za to nieziemski spokój ducha i energia na resztę dnia. Dla was liczą się kalorie czy może coś innego?',
    hashtags: ['yoga', 'rozciaganie', 'mindfulness', 'spokój', 'energia', 'ranek'],
    likes: 37,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d030f913631e808e44cfef',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Trening obwodowy to mistrzostwo świata! 🏆 5 obwodów, 10 stacji, 45 minut i 550 kcal historii. Konkretny wycisk i mega satysfakcja. Polecam każdemu, kto lubi wyzwania! Kto próbował?',
    hashtags: ['treningobwodowy', 'wycisk', 'satysfakcja', 'wyzwanie', 'fitness', 'motywacja'],
    likes: 15,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d030ff558aa2f9507f71a6',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Pływanie to mój sposób na wszystko! 🏊‍♀️ Godzina w basenie i 600 kcal mniej. Odpręża, relaksuje i świetnie rzeźbi sylwetkę. Jaka jest wasza ulubiona aktywność, która nie feels like a workout?',
    hashtags: ['plywanie', 'basen', 'relaks', 'trening', 'kalorie', 'zdrowie'],
    likes: 29,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d0310602bad324c3e2fc11',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Dzień z crossfit! 🏋️‍♀️ WOD z deadliftami, wall balls i podciąganiem. Czas: 18:45. Kalorie: ok 500. Poziom zmęczenia: maksymalny. Poziom satysfakcji: jeszcze wyższy! CF community, przedstawcie się!',
    hashtags: ['crossfit', 'WOD', 'deadlift', 'wallballs', 'podsciaganie', 'hardwork'],
    likes: 42,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d0311ed82e5501e29f5bc0',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Spacer metaboliczny z obciążeniem! 💥 10 tysięcy kroków z kamizelką 8kg. Efekt: 450 kcal i świetne samopoczucie. Czasami nie trzeba mega intensywności, by zrobić coś dobrego dla ciała. A wy lubicie spacery?',
    hashtags: ['spacer', 'kroki', 'healthylifestyle', 'obciążenie', 'outdoor', 'metabolism'],
    likes: 9,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d0312a59b14122341d5073',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Rowerowa misja ukończona! 🚴‍♀️ 35 km po malowniczych szlakach za mną. Czas: 1h 25min, kalorie: 510. Widoki bezcenne! Rowery górskie, szosówki - co wolicie? Jakie macie plany na aktywny weekend?',
    hashtags: ['rower', 'góry', 'outdoor', 'kolarstwo', 'weekend', 'przyroda'],
    likes: 31,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    _id: '68d0313a0515b24d71e60cc2',
    author: '68d0243f1ef234cc5446b600', // monikaGym
    content:
      'Taneczny szał w rytm latino! 💃🕺 Godzina zumby to zawsze ponad 500 kcal zabawy i uśmiechu. To dowód, że trening nie musi być nudny! Jaka jest wasza ulubiona aktywność, przy której zapominacie, że ćwiczycie?',
    hashtags: ['taniec', 'zumba', 'zabawa', 'radość', 'fitness', 'latinodance'],
    likes: 56,
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];
