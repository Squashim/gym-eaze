import Badge from '../../models/badgeModel';
import { ObjectId } from 'mongodb';

export const seedBadges = async () => {
  await Badge.deleteMany();
  await Badge.insertMany(badges);
  console.log('Odznaki dodane');
};

export const badgesIds = {
  time: {
    oneYear: new ObjectId('6887b46dcb7bedee43b662f9'),
    twoYears: new ObjectId('6887b47a18421c5f0be235c9'),
    threeYears: new ObjectId('6887b4855f01680061761f3a'),
    fourYears: new ObjectId('6887b49a91d45c094c4d468d'),
    fiveYears: new ObjectId('6887b4a86ded2a8bbf369396'),
  },
  social: {
    tenFollowers: new ObjectId('6887b6111f7d7d53800b2f86'),
    fiftyFollowers: new ObjectId('6887b61df9e7b4515c87c154'),
    hundredFollowers: new ObjectId('6887b62a0b6d756908a8e1b4'),
  },
  workouts: {
    first: new ObjectId('6887b63aa3234565c7b5392b'),
    five: new ObjectId('6887b6ecf2d6034b71bbafa3'),
    ten: new ObjectId('6887b6f8937aac1fa5612588'),
    twenty: new ObjectId('6887b7020dc9f33dff5748a7'),
  },
  exercises: {
    first: new ObjectId('6887b71977991eea1a365c96'),
    five: new ObjectId('6887b72056848532eef4a185'),
    ten: new ObjectId('6887c060b56cf2f6f1fd3d11'),
    twenty: new ObjectId('6887b72c85fbc0928cd99183'),
  },
  posts: {
    first: new ObjectId('6887b73a2078947c7f327f80'),
    five: new ObjectId('6887b74635b1040012e6da44'),
    ten: new ObjectId('6887b751f744ffa000f0a21a'),
    twenty: new ObjectId('6887b75f905b84316aa72975'),
  },
};

