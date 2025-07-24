import { Types } from "mongoose";
import Exercise from "../../models/exerciseModel";

export const seedExercises = async () => {
  await Exercise.deleteMany();
  await Exercise.insertMany(exercises);
  console.log("Ćwiczenia dodane");
};

const exercises = [
  {
    author: new Types.ObjectId("64b4e2f5a1c8d12e3f4b5601"),
    name: "Wyciskanie hantli na ławce skośnej",
    description:
      "Ćwiczenie na górną część klatki piersiowej skupia się na wzmocnieniu i modelowaniu mięśni piersiowych w ich górnym obszarze. Wykonywane prawidłowo, pomaga rozbudować masę mięśniową oraz poprawić kształt i definicję mięśni piersiowych. Najczęściej stosuje się je z wykorzystaniem nachylonej ławki, co pozwala lepiej zaangażować górne włókna mięśniowe. To ćwiczenie jest kluczowe dla osób pragnących uzyskać pełniejszy i bardziej zbalansowany wygląd klatki piersiowej oraz zwiększyć siłę górnej partii mięśniowej. Regularne wykonywanie poprawia stabilizację barków oraz wspomaga inne ruchy górnej części ciała. ",
    image: "https://ibb.co/DHDCrhZq",
    video: "https://streamable.com/1gthhv",
    likes: 0,
    visibility: "Publiczny",
    advancedLevel: "Początkujący",
    muscleGroup: [new Types.ObjectId("68825a370b497082d4918e2b")],
    equipment: [
      new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e1"),
      new Types.ObjectId("6882577a998fe6143adc8184"),
    ],
    hashtags: ["moc", "fitness", "trening"],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    author: new Types.ObjectId("64b4e2f5a1c8d12e3f4b5601"),
    name: "Przysiady ze sztangą",
    description:
      "Podstawowe ćwiczenie na nogi i pośladki to fundament każdego treningu dolnych partii ciała. Skupia się na aktywacji mięśni czworogłowych uda, dwugłowych uda oraz mięśni pośladkowych, które odpowiadają za siłę, stabilizację oraz estetykę sylwetki. To ruch angażujący wiele stawów i grup mięśniowych jednocześnie, dzięki czemu poprawia koordynację oraz zwiększa wytrzymałość mięśniową. Regularne wykonywanie tego ćwiczenia pomaga w kształtowaniu jędrnych i silnych nóg, a także wspiera prawidłową postawę i zmniejsza ryzyko kontuzji. Idealne zarówno dla osób początkujących, jak i zaawansowanych, stanowi bazę do budowania bardziej zaawansowanych treningów dolnych partii ciała.",
    image: "https://ibb.co/B5ypz3Lh",
    video: "https://streamable.com/1gthhv",
    likes: 5,
    visibility: "Publiczny",
    advancedLevel: "Średniozaawansowany",
    muscleGroup: [
      new Types.ObjectId("68825a812383da02f31ac12a"),
      new Types.ObjectId("68825aab333b8f74d2ea55dd"),
    ],
    equipment: [new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e2")],
    hashtags: [
      new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d607"),
      new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d611"),
    ],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  {
    author: new Types.ObjectId("64b4e2f5a1c8d12e3f4b5603"),
    name: "Martwy ciąg klasyczny",
    description:
      "Kompleksowe ćwiczenie angażujące niemal całe ciało, ze szczególnym naciskiem na tylną kinematyczną. Aktywuje mięśnie dwugłowe uda, pośladkowe, czworoboczne grzbietu oraz prostowniki kręgosłupa. To fundamentalny ruch, który buduje funkcjonalną siłę, poprawia stabilizację core'a oraz rozwija moc eksplozywną. Regularne wykonywanie martwego ciągu wzmacnia całą tylną część ciała, co przekłada się na lepszą postawę, zwiększoną wydolność w codziennych czynnościach oraz zmniejszone ryzyko bólów kręgosłupa. Ćwiczenie wymaga precyzyjnej techniki, ale nagradzane jest spektakularnymi efektami w budowaniu siły i masy mięśniowej.",
    image: "https://ibb.co/cKdg8s12",
    video: "https://streamable.com/1gthhv",
    likes: 8,
    visibility: "Publiczny",
    advancedLevel: "Zaawansowany",
    muscleGroup: [
      new Types.ObjectId("68825aab333b8f74d2ea55dd"),
      new Types.ObjectId("68825a812383da02f31ac12a"),
      new Types.ObjectId("68825a4660b5b044fd789516"),
    ],
    equipment: [new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e2")],
    hashtags: [
      new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d607"),
      new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d612"),
    ],
    createdAt: new Date(),
    updatedAt: new Date(),
  },

  {
    author: new Types.ObjectId("64b4e2f5a1c8d12e3f4b5604"),
    name: "Wyciskanie sztangi na ławce płaskiej",
    description:
      "Królewskie ćwiczenie na klatkę piersiową, które stanowi fundament treningu górnych partii ciała. Głównie aktywuje wielką mięsień piersiowy, tricepsy oraz przednią część mięśni naramiennych. To klasyczny ruch, który buduje imponującą masę i siłę górnej części korpusu. Regularne wykonywanie wyciskania na ławce płaskiej rozwija nie tylko estetykę klatki piersiowej, ale także funkcjonalną siłę potrzebną w codziennym życiu. Ćwiczenie poprawia stabilizację łopatek, wzmacnia mięśnie pomocnicze oraz rozwija koordynację między różnymi grupami mięśniowymi. Idealne dla wszystkich poziomów zaawansowania z możliwością progresywnego zwiększania obciążenia.",
    image: "https://ibb.co/M5RWMKN6",
    video: "https://streamable.com/1gthhv",
    likes: 12,
    visibility: "Prywatny",
    advancedLevel: "Początkujący",
    muscleGroup: [
      new Types.ObjectId("68825a3f2f2351ccb33b3dc6"),
      new Types.ObjectId("68825a370b497082d4918e2b"),
      new Types.ObjectId("68825a2eb5b7ba6fb98cbfb8"),
    ],
    equipment: [
      new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e2"),
      new Types.ObjectId("6882577a998fe6143adc8184"),
    ],
    hashtags: [new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d616")],
    createdAt: new Date(),
    updatedAt: new Date(),
  },

  {
    author: new Types.ObjectId("64b4e2f5a1c8d12e3f4b5602"),
    name: "Podciąganie na drążku",
    description:
      "Fundamentalne ćwiczenie na górne partie ciała, które angażuje najszerszy mięsień grzbietu, dwugłowy ramienia oraz tylną część naramiennych. To naturalne ciągnące ćwiczenie rozwija funkcjonalną siłę oraz buduje charakterystyczny kształt pleców typu V. Regularne podciąganie wzmacnia grip, poprawia stabilizację łopatek oraz koryguje postawę poprzez przeciwdziałanie skutkom siedzącego trybu życia. Ćwiczenie można modyfikować poprzez zmianę chwytu, szerokości oraz tempo wykonania, co pozwala na ciągłą progresję. Choć wymagające, podciąganie należy do najefektywniejszych ruchów dla rozbudowy górnych partii ciała i budowania imponującej sylwetki.",
    image: "https://ibb.co/GvhnPKs2",
    video: "https://streamable.com/1gthhv",
    likes: 2,
    visibility: "Publiczny",
    advancedLevel: "Średniozaawansowany",
    muscleGroup: [
      new Types.ObjectId("68825a2739b1644b05f41baa"),
      new Types.ObjectId("68825a4660b5b044fd789516"),
      new Types.ObjectId("68825ab9658591ac85380bf7"),
    ],
    equipment: [new Types.ObjectId("6882588fea1fb71b76cb9e84")],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
];
