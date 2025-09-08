import Badge from "../../models/badgeModel";
export const seedBadges = async () => {
  await Badge.deleteMany();
  await Badge.insertMany(badges);
  console.log("Odznaki dodane");
};

const badges = [
  {
    _id: "6887b46dcb7bedee43b662f9",
    title: "Rok z nami",
    description: "Za pierwszy rok aktywności. Trzymasz formę!",
    image: "https://ibb.co/JWcY67dB",
    requirements: {
      action: "Days",
      target: 1,
    },
  },
  {
    _id: "6887b47a18421c5f0be235c9",
    title: "2 lata wytrwałości",
    description: "Dwa lata wytrwałości- imponujące",
    image: "https://ibb.co/n84Myzb5",
    requirements: {
      action: "Days",
      target: 2,
    },
  },
  {
    _id: "6887b4855f01680061761f3a",
    title: "3 lata treningu",
    description: "Trzy lata progresu i rozwoju. Brawo!",
    image: "https://ibb.co/VYvD1rbk",
    requirements: {
      action: "Days",
      target: 3,
    },
  },
  {
    _id: "6887b49a91d45c094c4d468d",
    title: "4-letni weteran",
    description: "Cztery lata z GymEaze- przykład dla innych",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Days",
      target: 4,
    },
  },
  {
    _id: "6887b4a86ded2a8bbf369396",
    title: "5 lat konsekwencji",
    description: "Pół dekady zaangażowania. Jesteś legendą",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Days",
      target: 5,
    },
  },

  //======================= ODZNAKI ZA POPULARNOŚĆ =====================
  {
    _id: "6887b6111f7d7d53800b2f86",
    title: "Pierwsza dziesiątka - 10 motywatorów",
    description: "Gratulacje! Masz już pierwszych fanów",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Followers",
      target: 10,
    },
  },
  {
    _id: "6887b61df9e7b4515c87c154",
    title: "Pół setki mocy - 50 motywatorów",
    description: "Jesteś inspiracją - coraz więcej osób Cię śledzi",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Followers",
      target: 50,
    },
  },
  {
    _id: "6887b62a0b6d756908a8e1b4",
    title: "Fitness influencer - 100 motywatorów",
    description: "Setka za Tobą! Twoja forma robi wrażenie",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Followers",
      target: 100,
    },
  },
  //======================= ODZNAKI ZA STWORZONE TRENINGI =====================
  {
    _id: "6887b63aa3234565c7b5392b",
    title: "Trener w akccji - 1 utworzony trening",
    description: "Twój pierwszy trening jest już utworzony. Świetna robota",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Trainings",
      target: 1,
    },
  },
  {
    _id: "6887b6ecf2d6034b71bbafa3",
    title: "Twórca formy - 5 utworzonych treningów",
    description: "Pomagasz innym w drodze do lepszej formy",
    image: "https://ibb.co/3m1c7NBz",
    requirements: {
      action: "Trainings",
      target: 5,
    },
  },
  {
    _id: "6887b6f8937aac1fa5612588",
    title: "Mentor społeczności - 10 utworzonych treningów",
    description: "Twoje plany inspirują innych. Jesteś prawdziwym liderem",
    image: "https://ibb.co/ZRHGN37F",
    requirements: {
      action: "Trainings",
      target: 10,
    },
  },
  {
    _id: "6887b7020dc9f33dff5748a7",
    title: "Architekt siły - 20 utworzonych treningów",
    description: "Budujesz społeczność treningową cegła po cegle",
    image: "https://ibb.co/Qj33Dgq9",
    requirements: {
      action: "Trainings",
      target: 20,
    },
  },
  //======================= ODZNAKI ZA CZUJNIK =====================
  {
    _id: "6887b70cf3e23b7c2b647223",
    title: "Techniczny debiut",
    description: "Połączyłeś się z czujnikiem- witaj w inteligentnym treningu",
    image: "https://ibb.co/6RypnMCK",
    requirements: {
      action: "Sensor",
      isConnected: true,
    },
  },
  //======================= ODZNAKI ZA STWORZONE ĆWICZENIA =====================
  {
    _id: "6887b71977991eea1a365c96",
    title: "Kreator ruchu - 1 utworzone ćwiczenie",
    description: "Twoje pierwsze autorskie ćwiczenie jest już gotowe",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Exercises",
      target: 1,
    },
  },
  {
    _id: "6887b72056848532eef4a185",
    title: "Pomysłowy trener - 5 utworzonych ćwiczeń",
    description: "Pomagasz innym w drodze do lepszej formy",
    image: "https://ibb.co/gZCPMG6C",
    requirements: {
      action: "Exercises",
      target: 5,
    },
  },
  {
    _id: "6887c060b56cf2f6f1fd3d11",
    title: "Ekspert formy - 10 utworzonych ćwiczeń",
    description: "Twoja baza ćwiczeń robi się naprawdę solidna",
    image: "https://ibb.co/8LNzXGJw",
    requirements: {
      action: "Exercises",
      target: 10,
    },
  },
  {
    _id: "6887b72c85fbc0928cd99183",
    title: "Mistrz tworzenia ćwiczeń - 20 utworzonych ćwiczeń",
    description: "Komponujesz ćwiczeń jak zawodowiec - czapki z głów",
    image: "https://ibb.co/8D8k8cBg",
    requirements: {
      action: "Exercises",
      target: 20,
    },
  },
  //======================= ODZNAKI ZA STWORZONE POSTY =====================
  {
    _id: "6887b73a2078947c7f327f80",
    title: "Pierwszy głos - 1 utworzony post",
    description: "Zabierasz głos w społeczności - świetny początek!",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1757191016/profile_pictures/mrydusfs3zbeyiwpvdem.jpg",
    requirements: {
      action: "Posts",
      target: 1,
    },
  },
  {
    _id: "6887b74635b1040012e6da44",
    title: "Aktywny motywator - 5 utworzonych postów",
    description: "Twoje wpisy dodają innym energii i inspiracji.",
    image: "https://ibb.co/hFZ9nrGP",
    requirements: {
      action: "Posts",
      target: 5,
    },
  },
  {
    _id: "6887b751f744ffa000f0a21a",
    title: "Głos siłowni - 10 utworzonych postów",
    description: "Twoje treści budują atmosferę i wspierają społeczność.",
    image: "https://ibb.co/tTN8LLP7",
    requirements: {
      action: "Posts",
      target: 10,
    },
  },
  {
    _id: "6887b75f905b84316aa72975",
    title: "Lider dyskusji - 20 utworzonych postów",
    description: "Twoje posty wyznaczają rytm całej społeczności.",
    image: "https://ibb.co/vMyLCkt",
    requirements: {
      action: "Posts",
      target: 20,
    },
  },
];
