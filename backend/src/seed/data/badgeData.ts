import Badge from "../../models/badgeModel";
export const seedBadges = async () => {
  await Badge.deleteMany();
  await Badge.insertMany(badges);
  console.log("Odznaki dodane");
};

const badges = [
  {
    title: "Rok z nami",
    description: "Za pierwszy rok aktywności. Trzymasz formę!",
    image: "https://ibb.co/JWcY67dB",
    requirements: {
      action: "Days",
      target: 1,
    },
  },
  {
    title: "2 lata wytrwałości",
    description: "Dwa lata wytrwałości- imponujące",
    image: "https://ibb.co/n84Myzb5",
    requirements: {
      action: "Days",
      target: 2,
    },
  },
  {
    title: "3 lata treningu",
    description: "Trzy lata progresu i rozwoju. Brawo!",
    image: "https://ibb.co/VYvD1rbk",
    requirements: {
      action: "Days",
      target: 3,
    },
  },
  {
    title: "4-letni weteran",
    description: "Cztery lata z GymEaze- przykład dla innych",
    image: "https://ibb.co/LD5yZNPm",
    requirements: {
      action: "Days",
      target: 4,
    },
  },
  {
    title: "5 lat konsekwencji",
    description: "Pół dekady zaangażowania. Jesteś legendą",
    image: "https://ibb.co/vCd62KNs",
    requirements: {
      action: "Days",
      target: 5,
    },
  },

  //======================= ODZNAKI ZA POPULARNOŚĆ =====================
  {
    title: "Pierwsza dziesiątka - 10 motywatorów",
    description: "Gratulacje! Masz już pierwszych fanów",
    image: "https://ibb.co/hFhGwDBM",
    requirements: {
      action: "Followers",
      target: 10,
    },
  },
  {
    title: "Pół setki mocy - 50 motywatorów",
    description: "Jesteś inspiracją - coraz więcej osób Cię śledzi",
    image: "https://ibb.co/vv3LwCJL",
    requirements: {
      action: "Followers",
      target: 50,
    },
  },
  {
    title: "Fitness influencer - 100 obserwujących",
    description: "Setka za Tobą! Twoja forma robi wrażenie",
    image: "https://ibb.co/NgkyztjH",
    requirements: {
      action: "Followers",
      target: 100,
    },
  },
  //======================= ODZNAKI ZA STWORZONE TRENINGI =====================
  {
    title: "Trener w akccji - 1 utworzony trening",
    description: "Twój pierwszy trening jest już utworzony. Świetna robota",
    image: "https://ibb.co/vCksp4Gb",
    requirements: {
      action: "Trainings",
      target: 1,
    },
  },
  {
    title: "Twórca formy - 5 utworzonych treningów",
    description: "Pomagasz innym w drodze do lepszej formy",
    image: "https://ibb.co/3m1c7NBz",
    requirements: {
      action: "Trainings",
      target: 5,
    },
  },
  {
    title: "Mentor społeczności - 10 utworzonych treningów",
    description: "Twoje plany inspirują innych. Jesteś prawdziwym liderem",
    image: "https://ibb.co/ZRHGN37F",
    requirements: {
      action: "Trainings",
      target: 10,
    },
  },
  {
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
    title: "Techniczny debiut",
    description: "Połączyłeś się z czujnikiem- witaj w inteligentnym treningu",
    image: "https://ibb.co/6RypnMCK",
    requirements: {
      action: "Sensor",
      connected: true,
    },
  },
  //======================= ODZNAKI ZA STWORZONE ĆWICZENIA =====================
  {
    title: "Kreator ruchu - 1 utworzone ćwiczenie",
    description: "Twoje pierwsze autorskie ćwiczenie jest już gotowe",
    image: "https://ibb.co/Ngv75Zc1",
    requirements: {
      action: "Exercises",
      target: 1,
    },
  },
  {
    title: "Pomysłowy trener - 5 utworzonych ćwiczeń",
    description: "Pomagasz innym w drodze do lepszej formy",
    image: "https://ibb.co/gZCPMG6C",
    requirements: {
      action: "Exercises",
      target: 5,
    },
  },
  {
    title: "Ekspert formy - 10 utworzonych ćwiczeń",
    description: "Twoja baza ćwiczeń robi się naprawdę solidna",
    image: "https://ibb.co/8LNzXGJw",
    requirements: {
      action: "Exercises",
      target: 10,
    },
  },
  {
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
    title: "Pierwszy głos - 1 utworzony post",
    description: "Zabierasz głos w społeczności - świetny początek!",
    image: "https://ibb.co/XxSg20fQ",
    requirements: {
      action: "Posts",
      target: 1,
    },
  },
  {
    title: "Aktywny motywator - 5 utworzonych postów",
    description: "Twoje wpisy dodają innym energii i inspiracji.",
    image: "https://ibb.co/hFZ9nrGP",
    requirements: {
      action: "Posts",
      target: 5,
    },
  },
  {
    title: "Głos siłowni - 10 utworzonych postów",
    description: "Twoje treści budują atmosferę i wspierają społeczność.",
    image: "https://ibb.co/tTN8LLP7",
    requirements: {
      action: "Posts",
      target: 10,
    },
  },
  {
    title: "Lider dyskusji - 20 utworzonych postów",
    description: "Twoje posty wyznaczają rytm całej społeczności.",
    image: "https://ibb.co/vMyLCkt",
    requirements: {
      action: "Posts",
      target: 20,
    },
  },
];