const badges = [
  {
    _id: badgesIds.time.oneYear,
    title: 'Rok z nami',
    description: 'Za pierwszy rok aktywności. Trzymasz formę!',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762193118/year_1_vyap8a.png',
    requirements: {
      action: 'AccountAge',
      target: 1,
    },
  },
  {
    _id: badgesIds.time.twoYears,
    title: '2 lata wytrwałości',
    description: 'Dwa lata wytrwałości - imponujące',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762193217/year_2_d7vxhu.png',
    requirements: {
      action: 'AccountAge',
      target: 2,
    },
  },
  {
    _id: badgesIds.time.threeYears,
    title: '3 lata treningu',
    description: 'Trzy lata progresu i rozwoju. Brawo!',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762193291/year_3_m3qpwq.png',
    requirements: {
      action: 'AccountAge',
      target: 3,
    },
  },
  {
    _id: badgesIds.time.fourYears,
    title: '4-letni weteran',
    description: 'Cztery lata z GymEaze - przykład dla innych',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762193297/year_4_jq42ra.png',
    requirements: {
      action: 'AccountAge',
      target: 4,
    },
  },
  {
    _id: badgesIds.time.fiveYears,
    title: '5 lat konsekwencji',
    description: 'Pół dekady zaangażowania. Jesteś legendą',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762193306/year_5_hgzp2k.png',
    requirements: {
      action: 'AccountAge',
      target: 5,
    },
  },

  //======================= ODZNAKI ZA POPULARNOŚĆ =====================
  {
    _id: badgesIds.social.tenFollowers,
    title: 'Pierwsza dziesiątka - 10 motywatorów',
    description: 'Gratulacje! Masz już pierwszych fanów',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762193880/followers_10_scbqpv.png',
    requirements: {
      action: 'Followers',
      target: 10,
    },
  },
  {
    _id: badgesIds.social.fiftyFollowers,
    title: 'Pół setki mocy - 50 motywatorów',
    description: 'Jesteś inspiracją - coraz więcej osób Cię śledzi',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762193926/followers_50_ti8flo.png',
    requirements: {
      action: 'Followers',
      target: 50,
    },
  },
  {
    _id: badgesIds.social.hundredFollowers,
    title: 'Fitness influencer - 100 motywatorów',
    description: 'Setka za Tobą! Twoja forma robi wrażenie',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762194000/followers_100_hrhzzs.png',
    requirements: {
      action: 'Followers',
      target: 100,
    },
  },
  //======================= ODZNAKI ZA STWORZONE TRENINGI =====================
  {
    _id: badgesIds.workouts.first,
    title: 'Trener w akcji - 1 utworzony trening',
    description: 'Twój pierwszy trening jest już utworzony. Świetna robota',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762273305/tr_1_kmklc1.png',
    requirements: {
      action: 'Trainings',
      target: 1,
    },
  },
  {
    _id: badgesIds.workouts.five,
    title: 'Twórca formy - 5 utworzonych treningów',
    description: 'Pomagasz innym w drodze do lepszej formy',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762273311/tr_2_arw7n5.png',
    requirements: {
      action: 'Trainings',
      target: 5,
    },
  },
  {
    _id: badgesIds.workouts.ten,
    title: 'Mentor społeczności - 10 utworzonych treningów',
    description: 'Twoje plany inspirują innych. Jesteś prawdziwym liderem',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762273314/tr_3_aysset.png',
    requirements: {
      action: 'Trainings',
      target: 10,
    },
  },
  {
    _id: badgesIds.workouts.twenty,
    title: 'Architekt siły - 20 utworzonych treningów',
    description: 'Budujesz społeczność treningową cegła po cegle',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762273315/tr_4_xvga6m.png',
    requirements: {
      action: 'Trainings',
      target: 20,
    },
  },
  //======================= ODZNAKI ZA STWORZONE ĆWICZENIA =====================
  {
    _id: badgesIds.exercises.first,
    title: 'Kreator ruchu - 1 utworzone ćwiczenie',
    description: 'Twoje pierwsze autorskie ćwiczenie jest już gotowe',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762195090/training_plan_1_qdx3nv.png',
    requirements: {
      action: 'Exercises',
      target: 1,
    },
  },
  {
    _id: badgesIds.exercises.five,
    title: 'Pomysłowy trener - 5 utworzonych ćwiczeń',
    description: 'Pomagasz innym w drodze do lepszej formy',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762195093/training_plan_2_m9calg.png',
    requirements: {
      action: 'Exercises',
      target: 5,
    },
  },
  {
    _id: badgesIds.exercises.ten,
    title: 'Ekspert formy - 10 utworzonych ćwiczeń',
    description: 'Twoja baza ćwiczeń robi się naprawdę solidna',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762195097/training_plan_3_qkp7w5.png',
    requirements: {
      action: 'Exercises',
      target: 10,
    },
  },
  {
    _id: badgesIds.exercises.twenty,
    title: 'Mistrz tworzenia ćwiczeń - 20 utworzonych ćwiczeń',
    description: 'Komponujesz ćwiczenia jak zawodowiec - czapki z głów',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762195096/training_plan_4_ws9gvo.png',
    requirements: {
      action: 'Exercises',
      target: 20,
    },
  },
  //======================= ODZNAKI ZA STWORZONE POSTY =====================
  {
    _id: badgesIds.posts.first,
    title: 'Pierwszy głos - 1 utworzony post',
    description: 'Zabierasz głos w społeczności - świetny początek!',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762269815/post_2_h1etve.png',
    requirements: {
      action: 'Posts',
      target: 1,
    },
  },
  {
    _id: badgesIds.posts.five,
    title: 'Aktywny motywator - 5 utworzonych postów',
    description: 'Twoje posty dodają innym energii i inspiracji.',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762269889/post_5_cvmgxi.png',
    requirements: {
      action: 'Posts',
      target: 5,
    },
  },
  {
    _id: badgesIds.posts.ten,
    title: 'Głos siłowni - 10 utworzonych postów',
    description: 'Twoje treści budują atmosferę i wspierają społeczność.',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762269812/post_4_ncfgpz.png',
    requirements: {
      action: 'Posts',
      target: 10,
    },
  },
  {
    _id: badgesIds.posts.twenty,
    title: 'Lider dyskusji - 20 utworzonych postów',
    description: 'Twoje posty wyznaczają rytm całej społeczności.',
    image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1762269817/post_heqqbd.png',
    requirements: {
      action: 'Posts',
      target: 20,
    },
  },
];
