import TrainingPlan from '../../models/trainingPlanModel';
import { userIds } from './userData';
import { exercisesIds } from './exerciseData';
import { AdvancedLevel } from '../../constants/advancedLevel';
import { muscleGroupsIds } from './muscleGroupData';
import { equipmentIds } from './equipmentData';

export const seedTrainingPlans = async () => {
  await TrainingPlan.deleteMany();
  await TrainingPlan.insertMany(trainingPlans);
};

const trainingPlans = [
  {
    _id: '68e2521ddbfa97cffdecd0fa',
    name: 'Plan Tygodniowy - Full Body',
    author: userIds.admin,
    description:
      'Plan dla początkująco-średniozaawansowanych. 4 dni treningowe + 3 dni regeneracji.',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759616818/training-plans/images/qufwr8aouovypfquzar2.png',
    goal: 'Spalanie kalorii',
    visibility: 'Publiczny',
    advancedLevel: 'Średniozaawansowany',
    likes: 0,
    days: [
      {
        dayNumber: 0,
        name: 'Poniedziałek - Full Body',
        totalDuration: 675,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.dumbbellShoulderPress,

            name: 'Wyciskanie hantli nad głowę siedząc (Seated Dumbbell Shoulder Press)',
            description: `1. Usiądź na ławce z oparciem, trzymając hantle na wysokości barków, dłonie skierowane do przodu.\n2. Napnij brzuch i plecy, utrzymując stabilną postawę.\n3. Wypchnij hantle nad głowę, prostując ramiona, a następnie powoli opuść je z powrotem do pozycji wyjściowej.\nĆwiczenie rozwija tricepsy oraz poprawia stabilizację barków i górnej części tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148323/wyciskanie-hantli-nad-glowe-siedzac_ayell2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148454/wyciskanie-hantli-nad-glowe-siedzac_pzfyyi.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.triceps],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],

            isRepsSet: true,
            duration: 264,
            restAfterSet: 90,
            sets: [
              { repetitions: 10, weight: 60 },
              { repetitions: 10, weight: 60 },
              { repetitions: 8, weight: 65 },
            ],
          },
          {
            originalId: exercisesIds.machineSeatedCalfRaise,

            name: 'Wyciskanie hantli nad głowę siedząc (Seated Dumbbell Shoulder Press)',
            description: `1. Usiądź na ławce z oparciem, trzymając hantle na wysokości barków, dłonie skierowane do przodu.\n2. Napnij brzuch i plecy, utrzymując stabilną postawę.\n3. Wypchnij hantle nad głowę, prostując ramiona, a następnie powoli opuść je z powrotem do pozycji wyjściowej.\nĆwiczenie rozwija tricepsy oraz poprawia stabilizację barków i górnej części tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148323/wyciskanie-hantli-nad-glowe-siedzac_ayell2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148454/wyciskanie-hantli-nad-glowe-siedzac_pzfyyi.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.triceps],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],

            isRepsSet: true,
            duration: 246,
            restAfterSet: 90,
            sets: [
              { repetitions: 8, weight: 50 },
              { repetitions: 8, weight: 50 },
              { repetitions: 6, weight: 45 },
            ],
          },
          {
            originalId: exercisesIds.barbellRow,

            name: 'Wiosłowanie sztangą w opadzie tułowia (Barbell Row)',
            description: `Stań w lekkim rozkroku, kolana lekko ugnij i pochyl tułów do przodu, utrzymując plecy prosto.\nChwyć sztangę nachwytem na szerokość barków.\nPrzyciągaj sztangę w kierunku dolnej części klatki lub brzucha, łokcie prowadząc blisko tułowia.\nPowoli opuszczaj sztangę do pozycji wyjściowej, kontrolując ciężar.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148317/wioslowanie-sztanga_xoyor0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148447/wioslowanie-sztanga_hbnxfm.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.biceps,
            ],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],

            isRepsSet: false,
            duration: 165,
            restAfterSet: 30,
            sets: [{ duration: 60 }, { duration: 45 }],
          },
        ],
      },
      {
        dayNumber: 1,
        name: 'Wtorek - Regeneracja',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 2,
        name: 'Środa - Push Day',
        totalDuration: 504,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellPreacherCurl,

            name: 'Uginanie przedramion na modlitewniku przy użyciu sztangi łamanej (Barbell Preacher Curl)',
            description: `1. Usiądź na modlitewniku, oprzyj ramiona na poduszce i chwyć sztangę.\n2. Unieś sztangę, zginając ramiona w łokciach, skupiając się na pracy bicepsów.\n3. Powoli opuść sztangę do pozycji wyjściowej, kontrolując ciężar.\nĆwiczenie izoluje biceps, minimalizując udział innych mięśni.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148310/uginanie-ramion-modlitewnik_lr4jjn.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148441/uginanie-ramion-modlitewnik_psnfxx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps],
            equipment: [
              equipmentIds.preacherCurlBench,
              equipmentIds.curlBarbell,
              equipmentIds.weightPlates,
            ],

            isRepsSet: true,
            duration: 246,
            restAfterSet: 90,
            sets: [
              { repetitions: 8, weight: 30 },
              { repetitions: 8, weight: 30 },
              { repetitions: 6, weight: 35 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],

            isRepsSet: true,
            duration: 258,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 8, weight: null },
              { repetitions: 8, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: 3,
        name: 'Czwartek - Rest',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 4,
        name: 'Piątek - Pull Day',
        totalDuration: 609,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.pullup,

            name: 'Podciąganie na drążku (Pull-Up)',
            description: `1. Chwyć drążek nachwytem, dłonie nieco szerzej niż barki.\n2. Zawiśnij swobodnie, stabilizując ciało.\n3. Unieś ciało, ciągnąc łokcie w dół i do tyłu, aż broda znajdzie się nad drążkiem.\n4. Powoli opuść ciało do pełnego wyprostu ramion, kontrolując ruch.\n5. Utrzymuj napięcie pleców i ramion przez cały ruch.\nDo zaawansowanego treningu możesz dodać obciążenie za pomocą pasa z ciężarem.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148281/podciaganie_xqmq5x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148411/podciaganie_ugrtlq.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.abs,
            ],
            equipment: [equipmentIds.pullupBar, equipmentIds.ownWeight],

            isRepsSet: true,
            duration: 285,
            restAfterSet: 120,
            sets: [
              { repetitions: 5, weight: 80 },
              { repetitions: 5, weight: 80 },
              { repetitions: 5, weight: 90 },
            ],
          },
          {
            originalId: exercisesIds.seatedKneeTuck,

            name: 'Przyciąganie kolan do klatki piersiowej w siadzie (Seated Knee Tuck)',
            description: `Usiądź na podłodze, oprzyj się lekko do tyłu i podeprzyj dłońmi.\nUnieś zgięte kolana w kierunku klatki piersiowej, napinając brzuch.\nNastępnie wyprostuj nogi, nie odkładając ich na ziemię.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148290/przyciaganie-kolan-do-klatki-piersiowej_lmovn1.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148420/przyciaganie-kolan-do-klatki-piersiowej_oqluqf.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs],
            equipment: [equipmentIds.trainingMat, equipmentIds.ownWeight],

            isRepsSet: true,
            duration: 324,
            restAfterSet: 90,
            sets: [
              { repetitions: 8, weight: null },
              { repetitions: 4, weight: null },
              { repetitions: 6, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: 5,
        name: 'Sobota - Rest',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 6,
        name: 'Niedziela - Full Body (Light)',
        totalDuration: 390,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.stairClimber,

            name: 'Schody treningowe (Stair Climber)',
            description: `Stań na maszynie, chwyć uchwyty dla stabilizacji i rozpocznij rytmiczne wchodzenie po schodach.\nUtrzymuj prostą sylwetkę i napnij pośladki.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148304/stepper_aqzvkj.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148435/stepper_nkvosa.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.cardio,
              muscleGroupsIds.glutes,
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.stairClimber],

            isRepsSet: true,
            duration: 102,
            restAfterSet: 30,
            sets: [
              { repetitions: 12, weight: 20 },
              { repetitions: 12, weight: 20 },
            ],
          },
          {
            originalId: exercisesIds.stationaryBike,

            name: 'Rower stacjonarny (Stationary Bike)',
            description: `Usiądź wygodnie, dopasuj siodełko i rozpocznij jazdę w kontrolowanym tempie.\nĆwiczenie poprawia kondycję oraz wspiera spalanie kalorii.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148299/rowerek-maszyna_ynijed.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148428/rowerek-maszyna_pdhfpi.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.stationaryBike],

            isRepsSet: true,
            duration: 288,
            restAfterSet: 60,
            sets: [
              { repetitions: 14, weight: null },
              { repetitions: 12, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
        ],
      },
    ],
    hashtags: ['fullbody', 'strength', 'hypertrophy'],
    createdAt: new Date(1759616818984),
    updatedAt: new Date(1759616818984),
    __v: 0,
  },
  {
    _id: '68e25c6cab0991b357da72b2',
    name: 'Plan Split - Push / Pull / Legs',
    author: userIds.admin,
    description:
      'Zaawansowany plan treningowy typu split z dniem ogólnorozwojowym\r\n\r\nTen plan został opracowany z myślą o osobach średniozaawansowanych i zaawansowanych, które mają już solidne podstawy techniczne, dobrą świadomość własnego ciała oraz doświadczenie w pracy z ciężarami. Celem programu jest maksymalne rozwinięcie siły, masy mięśniowej oraz wytrzymałości, przy jednoczesnym zachowaniu równowagi pomiędzy wszystkimi partiami ciała.\r\n\r\nTrening typu split polega na podziale partii mięśniowych pomiędzy poszczególne dni tygodnia. Dzięki temu każda grupa mięśniowa może być trenowana z odpowiednią intensywnością, a organizm otrzymuje wystarczającą ilość czasu na regenerację. Program zakłada 5 dni treningowych w tygodniu, w tym jeden dzień ogólnorozwojowy, który ma na celu poprawę koordynacji, mobilności, stabilizacji i ogólnej sprawności fizycznej.',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759665022/training-plans/images/eyohmu2lbopw7ti7fnky.png',
    goal: 'Zwiększanie siły',
    visibility: 'Publiczny',
    advancedLevel: 'Zaawansowany',
    likes: 0,
    days: [
      {
        dayNumber: 0,
        name: 'Poniedziałek - Push (Klatka, Barki, Triceps)',
        totalDuration: 5712,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 1365,
            restAfterSet: 300,
            sets: [
              { repetitions: 11, weight: 60 },
              { repetitions: 12, weight: 65 },
              { repetitions: 12, weight: 70 },
              { repetitions: 12, weight: 70 },
              { repetitions: 8, weight: 75 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellInclineBenchPress,

            name: 'Wyciskanie hantli na ławce skośnej (Dumbbell Incline Bench Press)',
            description: `1. Usiądź na ławce ustawionej pod kątem 30-45°, stopy stabilnie na podłodze.\n2. Chwyć hantle i unieś je nad klatkę piersiową, dłonie skierowane do przodu.\n3. Powoli opuszczaj hantle w kierunku górnej części klatki, kontrolując ruch.\n4. Wypchnij hantle z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148324/wyciskanie-hantli-skosna_r44nq0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148456/wyciskanie-hantli-skosna_phljzl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],

            isRepsSet: true,
            duration: 708,
            restAfterSet: 180,
            sets: [
              { repetitions: 14, weight: 20 },
              { repetitions: 14, weight: 20 },
              { repetitions: 14, weight: 30 },
              { repetitions: 14, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.overheadDumbbellExtension,

            name: 'Wyciskanie francuskie hantli oburącz stojąc (Standing Dumbbell French Press)',
            description: `1. Stań prosto, stopy na szerokość barków, trzymaj hantel obiema rękami nad głową.\n2. Unieś ramiona, aby były wyprostowane.\n3. Powoli opuszczaj hantel za głowę, zginając łokcie i kontrolując ciężar.\n4. Wypchnij hantel z powrotem do pozycji wyjściowej, napinając tricepsy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148322/wyciskanie-hantla-zza-glowy_dttfui.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148453/wyciskanie-hantla-zza-glowy_laqija.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.triceps],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 600,
            restAfterSet: 120,
            sets: [
              { repetitions: 20, weight: 10 },
              { repetitions: 20, weight: 10 },
              { repetitions: 20, weight: 10 },
              { repetitions: 20, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.barbellPreacherCurl,

            name: 'Uginanie przedramion na modlitewniku przy użyciu sztangi łamanej (Barbell Preacher Curl)',
            description: `1. Usiądź na modlitewniku, oprzyj ramiona na poduszce i chwyć sztangę.\n2. Unieś sztangę, zginając ramiona w łokciach, skupiając się na pracy bicepsów.\n3. Powoli opuść sztangę do pozycji wyjściowej, kontrolując ciężar.\nĆwiczenie izoluje biceps, minimalizując udział innych mięśni.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148310/uginanie-ramion-modlitewnik_lr4jjn.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148441/uginanie-ramion-modlitewnik_psnfxx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps],
            equipment: [
              equipmentIds.preacherCurlBench,
              equipmentIds.curlBarbell,
              equipmentIds.weightPlates,
            ],
            isRepsSet: true,
            duration: 348,
            restAfterSet: 120,
            sets: [
              { repetitions: 12, weight: 20 },
              { repetitions: 12, weight: 20 },
              { repetitions: 12, weight: 20 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellShoulderPress,

            name: 'Wyciskanie hantli nad głowę siedząc (Seated Dumbbell Shoulder Press)',
            description: `1. Usiądź na ławce z oparciem, trzymając hantle na wysokości barków, dłonie skierowane do przodu.\n2. Napnij brzuch i plecy, utrzymując stabilną postawę.\n3. Wypchnij hantle nad głowę, prostując ramiona, a następnie powoli opuść je z powrotem do pozycji wyjściowej.\nĆwiczenie rozwija tricepsy oraz poprawia stabilizację barków i górnej części tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148323/wyciskanie-hantli-nad-glowe-siedzac_ayell2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148454/wyciskanie-hantli-nad-glowe-siedzac_pzfyyi.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.triceps],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 255,
            restAfterSet: 60,
            sets: [
              { repetitions: 15, weight: 30 },
              { repetitions: 15, weight: 30 },
              { repetitions: 15, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.cableBicepCurl,

            name: 'Zginanie przedramion z drążkiem wyciągu dolnego stojąc (Cable Curl)',
            description: `Stań przodem do wyciągu dolnego, chwyć uchwyt podchwytem.\nUtrzymując łokcie blisko tułowia, powoli uginaj ramiona, przyciągając uchwyt w kierunku barków.\nKontrolowanym ruchem wróć do pozycji wyjściowej.\nĆwiczenie skutecznie izoluje biceps, zapewnia równomierne napięcie przez cały zakres ruchu i pozwala na bezpieczny trening z obciążeniem.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148314/uginanie-ramion-z-wyciagiem-dolnym_ogmqdd.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148444/uginanie-ramion-z-wyciagiem-dolnym_xutdqj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 456,
            restAfterSet: 120,
            sets: [
              { repetitions: 8, weight: 30 },
              { repetitions: 8, weight: 30 },
              { repetitions: 8, weight: 30 },
              { repetitions: 8, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.machineFly,

            name: 'Rozpiętki na maszynie (Motylki | Machine Fly)',
            description: `1. Usiądź na maszynie, oprzyj plecy o oparcie i ustaw ramiona na wysokości klatki piersiowej.\n2. Chwyć uchwyty i powoli przyciągaj je do siebie, skupiając się na pracy mięśni klatki piersiowej.\n3. Kontrolowanym ruchem powróć do pozycji wyjściowej, nie blokując stawów łokciowych.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148300/rozpietki_ntlqo4.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148430/rozpietki_smjvcr.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.chest, muscleGroupsIds.shoulders],
            equipment: [equipmentIds.butterflyMachine],
            isRepsSet: true,
            duration: 720,
            restAfterSet: 180,
            sets: [
              { repetitions: 15, weight: 35 },
              { repetitions: 15, weight: 35 },
              { repetitions: 15, weight: 35 },
              { repetitions: 15, weight: 35 },
            ],
          },
          {
            originalId: exercisesIds.cableTricepsPushdown,

            name: 'Prostowanie ramion z linkami wyciągu górnego (Cable Triceps Pushdown)',
            description: `1. Stań przodem do wyciągu górnego i chwyć linę oburącz.\n2. Cofnij lekko barki, napnij brzuch i trzymaj łokcie blisko tułowia.\n3. Prostuj ramiona w dół, jednocześnie rozchylając końcówki liny na boki, aby mocniej zaangażować tricepsy.\n4. Wróć powoli do pozycji startowej, kontrolując ciężar.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148285/prostowanie-ramion-z-linka-wyciagu-gornego_iubnun.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148415/prostowanie-ramion-z-linka-wyciagu-gornego_nqubiw.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.triceps],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 1260,
            restAfterSet: 180,
            sets: [
              { repetitions: 10, weight: 15 },
              { repetitions: 10, weight: 15 },
              { repetitions: 10, weight: 15 },
              { repetitions: 10, weight: 15 },
              { repetitions: 10, weight: 15 },
              { repetitions: 10, weight: 15 },
            ],
          },
        ],
      },
      {
        dayNumber: 1,
        name: 'Wtorek - Pull (Plecy, Biceps)',
        totalDuration: 3072,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.facePull,

            name: 'Przyciąganie linki wyciągu do twarzy (Cable Face Pull)',
            description: `Ustaw wyciąg górny na wysokości twarzy i podepnij linę.\nStań stabilnie, chwyć końce liny nachwytem.\nPrzyciągnij linę do twarzy, prowadząc łokcie szeroko na boki i zewnętrznie rotując barki.\nSkup się na pracy tylnych aktonów barków i mięśni między łopatkami, a nie na bicepsach.\nĆwiczenie poprawia stabilność barków, postawę i rozwija górną część pleców.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148293/przyciaganie-linki-wyciagu-do-twarzy_wpjvya.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148422/przyciaganie-linki-wyciagu-do-twarzy_tofsq5.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.upperBack],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 480,
            restAfterSet: 180,
            sets: [
              { repetitions: 14, weight: 40 },
              { repetitions: 14, weight: 45 },
              { repetitions: 12, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.latPulldown,

            name: 'Ściąganie drążka wyciągu górnego (Lat Pulldown)',
            description: `1. Usiądź na maszynie i ustaw uda pod poduszkami stabilizującymi.\n2. Chwyć drążek szerzej niż szerokość barków nachwytem.\n3. Ściągaj drążek w dół do górnej części klatki, prowadząc łokcie wzdłuż boków ciała. Plecy trzymaj proste, a ruch wykonuj kontrolowanie, bez bujania tułowiem.\n4. Powoli wróć do pozycji startowej, rozciągając mięśnie grzbietu.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148288/przyciaganie-drazka-do-klaty-siedzac_zmpbg5.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148418/przyciaganie-drazka-do-klaty-siedzac_gygykx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.upperBack,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 945,
            restAfterSet: 180,
            sets: [
              { repetitions: 15, weight: 5 },
              { repetitions: 15, weight: 5 },
              { repetitions: 15, weight: 5 },
              { repetitions: 15, weight: 5 },
              { repetitions: 15, weight: 5 },
            ],
          },
          {
            originalId: exercisesIds.cableRow,

            name: 'Wiosłowanie na wyciągu dolnym siedząc (Cable Seated Row)',
            description: `Usiądź na maszynie z nogami opartymi o podnóżki i chwyć uchwyt oburącz.\nPlecy trzymaj prosto, klatkę lekko wypchnij do przodu.\nPrzyciągaj uchwyt do brzucha, prowadząc łokcie blisko ciała i mocno napinając mięśnie grzbietu.\nNastępnie powoli prostuj ręce, utrzymując kontrolę nad ciężarem.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148286/przyciaganie-do-brzucha-siedzac_ccfrzo.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148417/przyciaganie-do-brzucha-siedzac_rupjld.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.biceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 336,
            restAfterSet: 120,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 35 },
              { repetitions: 8, weight: 40 },
            ],
          },
          {
            originalId: exercisesIds.pushup,

            name: 'Pompki (Push-Up)',
            description: `1. Ręce rozstaw na szerokość barków, ciało w jednej linii od głowy do stóp.\n2. Napnij ciało i pośladki, aby utrzymać stabilną pozycję.\n3. Zginając łokcie, opuść ciało w dół, aż klatka prawie dotknie podłoża.\n4. Wypchnij ciało w górę, prostując ramiona i wracając do pozycji wyjściowej.\n5. Kontroluj tempo i unikaj opadania bioder.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148283/pompka_vwkogt.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148412/pompka_dpftbv.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.abs,
            ],
            equipment: [equipmentIds.ownWeight],
            isRepsSet: true,
            duration: 495,
            restAfterSet: 180,
            sets: [
              { repetitions: 15, weight: 2 },
              { repetitions: 15, weight: 2 },
              { repetitions: 15, weight: 2 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellShrug,

            name: 'Wzruszanie ramion z hantlami (Kaptury | Dumbbell Shrug)',
            description:
              'Proste, ale bardzo skuteczne ćwiczenie na mięśnie czworoboczne. Wystarczy stanąć prosto z hantlami w dłoniach i unosić barki możliwie jak najwyżej, starając się nie bujać ciałem ani nie zginać łokci. Ruch jest krótki, lecz intensywnie angażuje górną część pleców i poprawia stabilizację obręczy barkowej.',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148273/kaptury_mnliis.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148403/kaptury_locwwi.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.upperBack, muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: 20 },
              { repetitions: 10, weight: 20 },
              { repetitions: 10, weight: 20 },
              { repetitions: 8, weight: 25 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 612,
            restAfterSet: 120,
            sets: [
              { repetitions: 12, weight: 35 },
              { repetitions: 12, weight: 35 },
              { repetitions: 12, weight: 35 },
              { repetitions: 8, weight: 40 },
            ],
          },
        ],
      },
      {
        dayNumber: 2,
        name: 'Środa - Legs (Nogi)',
        totalDuration: 2133,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.machineLegExtension,

            isRepsSet: true,
            duration: 600,
            restAfterSet: 120,
            sets: [
              { repetitions: 20, weight: 60 },
              { repetitions: 20, weight: 60 },
              { repetitions: 20, weight: 60 },
              { repetitions: 20, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 366,
            restAfterSet: 120,
            sets: [
              { repetitions: 14, weight: 20 },
              { repetitions: 14, weight: 20 },
              { repetitions: 14, weight: 20 },
            ],
          },
          {
            originalId: exercisesIds.machineSeatedCalfRaise,

            name: 'Wspięcia na palce siedząc na maszynie (Seated Calf Raise)',
            description: `Usiądź na maszynie, oprzyj kolana pod poduszkami i stopy ustaw na platformie.\nTrzymaj plecy prosto i chwyć uchwyty dla stabilizacji.\nUnieś pięty, prostując stopy maksymalnie do góry, napinając mięśnie łydek.\nPowoli opuść pięty poniżej poziomu platformy, kontrolując ruch.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148319/wspiecia-na-palce-siedzac_tqvsvy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148450/wspiecia-na-palce-siedzac_rcctwy.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.calves],
            equipment: [equipmentIds.calfRaiseMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 672,
            restAfterSet: 180,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 8, weight: 15 },
            ],
          },
          {
            id: exercisesIds.legPress,

            name: 'Wypychanie nóg na suwnicy (Leg Press)',
            description: `1. Usiądź na maszynie, stopy ustaw na platformie na szerokość barków.\n2. Oprzyj plecy o oparcie, uchwyć uchwyty dla stabilizacji.\n3. Powoli uginaj kolana, opuszczając platformę w kierunku klatki piersiowej, utrzymując stopy płasko.\n4. Wypchnij platformę, prostując nogi, ale nie blokując kolan na górze.\nĆwiczenie rozwija mięśnie ud, pośladków i łydek, pozwalając na bezpieczne podnoszenie dużych obciążeń.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148306/suwnica_xb9sqb.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148436/suwnica_pvgwqo.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.calves,
              muscleGroupsIds.glutes,
            ],
            equipment: [equipmentIds.legPress, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 495,
            restAfterSet: 120,
            sets: [
              { repetitions: 15, weight: 60 },
              { repetitions: 15, weight: 60 },
              { repetitions: 15, weight: 60 },
            ],
          },
        ],
      },
      {
        dayNumber: 3,
        name: 'Czwartek - Rest',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 4,
        name: 'Piątek - Full Body Conditioning (15 ćwiczeń!)',
        totalDuration: 10086,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 20 },
              { repetitions: 10, weight: 20 },
              { repetitions: 10, weight: 20 },
            ],
          },
          {
            originalId: exercisesIds.dip,

            name: 'Pompki na poręczach (Dipy)',
            description: `Ustaw się pomiędzy poręczami i chwyć je mocno dłońmi. Unieś ciało, prostując ręce w łokciach.\nNastępnie powoli ugnij łokcie i opuść się w dół, aż ramiona znajdą się równolegle do podłoża lub nieco niżej.\nZ tej pozycji dynamicznie odepchnij się do góry, wracając do pełnego wyprostu.\nTo wymagające ćwiczenie siłowe z ciężarem własnego ciała, które znakomicie rozwija górną część ciała.`,
            image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148270/dipy_swxe8u.webp',
            video: 'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148399/dipy_qe7qbt.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.chest,
            ],
            equipment: [equipmentIds.dipHandlebars, equipmentIds.ownWeight],
            isRepsSet: true,
            duration: 222,
            restAfterSet: 60,
            sets: [
              { repetitions: 12, weight: 0 },
              { repetitions: 12, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 7380,
            restAfterSet: 180,
            sets: [{ duration: 3600 }, { duration: 3600 }],
          },
          {
            originalId: exercisesIds.cableCrunch,

            name: 'Spięcia brzucha klęcząc przy użyciu linki wyciągu górnego (Allachy | Cable Crunch)',
            description: `1. Ustaw wyciąg górny z liną lub drążkiem.\n2. Uklęknij przodem do maszyny, chwyć linę oburącz i przyciągnij ją do głowy lub karku.\n3. Z pozycji wyprostowanej zacznij wykonywać skłon tułowia w dół, napinając mięśnie brzucha.\n4. Utrzymuj napięcie w całym zakresie ruchu, nie ciągnij ciężaru rękami - pracują głównie mięśnie brzucha.\n5. Powoli wróć do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie angażuje mięśnie proste brzucha, wspomaga ich wzmacnianie i rozwój, a dzięki obciążeniu pozwala na progresję siły i masy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/allachy_srovmy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148396/allachy_pftlvh.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs],
            equipment: [equipmentIds.atlas, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.pushup,

            name: 'Pompki (Push-Up)',
            description: `1. Ręce rozstaw na szerokość barków, ciało w jednej linii od głowy do stóp.\n2. Napnij ciało i pośladki, aby utrzymać stabilną pozycję.\n3. Zginając łokcie, opuść ciało w dół, aż klatka prawie dotknie podłoża.\n4. Wypchnij ciało w górę, prostując ramiona i wracając do pozycji wyjściowej.\n5. Kontroluj tempo i unikaj opadania bioder.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148283/pompka_vwkogt.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148412/pompka_dpftbv.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.abs,
            ],
            equipment: [equipmentIds.ownWeight],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.pullup,

            name: 'Podciąganie na drążku (Pull-Up)',
            description: `1. Chwyć drążek nachwytem, dłonie nieco szerzej niż barki.\n2. Zawiśnij swobodnie, stabilizując ciało.\n3. Unieś ciało, ciągnąc łokcie w dół i do tyłu, aż broda znajdzie się nad drążkiem.\n4. Powoli opuść ciało do pełnego wyprostu ramion, kontrolując ruch.\n5. Utrzymuj napięcie pleców i ramion przez cały ruch.\nDo zaawansowanego treningu możesz dodać obciążenie za pomocą pasa z ciężarem.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148281/podciaganie_xqmq5x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148411/podciaganie_ugrtlq.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.abs,
            ],
            equipment: [equipmentIds.pullupBar, equipmentIds.ownWeight],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.barbellOverheadPress,

            name: 'Wyciskanie sztangi nad głowę (OHP | Barbell Overhead Press)',
            description: `Stań prosto, stopy na szerokość barków, sztangę trzymaj nachwytem na wysokości klatki piersiowej.\nPowoli wypchnij sztangę nad głowę, prostując ramiona, zachowując naturalną krzywiznę pleców.\nKontrolowany ruch powrotny do klatki piersiowej zapewnia bezpieczeństwo i maksymalne napięcie mięśni.\nĆwiczenie angażuje głównie barki i tricepsy, rozwijając siłę górnej części ciała.`,
            image: 'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148280/ohpy_u7qdok.webp',
            video: 'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148410/ohpy_ekmyyj.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.triceps, muscleGroupsIds.abs],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 330,
            restAfterSet: 120,
            sets: [
              { repetitions: 10, weight: 25 },
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.hipAbductionMachine,

            name: 'Odwodzenie nóg na maszynie (Machine Hip Abduction)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie.\nTrzymaj uchwyty dla stabilizacji i powoli odchyl nogi na boki, napinając pośladki i zewnętrzną część ud.\nKontrolowany ruch powrotny pozwala utrzymać napięcie w mięśniach.\nĆwiczenie wzmacnia mięśnie pośladkowe, odwodziciele ud i poprawia stabilizację bioder.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148278/odwodzenie-nog_tac7g6.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148409/odwodzenie-nog_zcwuqs.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.glutes,
              muscleGroupsIds.hipFlexors,
              muscleGroupsIds.hamstrings,
            ],
            equipment: [equipmentIds.abductorMachine],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.scissorsKicks,

            name: 'Nożyce (Scissors Kicks)',
            description: `Połóż się na plecach, ręce wzdłuż ciała lub pod pośladkami dla wsparcia.\nUnieś lekko nogi nad podłogę i wykonuj naprzemienne, krzyżujące się ruchy nóg w górę i dół, jak nożyce. Utrzymuj napięcie w brzuchu i staraj się nie odrywać dolnej części pleców od podłoża. Ćwiczenie wzmacnia mięśnie proste i skośne brzucha, poprawia stabilizację core oraz wytrzymałość nóg.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148277/nozyce-nogami_bbkvih.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148408/nozyce-nogami_visep2.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.obliques],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.latPushdown,

            name: 'Prostopadłe przyciąganie drążka wyciągu górnego do bioder (Narciarz | Cable Standing Skier Pull-Down)',
            description: `Stań przodem do wyciągu górnego, lekko pochylając tułów do przodu i chwyć drążek nachwytem.\nPrzyciągaj drążek prostopadle w dół do bioder, naśladując ruch narciarza, napinając plecy i stabilizując barki.\nPowoli wróć do pozycji wyjściowej, kontrolując ciężar i ruch.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148276/narciarz_fhwglc.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148406/narciarz_ezkg0p.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.biceps,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.deadlift,

            name: 'Martwy ciąg (Deadlift)',
            description: `1. Stań prosto, stopy na szerokość bioder, sztanga przed stopami.\n2. Pochyl się w biodrach, utrzymując plecy proste, chwyć sztangę nachwytem lub mieszanym.\n3. Unieś sztangę, prostując nogi i biodra jednocześnie, zachowując naturalną krzywiznę kręgosłupa.\n4. W szczytowej pozycji ściągnij łopatki i utrzymaj napięcie mięśni pleców.\n5. Powoli opuść sztangę kontrolowanym ruchem, nie zaokrąglając pleców.\nMartwy ciąg wzmacnia plecy, pośladki i nogi, a przy prawidłowej technice zwiększa siłę całego ciała.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148275/martwy-ciag_hdgoop.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148405/martwy-ciag_imyhla.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.glutes,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.quads,
              muscleGroupsIds.forearms,
              muscleGroupsIds.calves,
              muscleGroupsIds.abs,
            ],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.dumbbellShrug,

            name: 'Wzruszanie ramion z hantlami (Kaptury | Dumbbell Shrug)',
            description:
              'Proste, ale bardzo skuteczne ćwiczenie na mięśnie czworoboczne. Wystarczy stanąć prosto z hantlami w dłoniach i unosić barki możliwie jak najwyżej, starając się nie bujać ciałem ani nie zginać łokci. Ruch jest krótki, lecz intensywnie angażuje górną część pleców i poprawia stabilizację obręczy barkowej.',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148273/kaptury_mnliis.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148403/kaptury_locwwi.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.upperBack, muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 45,
            restAfterSet: 60,
            sets: [{ repetitions: 15, weight: 30 }],
          },
          {
            originalId: exercisesIds.hammerDumbbell,

            name: 'Uginanie ramion młotkowe (Hammer Dumbbell Curl)',
            description: `1. Stań prosto, trzymając hantle w dłoniach wzdłuż ciała, chwytem neutralnym (kciuki skierowane do góry).\n2. Z wydechem ugnij jednocześnie lub naprzemiennie ramiona w łokciach, unosząc hantle w kierunku barków.\n3. Utrzymuj łokcie blisko tułowia, nie kołysz ciałem i kontroluj ruch.\n4. W szczytowej fazie mocno napnij bicepsy i przedramiona, a następnie powoli opuść ciężar do pozycji wyjściowej.\nTo ćwiczenie skutecznie rozwija mięśnie ramion, ze szczególnym naciskiem na mięsień ramienny i przedramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148272/hamery-hantlami_oz9dqu.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148402/hamery-hantlami_qdbnmx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps, muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 189,
            restAfterSet: 120,
            sets: [
              { repetitions: 12, weight: 25 },
              { repetitions: 11, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.lyingFrenchPress,

            name: 'Wyciskanie francuskie sztangi leżąc (Lying Barbell Triceps Extension)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze.\n2. Chwyć sztangę wąskim nachwytem i unieś ją nad czoło, ramiona prosto.\n3. Powoli opuszczaj sztangę w kierunku czoła lub lekko za głowę, kontrolując ruch.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, napinając tricepsy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148319/wyciskanie-francuskie_vjrcgi.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148451/wyciskanie-francuskie_jhm73o.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.triceps, muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.curlBarbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 450,
            restAfterSet: 120,
            sets: [
              { repetitions: 10, weight: 5 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 25 },
            ],
          },
        ],
      },
      {
        dayNumber: 5,
        name: 'Sobota - Cardio + Mobility',
        totalDuration: 7500,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 3600,
            restAfterSet: 300,
            sets: [{ duration: 3600 }],
          },
          {
            originalId: exercisesIds.stationaryBike,

            name: 'Rower stacjonarny (Stationary Bike)',
            description: `Usiądź wygodnie, dopasuj siodełko i rozpocznij jazdę w kontrolowanym tempie.\nĆwiczenie poprawia kondycję oraz wspiera spalanie kalorii.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148299/rowerek-maszyna_ynijed.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148428/rowerek-maszyna_pdhfpi.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.stationaryBike],
            isRepsSet: false,
            duration: 3900,
            restAfterSet: 300,
            sets: [{ duration: 3600 }],
          },
        ],
      },
      {
        dayNumber: 6,
        name: 'Niedziela - Rest',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
    ],
    hashtags: ['split', 'strength', 'conditioning', 'advanced', '7days', 'push', 'pull', 'legs'],
    createdAt: new Date(1759665022798),
    updatedAt: new Date(1759665022798),
    __v: 0,
  },
  {
    _id: '68e3d38ebcafac9774234722',
    name: 'Plan: Full Body Express',
    author: userIds.admin,
    description:
      'Plan Full Body Express to kompleksowy jednodniowy trening ogólnorozwojowy, który został stworzony z myślą o osobach pragnących utrzymać formę, poprawić siłę i kondycję, ale nie mających czasu na codzienne wizyty na siłowni. Ten plan pozwala w ciągu około 60 minut skutecznie zaangażować wszystkie główne partie mięśniowe – nogi, klatkę piersiową, plecy, barki, ramiona oraz mięśnie brzucha.\r\n\r\nZastosowana struktura treningu opiera się na klasycznych ćwiczeniach siłowych wielostawowych, które angażują jednocześnie wiele grup mięśniowych, co zwiększa efektywność treningu. Dzięki temu w krótkim czasie można osiągnąć zauważalne rezultaty w postaci wzrostu siły, poprawy sylwetki i ogólnej wydolności organizmu.',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759760964/training-plans/images/urxwe7zezn3yk84w3mlw.jpg',
    goal: 'Redukcja tkanki tłuszczowej',
    visibility: 'Publiczny',
    advancedLevel: 'Średniozaawansowany',
    likes: 0,
    days: [
      {
        dayNumber: 0,
        name: 'Całe ciało w jeden dzień!',
        totalDuration: 2754,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 600,
            restAfterSet: 30,
            sets: [{ duration: 600 }],
          },
          {
            originalId: exercisesIds.barbellSquat,

            name: 'Przysiad ze sztangą na plecach (Barbell Back Squat)',
            description: `Ustaw sztangę na stojakach na wysokości górnej części klatki piersiowej.\nWejdź pod gryf i połóż go na górnej części pleców lub mięśniach czworobocznych, chwytając szeroko rękami.\nWyprostuj nogi i wyjmij sztangę ze stojaków, cofnij się krok lub dwa.\nStań stabilnie ze stopami na szerokość barków lub nieco szerzej.\nUginając kolana i biodra, schodź w dół jak do zwykłego przysiadu, utrzymując plecy proste i klatkę wypchniętą.\nZejdź, aż uda będą równoległe do podłogi (lub głębiej, jeśli mobilność pozwala), po czym dynamicznie wróć do góry.\nTo jedno z najważniejszych ćwiczeń siłowych, rozwijające nogi, pośladki, a także stabilizację całego ciała.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148296/przysiad-sztanga_natpu5.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148427/przysiad-sztanga_mailtp.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
              muscleGroupsIds.lowerBack,
            ],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 90,
            sets: [
              { repetitions: 10, weight: 35 },
              { repetitions: 10, weight: 0 },
              { repetitions: 10, weight: 40 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellShoulderPress,

            name: 'Wyciskanie hantli nad głowę siedząc (Seated Dumbbell Shoulder Press)',
            description: `1. Usiądź na ławce z oparciem, trzymając hantle na wysokości barków, dłonie skierowane do przodu.\n2. Napnij brzuch i plecy, utrzymując stabilną postawę.\n3. Wypchnij hantle nad głowę, prostując ramiona, a następnie powoli opuść je z powrotem do pozycji wyjściowej.\nĆwiczenie rozwija tricepsy oraz poprawia stabilizację barków i górnej części tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148323/wyciskanie-hantli-nad-glowe-siedzac_ayell2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148454/wyciskanie-hantli-nad-glowe-siedzac_pzfyyi.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.triceps],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 252,
            restAfterSet: 90,
            sets: [
              { repetitions: 8, weight: 20 },
              { repetitions: 8, weight: 15 },
              { repetitions: 8, weight: 20 },
            ],
          },
          {
            originalId: exercisesIds.lyingFrenchPress,

            name: 'Wyciskanie francuskie sztangi leżąc (Lying Barbell Triceps Extension)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze.\n2. Chwyć sztangę wąskim nachwytem i unieś ją nad czoło, ramiona prosto.\n3. Powoli opuszczaj sztangę w kierunku czoła lub lekko za głowę, kontrolując ruch.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, napinając tricepsy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148319/wyciskanie-francuskie_vjrcgi.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148451/wyciskanie-francuskie_jhm73o.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.triceps, muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.curlBarbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 15 },
              { repetitions: 12, weight: 10 },
              { repetitions: 12, weight: 15 },
            ],
          },
          {
            originalId: exercisesIds.barbellRow,

            name: 'Wiosłowanie sztangą w opadzie tułowia (Barbell Row)',
            description: `Stań w lekkim rozkroku, kolana lekko ugnij i pochyl tułów do przodu, utrzymując plecy prosto.\nChwyć sztangę nachwytem na szerokość barków.\nPrzyciągaj sztangę w kierunku dolnej części klatki lub brzucha, łokcie prowadząc blisko tułowia.\nPowoli opuszczaj sztangę do pozycji wyjściowej, kontrolując ciężar.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148317/wioslowanie-sztanga_xoyor0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148447/wioslowanie-sztanga_hbnxfm.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.biceps,
            ],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 312,
            restAfterSet: 90,
            sets: [
              { repetitions: 15, weight: 15 },
              { repetitions: 14, weight: 15 },
              { repetitions: 15, weight: 15 },
            ],
          },
          {
            originalId: exercisesIds.declineCrunch,

            name: 'Skłony tułowia na ławce ujemnej (Decline Crunch)',
            description: `1. Usiądź na ławce ujemnej, stopy zabezpiecz pod podpórkami.\n2. Unieś lekko tułów, napinając mięśnie brzucha, dłonie mogą być za głową lub skrzyżowane na klatce.\n3. Powoli opuść tułów, kontrolując ruch, nie pozwalając, by plecy całkowicie opadły.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148303/spiecia-brzucha-lawka-ujemna_dtuu5c.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148433/spiecia-brzucha-lawka-ujemna_dpfrqk.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs],
            equipment: [equipmentIds.declineBench],
            isRepsSet: true,
            duration: 291,
            restAfterSet: 75,
            sets: [
              { repetitions: 18, weight: 0 },
              { repetitions: 17, weight: null },
              { repetitions: 12, weight: null },
            ],
          },
          {
            originalId: exercisesIds.obliqueMountainClimber,

            name: 'Przyciąganie kolana do łokcia po skosie w podporze (Oblique Mountain Climber)',
            description: `Przyjmij pozycję podporu przodem jak do pompki.\nPrzyciągaj kolano w kierunku przeciwnego łokcia, wykonując szybkie, naprzemienne ruchy nóg.\nUtrzymuj napięcie brzucha i nie pozwól, by biodra unosiły się zbyt wysoko.\nĆwiczenie angażuje mięśnie skośne brzucha, poprawia wytrzymałość i spala dużą ilość kalorii.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148290/przyciaganie-kolan-do-lokci-po-skosie-w-podporze_g0q06w.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148421/przyciaganie-kolan-do-lokci-po-skosie-w-podporze_bnxiwh.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.obliques, muscleGroupsIds.abs],
            equipment: [equipmentIds.trainingMat, equipmentIds.ownWeight],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: null },
              { repetitions: 12, weight: null },
              { repetitions: 12, weight: null },
            ],
          },
          {
            originalId: exercisesIds.facePull,

            name: 'Przyciąganie linki wyciągu do twarzy (Cable Face Pull)',
            description: `Ustaw wyciąg górny na wysokości twarzy i podepnij linę.\nStań stabilnie, chwyć końce liny nachwytem.\nPrzyciągnij linę do twarzy, prowadząc łokcie szeroko na boki i zewnętrznie rotując barki.\nSkup się na pracy tylnych aktonów barków i mięśni między łopatkami, a nie na bicepsach.\nĆwiczenie poprawia stabilność barków, postawę i rozwija górną część pleców.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148293/przyciaganie-linki-wyciagu-do-twarzy_wpjvya.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148422/przyciaganie-linki-wyciagu-do-twarzy_tofsq5.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.upperBack],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 483,
            restAfterSet: 90,
            sets: [
              { repetitions: 13, weight: 40 },
              { repetitions: 12, weight: 40 },
              { repetitions: 8, weight: 50 },
              { repetitions: 8, weight: 50 },
            ],
          },
        ],
      },
    ],
    hashtags: ['siła', 'redukcja', 'sztanga', 'hantle', 'mata'],
    createdAt: new Date(1759760965331),
    updatedAt: new Date(1759760965331),
    __v: 0,
  },
  {
    _id: '68e3d619d99bd6409847916c',
    name: 'Plan: Cardio & Core Blast',
    author: userIds.admin,
    description:
      'Plan Cardio & Core Blast to dynamiczny jednodniowy trening, który łączy elementy intensywnego cardio z ćwiczeniami wzmacniającymi mięśnie brzucha i korpusu. Dzięki dużej intensywności pozwala spalić znaczną ilość kalorii w krótkim czasie, poprawić wydolność organizmu oraz wysmuklić sylwetkę.\r\n\r\nTrening jest oparty na interwałach czasowych (45 sekund pracy / 15 sekund odpoczynku), co sprawia, że ciało cały czas pracuje na wysokim tętnie, a jednocześnie wzmacnia mięśnie odpowiedzialne za stabilizację. Nie wymaga specjalistycznego sprzętu — wystarczy mata i odrobina miejsca.\r\n\r\nDzięki swojej prostocie i uniwersalności plan sprawdzi się zarówno w domu, jak i na siłowni. Możesz go wykonywać 2–3 razy w tygodniu, aby poprawić kondycję i ujędrnić ciało.',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759761851/training-plans/images/t5w4hm1let8zvgse4tu4.jpg',
    goal: 'Poprawa wytrzymałości',
    visibility: 'Publiczny',
    advancedLevel: 'Początkujący',
    likes: 0,
    days: [
      {
        dayNumber: 0,
        name: 'Cardio & Core Blast',
        totalDuration: 2772,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 480,
            restAfterSet: 30,
            sets: [{ duration: 480 }],
          },
          {
            originalId: exercisesIds.stairClimber,

            name: 'Schody treningowe (Stair Climber)',
            description: `Stań na maszynie, chwyć uchwyty dla stabilizacji i rozpocznij rytmiczne wchodzenie po schodach.\nUtrzymuj prostą sylwetkę i napnij pośladki.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148304/stepper_aqzvkj.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148435/stepper_nkvosa.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.cardio,
              muscleGroupsIds.glutes,
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.stairClimber],
            isRepsSet: false,
            duration: 840,
            restAfterSet: 90,
            sets: [{ duration: 600 }, { duration: 30 }, { duration: 30 }],
          },
          {
            originalId: exercisesIds.stationaryBike,

            name: 'Rower stacjonarny (Stationary Bike)',
            description: `Usiądź wygodnie, dopasuj siodełko i rozpocznij jazdę w kontrolowanym tempie.\nĆwiczenie poprawia kondycję oraz wspiera spalanie kalorii.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148299/rowerek-maszyna_ynijed.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148428/rowerek-maszyna_pdhfpi.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.stationaryBike],
            isRepsSet: false,
            duration: 300,
            restAfterSet: 30,
            sets: [{ duration: 300 }],
          },
          {
            originalId: exercisesIds.russianTwist,

            name: 'Skręty tułowia w siadzie (Russian Twist)',
            description: `Usiądź na podłodze, lekko odchyl się do tyłu i unieś stopy nad ziemię.\nTrzymając ręce razem skręcaj tułów na boki, dotykając podłoża lub przenosząc obciążenie.\nĆwiczenie wzmacnia mięśnie skośne brzucha i poprawia stabilizację.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148302/russian-twist_ud40gg.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148432/russian-twist_y5ilgm.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.obliques],
            equipment: [equipmentIds.trainingMat, equipmentIds.ownWeight],
            isRepsSet: true,
            duration: 150,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.machineFly,

            name: 'Rozpiętki na maszynie (Motylki | Machine Fly)',
            description: `1. Usiądź na maszynie, oprzyj plecy o oparcie i ustaw ramiona na wysokości klatki piersiowej.\n2. Chwyć uchwyty i powoli przyciągaj je do siebie, skupiając się na pracy mięśni klatki piersiowej.\n3. Kontrolowanym ruchem powróć do pozycji wyjściowej, nie blokując stawów łokciowych.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148300/rozpietki_ntlqo4.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148430/rozpietki_smjvcr.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.chest, muscleGroupsIds.shoulders],
            equipment: [equipmentIds.butterflyMachine],
            isRepsSet: true,
            duration: 162,
            restAfterSet: 30,
            sets: [
              { repetitions: 10, weight: 50 },
              { repetitions: 12, weight: 50 },
              { repetitions: 12, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.declineCrunch,

            name: 'Skłony tułowia na ławce ujemnej (Decline Crunch)',
            description: `1. Usiądź na ławce ujemnej, stopy zabezpiecz pod podpórkami.\n2. Unieś lekko tułów, napinając mięśnie brzucha, dłonie mogą być za głową lub skrzyżowane na klatce.\n3. Powoli opuść tułów, kontrolując ruch, nie pozwalając, by plecy całkowicie opadły.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148303/spiecia-brzucha-lawka-ujemna_dtuu5c.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148433/spiecia-brzucha-lawka-ujemna_dpfrqk.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs],
            equipment: [equipmentIds.declineBench],
            isRepsSet: true,
            duration: 240,
            restAfterSet: 75,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.bicycleCrunch,

            name: 'Brzuszki rowerowe (Rowerek | Bicycle Crunch)',
            description: `Połóż się na plecach, dłonie trzymaj za głową, nogi uniesione.\nPrzyciągaj na zmianę kolano do przeciwnego łokcia, prostując drugą nogę jak podczas jazdy rowerem.\nRuch wykonuj płynnie, utrzymując stałe napięcie mięśni brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148294/przyciaganie-lokci-do-kolan-lezac-na-plecach_uti2ld.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148424/przyciaganie-lokci-do-kolan-lezac-na-plecach_oyzfbe.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs],
            equipment: [equipmentIds.trainingMat, equipmentIds.ownWeight],
            isRepsSet: true,
            duration: 600,
            restAfterSet: 90,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10 },
              { repetitions: 10 },
            ],
          },
        ],
      },
    ],
    hashtags: ['wytrzymałość', 'mobilność', 'szybkitrening', 'bezsprzetu'],
    createdAt: new Date(1759761852114),
    updatedAt: new Date(1759761852114),
    __v: 0,
  },
  {
    _id: '68e3db17df2e33276e49c3c6',
    name: 'Mocny fundament',
    author: userIds.admin,
    description:
      'Każdy dzień skupia się na innych grupach mięśniowych, co pozwala na maksymalne zaangażowanie i regenerację między treningami.\r\n\r\nPierwszy dzień (Push) rozwija klatkę piersiową, barki i triceps, drugi (Pull) koncentruje się na plecach i bicepsie, a trzeci dzień (Legs & Core) wzmacnia dolne partie i mięśnie stabilizujące.\r\nDzięki równowadze między ćwiczeniami wielostawowymi a izolowanymi, plan poprawia zarówno siłę, jak i wytrzymałość mięśniową.\r\n\r\nTo idealny wybór dla osób trenujących 3 razy w tygodniu, które chcą systematycznie poprawiać formę, nie przeciążając organizmu.',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759763145/training-plans/images/db2jkhwbjeoo9s21duhq.jpg',
    goal: 'Zwiększanie siły',
    visibility: 'Publiczny',
    advancedLevel: 'Średniozaawansowany',
    likes: 0,
    days: [
      {
        dayNumber: 0,
        name: 'Push (klatka, barki, triceps)',
        totalDuration: 1788,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 255,
            restAfterSet: 75,
            sets: [
              { repetitions: 10, weight: 50 },
              { repetitions: 10, weight: 50 },
              { repetitions: 15, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.declineBenchPress,

            name: 'Wyciskanie sztangi na ławce ujemnej (Decline Bench Press)',
            description: `1. Ułóż się na ławce, stopy stabilnie na podłodze.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę w kierunku dolnej części klatki piersiowej, kontrolując ruch.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.\nĆwiczenie rozwija dolne partie klatki piersiowej, angażuje tricepsy i barki.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148328/wyciskanie-sztangi-skosna_tie2wd.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148460/wyciskanie-sztangi-skosna_etfc8d.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.declineBench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 90,
            sets: [
              { repetitions: 10, weight: 40 },
              { repetitions: 10, weight: 40 },
              { repetitions: 10, weight: 40 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 222,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 50 },
              { repetitions: 12, weight: 50 },
              { repetitions: 12, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellInclineBenchPress,

            name: 'Wyciskanie hantli na ławce skośnej (Dumbbell Incline Bench Press)',
            description: `1. Usiądź na ławce ustawionej pod kątem 30-45°, stopy stabilnie na podłodze.\n2. Chwyć hantle i unieś je nad klatkę piersiową, dłonie skierowane do przodu.\n3. Powoli opuszczaj hantle w kierunku górnej części klatki, kontrolując ruch.\n4. Wypchnij hantle z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148324/wyciskanie-hantli-skosna_r44nq0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148456/wyciskanie-hantli-skosna_phljzl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],

            isRepsSet: true,
            duration: 186,
            restAfterSet: 45,
            sets: [
              { repetitions: 12, weight: 90 },
              { repetitions: 10, weight: 80 },
              { repetitions: 10, weight: 70 },
            ],
          },
          {
            originalId: exercisesIds.overheadDumbbellExtension,

            name: 'Wyciskanie francuskie hantli oburącz stojąc (Standing Dumbbell French Press)',
            description: `1. Stań prosto, stopy na szerokość barków, trzymaj hantel obiema rękami nad głową.\n2. Unieś ramiona, aby były wyprostowane.\n3. Powoli opuszczaj hantel za głowę, zginając łokcie i kontrolując ciężar.\n4. Wypchnij hantel z powrotem do pozycji wyjściowej, napinając tricepsy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148322/wyciskanie-hantla-zza-glowy_dttfui.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148453/wyciskanie-hantla-zza-glowy_laqija.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.triceps],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 231,
            restAfterSet: 60,
            sets: [
              { repetitions: 12, weight: 20 },
              { repetitions: 15, weight: 20 },
              { repetitions: 10, weight: 20 },
            ],
          },
          {
            originalId: exercisesIds.lyingFrenchPress,

            name: 'Wyciskanie francuskie sztangi leżąc (Lying Barbell Triceps Extension)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze.\n2. Chwyć sztangę wąskim nachwytem i unieś ją nad czoło, ramiona prosto.\n3. Powoli opuszczaj sztangę w kierunku czoła lub lekko za głowę, kontrolując ruch.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, napinając tricepsy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148319/wyciskanie-francuskie_vjrcgi.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148451/wyciskanie-francuskie_jhm73o.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.triceps, muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.curlBarbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 306,
            restAfterSet: 90,
            sets: [
              { repetitions: 14, weight: 30 },
              { repetitions: 14, weight: 30 },
              { repetitions: 14, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.barbellCurl,

            name: 'Zginanie przedramion ze sztangą stojąc (Standing Barbell Curl)',
            description: `1. Stań prosto, stopy na szerokość barków, chwyć sztangę podchwytem.\n2. Powoli uginaj ramiona w łokciach, unosząc sztangę w kierunku barków.\n3. Kontrolowanym ruchem opuść sztangę do pozycji wyjściowej.\nĆwiczenie wzmacnia bicepsy, angażując obie ręce jednocześnie.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148311/uginanie-ramion-sztanga-lamana_r73kyc.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148443/uginanie-ramion-sztanga-lamana_mmtymf.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 318,
            restAfterSet: 60,
            sets: [
              { repetitions: 12, weight: 20 },
              { repetitions: 16, weight: 30 },
              { repetitions: 18, weight: 90 },
            ],
          },
        ],
      },
      {
        dayNumber: 1,
        name: 'Dzień 2',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 2,
        name: 'Pull (plecy, biceps)',
        totalDuration: 1564,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellCurl,

            name: 'Zginanie przedramion ze sztangą stojąc (Standing Barbell Curl)',
            description: `1. Stań prosto, stopy na szerokość barków, chwyć sztangę podchwytem.\n2. Powoli uginaj ramiona w łokciach, unosząc sztangę w kierunku barków.\n3. Kontrolowanym ruchem opuść sztangę do pozycji wyjściowej.\nĆwiczenie wzmacnia bicepsy, angażując obie ręce jednocześnie.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148311/uginanie-ramion-sztanga-lamana_r73kyc.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148443/uginanie-ramion-sztanga-lamana_mmtymf.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 20 },
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 40 },
            ],
          },
          {
            originalId: exercisesIds.barbellPreacherCurl,

            isRepsSet: true,
            duration: 261,
            restAfterSet: 75,
            sets: [
              { repetitions: 10, weight: 40 },
              { repetitions: 12, weight: 30 },
              { repetitions: 15, weight: 35 },
            ],
          },
          {
            originalId: exercisesIds.cableBicepCurl,

            name: 'Zginanie przedramion z drążkiem wyciągu dolnego stojąc (Cable Curl)',
            description: `Stań przodem do wyciągu dolnego, chwyć uchwyt podchwytem.\nUtrzymując łokcie blisko tułowia, powoli uginaj ramiona, przyciągając uchwyt w kierunku barków.\nKontrolowanym ruchem wróć do pozycji wyjściowej.\nĆwiczenie skutecznie izoluje biceps, zapewnia równomierne napięcie przez cały zakres ruchu i pozwala na bezpieczny trening z obciążeniem.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148314/uginanie-ramion-z-wyciagiem-dolnym_ogmqdd.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148444/uginanie-ramion-z-wyciagiem-dolnym_xutdqj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps],
            equipment: [equipmentIds.atlas],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 90,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.dumbbellRow,

            name: 'Wiosłowanie hantlem jednorącz w opadzie tułowia (Dumbbell One-Arm Row)',
            description: `Stań w lekkim rozkroku, ugnij kolana i pochyl tułów do przodu, trzymając plecy prosto.\nJedną rękę oprzyj o ławkę lub coś innego dla stabilizacji, w drugiej trzymaj hantel.\nPrzyciągaj hantel w kierunku biodra, skupiając się na pracy mięśni pleców i łopatek.\nPowoli opuść hantel do pozycji wyjściowej, kontrolując ruch.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148315/wioslowanie-hantlem-w-podporze_rrih5o.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148446/wioslowanie-hantlem-w-podporze_yw2sz6.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.biceps,
            ],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 252,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 40 },
              { repetitions: 12, weight: 40 },
              { repetitions: 10, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.barbellRow,

            name: 'Wiosłowanie sztangą w opadzie tułowia (Barbell Row)',
            description: `Stań w lekkim rozkroku, kolana lekko ugnij i pochyl tułów do przodu, utrzymując plecy prosto.\nChwyć sztangę nachwytem na szerokość barków.\nPrzyciągaj sztangę w kierunku dolnej części klatki lub brzucha, łokcie prowadząc blisko tułowia.\nPowoli opuszczaj sztangę do pozycji wyjściowej, kontrolując ciężar.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148317/wioslowanie-sztanga_xoyor0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148447/wioslowanie-sztanga_hbnxfm.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.upperBack,
              muscleGroupsIds.lowerBack,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.biceps,
            ],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 283,
            restAfterSet: 80,
            sets: [
              { repetitions: 10, weight: 50 },
              { repetitions: 14, weight: 65 },
              { repetitions: 17, weight: null },
            ],
          },
          {
            originalId: exercisesIds.dumbbellShoulderPress,

            name: 'Wyciskanie hantli nad głowę siedząc (Seated Dumbbell Shoulder Press)',
            description: `1. Usiądź na ławce z oparciem, trzymając hantle na wysokości barków, dłonie skierowane do przodu.\n2. Napnij brzuch i plecy, utrzymując stabilną postawę.\n3. Wypchnij hantle nad głowę, prostując ramiona, a następnie powoli opuść je z powrotem do pozycji wyjściowej.\nĆwiczenie rozwija tricepsy oraz poprawia stabilizację barków i górnej części tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148323/wyciskanie-hantli-nad-glowe-siedzac_ayell2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148454/wyciskanie-hantli-nad-glowe-siedzac_pzfyyi.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.shoulders, muscleGroupsIds.triceps],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 60,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 65 },
            ],
          },
        ],
      },
      {
        dayNumber: 3,
        name: 'Dzień 4',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 4,
        name: 'Legs & Core (nogi i brzuch)',
        totalDuration: 1788,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 255,
            restAfterSet: 75,
            sets: [
              { repetitions: 10, weight: 50 },
              { repetitions: 10, weight: 50 },
              { repetitions: 15, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.declineBenchPress,

            name: 'Wyciskanie sztangi na ławce ujemnej (Decline Bench Press)',
            description: `1. Ułóż się na ławce, stopy stabilnie na podłodze.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę w kierunku dolnej części klatki piersiowej, kontrolując ruch.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.\nĆwiczenie rozwija dolne partie klatki piersiowej, angażuje tricepsy i barki.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148328/wyciskanie-sztangi-skosna_tie2wd.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148460/wyciskanie-sztangi-skosna_etfc8d.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.declineBench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 90,
            sets: [
              { repetitions: 10, weight: 40 },
              { repetitions: 10, weight: 40 },
              { repetitions: 10, weight: 40 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 222,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 50 },
              { repetitions: 12, weight: 50 },
              { repetitions: 12, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellInclineBenchPress,

            name: 'Wyciskanie hantli na ławce skośnej (Dumbbell Incline Bench Press)',
            description: `1. Usiądź na ławce ustawionej pod kątem 30-45°, stopy stabilnie na podłodze.\n2. Chwyć hantle i unieś je nad klatkę piersiową, dłonie skierowane do przodu.\n3. Powoli opuszczaj hantle w kierunku górnej części klatki, kontrolując ruch.\n4. Wypchnij hantle z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148324/wyciskanie-hantli-skosna_r44nq0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148456/wyciskanie-hantli-skosna_phljzl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.dumbbells],

            isRepsSet: true,
            duration: 186,
            restAfterSet: 45,
            sets: [
              { repetitions: 12, weight: 90 },
              { repetitions: 10, weight: 80 },
              { repetitions: 10, weight: 70 },
            ],
          },
          {
            originalId: exercisesIds.overheadDumbbellExtension,

            name: 'Wyciskanie francuskie hantli oburącz stojąc (Standing Dumbbell French Press)',
            description: `1. Stań prosto, stopy na szerokość barków, trzymaj hantel obiema rękami nad głową.\n2. Unieś ramiona, aby były wyprostowane.\n3. Powoli opuszczaj hantel za głowę, zginając łokcie i kontrolując ciężar.\n4. Wypchnij hantel z powrotem do pozycji wyjściowej, napinając tricepsy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148322/wyciskanie-hantla-zza-glowy_dttfui.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148453/wyciskanie-hantla-zza-glowy_laqija.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.triceps],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 231,
            restAfterSet: 60,
            sets: [
              { repetitions: 12, weight: 20 },
              { repetitions: 15, weight: 20 },
              { repetitions: 10, weight: 20 },
            ],
          },
          {
            originalId: exercisesIds.lyingFrenchPress,

            name: 'Wyciskanie francuskie sztangi leżąc (Lying Barbell Triceps Extension)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze.\n2. Chwyć sztangę wąskim nachwytem i unieś ją nad czoło, ramiona prosto.\n3. Powoli opuszczaj sztangę w kierunku czoła lub lekko za głowę, kontrolując ruch.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, napinając tricepsy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148319/wyciskanie-francuskie_vjrcgi.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148451/wyciskanie-francuskie_jhm73o.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.triceps, muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.curlBarbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 306,
            restAfterSet: 90,
            sets: [
              { repetitions: 14, weight: 30 },
              { repetitions: 14, weight: 30 },
              { repetitions: 14, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.barbellCurl,

            name: 'Zginanie przedramion ze sztangą stojąc (Standing Barbell Curl)',
            description: `1. Stań prosto, stopy na szerokość barków, chwyć sztangę podchwytem.\n2. Powoli uginaj ramiona w łokciach, unosząc sztangę w kierunku barków.\n3. Kontrolowanym ruchem opuść sztangę do pozycji wyjściowej.\nĆwiczenie wzmacnia bicepsy, angażując obie ręce jednocześnie.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148311/uginanie-ramion-sztanga-lamana_r73kyc.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148443/uginanie-ramion-sztanga-lamana_mmtymf.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.biceps],
            equipment: [equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 318,
            restAfterSet: 60,
            sets: [
              { repetitions: 12, weight: 20 },
              { repetitions: 16, weight: 30 },
              { repetitions: 18, weight: 90 },
            ],
          },
        ],
      },
    ],
    hashtags: ['postura', 'mięśnie', 'stabilność', '3dni'],
    createdAt: new Date(1759763146449),
    updatedAt: new Date(1759763146449),
    __v: 0,
  },
  {
    _id: '68e3f459d7b7908f67ed93e5',
    name: 'Silna Ewolucja (Program 4-tygodniowy)',
    author: userIds.admin,
    description:
      'Plan Silna Ewolucja to czterotygodniowy program treningowy, który wprowadza użytkownika w systematyczny rozwój siły, sylwetki i kondycji.\r\nPierwszy tydzień to adaptacja i nauka prawidłowej techniki, drugi to zwiększanie objętości, trzeci to maksymalizacja efektów, a czwarty – regeneracja i przygotowanie do kolejnego cyklu.\r\n\r\nZastosowany podział Push/Pull/Legs pozwala na równomierne rozwijanie wszystkich partii mięśniowych, a dzień Cardio & Core wspomaga spalanie tłuszczu i wzmacnia mięśnie stabilizujące.\r\nPlan łączy elementy siłowe, funkcjonalne i kondycyjne, dzięki czemu zapewnia zrównoważony rozwój i lepsze samopoczucie.',
    image:
      'https://res.cloudinary.com/dtglpgvzi/image/upload/v1759769514/training-plans/images/jowaftf0gt4lgpzg2jri.jpg',
    goal: 'Poprawa gibkości',
    visibility: 'Publiczny',
    advancedLevel: 'Średniozaawansowany',
    likes: 0,
    days: [
      {
        dayNumber: 0,
        name: '🔥 MOCNY START 🔥',
        totalDuration: 1857,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 333,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
        ],
      },
      {
        dayNumber: 1,
        name: 'Basen',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 2,
        name: 'Cardio',
        totalDuration: 3630,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 3630,
            restAfterSet: 30,
            sets: [{ duration: 3600 }],
          },
        ],
      },
      {
        dayNumber: 3,
        name: 'Relaks',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 4,
        name: 'Dzień masażu',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 5,
        name: 'Trening nóg',
        totalDuration: 2262,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: 6,
        name: 'Barki i biceps',
        totalDuration: 2577,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.cableCrunch,

            name: 'Spięcia brzucha klęcząc przy użyciu linki wyciągu górnego (Allachy | Cable Crunch)',
            description: `1. Ustaw wyciąg górny z liną lub drążkiem.\n2. Uklęknij przodem do maszyny, chwyć linę oburącz i przyciągnij ją do głowy lub karku.\n3. Z pozycji wyprostowanej zacznij wykonywać skłon tułowia w dół, napinając mięśnie brzucha.\n4. Utrzymuj napięcie w całym zakresie ruchu, nie ciągnij ciężaru rękami - pracują głównie mięśnie brzucha.\n5. Powoli wróć do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie angażuje mięśnie proste brzucha, wspomaga ich wzmacnianie i rozwój, a dzięki obciążeniu pozwala na progresję siły i masy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/allachy_srovmy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148396/allachy_pftlvh.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs],
            equipment: [equipmentIds.atlas, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 375,
            restAfterSet: 90,
            sets: [
              { repetitions: 15, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 8, weight: 50 },
            ],
          },
        ],
      },
      {
        dayNumber: 7,
        name: 'Relaks',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 8,
        name: 'Cardio',
        totalDuration: 3630,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 3630,
            restAfterSet: 30,
            sets: [{ duration: 3600 }],
          },
        ],
      },
      {
        dayNumber: 9,
        name: 'Dzień masażu',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 10,
        name: 'Dzień rehabilitacji',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 11,
        name: 'Nogi i łydki',
        totalDuration: 2262,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: 12,
        name: 'Cardio',
        totalDuration: 3630,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 3630,
            restAfterSet: 30,
            sets: [{ duration: 3600 }],
          },
        ],
      },
      {
        dayNumber: 13,
        name: 'Ramiona',
        totalDuration: 1857,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 333,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
        ],
      },
      {
        dayNumber: 14,
        name: 'Dzień 15',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 15,
        name: 'Brzuch',
        totalDuration: 2262,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: 16,
        name: 'Dzień 17',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 17,
        name: 'Cardio',
        totalDuration: 7230,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 7230,
            restAfterSet: 30,
            sets: [{ duration: 7200 }],
          },
        ],
      },
      {
        dayNumber: 18,
        name: 'Dzień 19',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 19,
        name: 'Nogi',
        totalDuration: 2262,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: 20,
        name: 'Dzień 21',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 21,
        name: 'Brzuch',
        totalDuration: 2577,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
          {
            originalId: exercisesIds.cableCrunch,

            name: 'Spięcia brzucha klęcząc przy użyciu linki wyciągu górnego (Allachy | Cable Crunch)',
            description: `1. Ustaw wyciąg górny z liną lub drążkiem.\n2. Uklęknij przodem do maszyny, chwyć linę oburącz i przyciągnij ją do głowy lub karku.\n3. Z pozycji wyprostowanej zacznij wykonywać skłon tułowia w dół, napinając mięśnie brzucha.\n4. Utrzymuj napięcie w całym zakresie ruchu, nie ciągnij ciężaru rękami - pracują głównie mięśnie brzucha.\n5. Powoli wróć do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie angażuje mięśnie proste brzucha, wspomaga ich wzmacnianie i rozwój, a dzięki obciążeniu pozwala na progresję siły i masy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/allachy_srovmy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148396/allachy_pftlvh.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs],
            equipment: [equipmentIds.atlas, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 375,
            restAfterSet: 90,
            sets: [
              { repetitions: 15, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 8, weight: 50 },
            ],
          },
        ],
      },
      {
        dayNumber: 22,
        name: 'Barki',
        totalDuration: 2262,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.barbellWristCurl,

            name: 'Uginanie nadgarstków ze sztangą siedząc (Barbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramiona na udach lub ławce, trzymając sztangę podchwytem.\n2. Powoli unoś nadgarstki w kierunku ciała, kontrolując ciężar sztangi.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść sztangę do pozycji wyjściowej, zachowując kontrolę nad ruchem.\nĆwiczenie wzmacnia mięśnie zginacze nadgarstka, poprawia siłę uchwytu i stabilizację przedramion.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148339/zginanie-nadgarstkow-sztanga-siedzac_zyk3k3.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148471/zginanie-nadgarstkow-sztanga-siedzac_hkbrhx.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 288,
            restAfterSet: 90,
            sets: [
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
              { repetitions: 12, weight: 30 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellSeatedWristCurl,

            name: 'Uginanie nadgarstka z hantlem siedząc (Seated Dumbbell Wrist Curl)',
            description: `1. Usiądź na ławce, oprzyj przedramię jednej ręki na udzie tak, aby dłoń była skierowana do wewnątrz kolana, trzymając hantel nachwytem.\n2. Powoli unieś nadgarstek, prostując go w górę, kontrolując ruch.\n3. Utrzymaj napięcie w górnej pozycji przez chwilę.\n4. Powoli opuść nadgarstek do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148337/zginanie-nadgarstkow-hantlem-siedzac_ebv2b2.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148470/zginanie-nadgarstkow-hantlem-siedzac_gnh1mj.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.forearms],
            equipment: [equipmentIds.dumbbells, equipmentIds.bench],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.parallelBarLegRaise,

            name: 'Wznosy nóg na poręczach (Parallel Bar Leg Raise)',
            description: `1. Chwyć poręcze i oprzyj przedramiona na podpórkach, utrzymując ciało stabilnie.\n2. Napnij mięśnie brzucha i unoś nogi przed siebie, trzymając je prosto lub lekko ugięte w kolanach.\n3. Utrzymaj napięcie przez chwilę w górnej pozycji.\n4. Powoli opuść nogi do pozycji wyjściowej, kontrolując ruch.\nĆwiczenie wzmacnia dolne partie brzucha.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148335/wznosy-nog-wiszac_quj2h0.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148466/wznosy-nog-wiszac_pey6cg.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.hipFlexors],
            equipment: [equipmentIds.dipHandlebars],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.dumbbellLateralRaise,

            name: 'Unoszenie hantli bokiem (Dumbbell Lateral Raise)',
            description: `Stań prosto, stopy na szerokość barków, hantle trzymaj wzdłuż tułowia, dłonie skierowane do środka.\nLekko ugnij łokcie i unosząc ramiona na boki, doprowadź je do wysokości barków.\nKontrolowanym ruchem opuść hantle do pozycji wyjściowej.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148334/wznosy-hantlami_cjyc88.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wznosy-hantlami_vj63wp.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.shoulders],
            equipment: [equipmentIds.dumbbells],
            isRepsSet: true,
            duration: 204,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 30 },
              { repetitions: 10, weight: 35 },
              { repetitions: 8, weight: 50 },
            ],
          },
          {
            originalId: exercisesIds.machineLegExtension,

            name: 'Wyprosty kolan na maszynie siedząc (Leg Extension)',
            description: `Usiądź na maszynie, oprzyj plecy o oparcie i ustaw wałki na wysokości kostek.\nChwyć uchwyty dla stabilizacji i napnij mięśnie ud.\nPowoli prostuj nogi w kolanach, maksymalnie napinając mięśnie czworogłowe.\nKontrolowanym ruchem opuść nogi do pozycji wyjściowej.\nĆwiczenie izoluje mięśnie przedniej części uda.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148331/wyprosty-nog-na-maszynie_ztruqy.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148464/wyprosty-nog-na-maszynie_b3b0sz.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.quads],
            equipment: [equipmentIds.legExtensionMachine],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.lunge,

            name: 'Wykroki z kettlem (Kettlebell Lunge)',
            description: `Stań prosto, trzymaj kettle oburącz.\nZrób duży krok do przodu i ugnij kolana pod kątem około 90°, kontrolując równowagę.\n Wróć do pozycji wyjściowej i powtórz na drugą nogę.\nĆwiczenie wzmacnia uda, pośladki i stabilizację tułowia.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148330/wykroki_ctsc05.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148462/wykroki_dczxib.webm',
            advancedLevel: AdvancedLevel.medium,
            muscleGroup: [
              muscleGroupsIds.quads,
              muscleGroupsIds.hamstrings,
              muscleGroupsIds.glutes,
              muscleGroupsIds.calves,
            ],
            equipment: [equipmentIds.kettlebell],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.benchPress,

            name: 'Wyciskanie sztangi na ławce płaskiej (Bench Press)',
            description: `1. Połóż się na ławce płaskiej, stopy stabilnie na podłodze, plecy lekko napięte.\n2. Chwyć sztangę nachwytem nieco szerzej niż szerokość barków.\n3. Powoli opuszczaj sztangę do klatki piersiowej, kontrolując ciężar.\n4. Wypchnij sztangę z powrotem do pozycji wyjściowej, prostując ramiona.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148327/wyciskanie-sztangi-plaska_awtoau.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148459/wyciskanie-sztangi-plaska_f3zyle.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.shoulders,
              muscleGroupsIds.triceps,
            ],
            equipment: [equipmentIds.bench, equipmentIds.barbell, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 258,
            restAfterSet: 75,
            sets: [
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
              { repetitions: 12, weight: 60 },
            ],
          },
          {
            originalId: exercisesIds.plank,

            name: 'Deska (Plank)',
            description: `1. Przyjmij pozycję podporu przodem, opierając się na przedramionach i palcach stóp.\n2. Ustaw łokcie pod barkami, a ciało trzymaj w jednej linii - od głowy po pięty.\n3. Napnij mięśnie brzucha i pośladków, nie unoś bioder zbyt wysoko ani nie opuszczaj ich w dół.\n4. Oddychaj spokojnie i utrzymuj pozycję tak długo, jak dasz radę.\nTo jedno z najlepszych ćwiczeń izometrycznych wzmacniających cały korpus.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148269/deska_wbao3x.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148398/deska_ww9o3r.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.abs, muscleGroupsIds.lowerBack],
            equipment: [equipmentIds.ownWeight, equipmentIds.trainingMat],
            isRepsSet: true,
            duration: 210,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
              { repetitions: 10, weight: 10 },
            ],
          },
          {
            originalId: exercisesIds.hammerChestPressMachine,

            name: 'Wyciskanie na maszynie Hammer (Hammer Chest Press)',
            description:
              'Ćwiczenie na maszynie Hammer, które izoluje klatkę piersiową i pozwala na bezpieczne budowanie siły oraz masy dzięki stabilnemu torowi ruchu.`',
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148271/hamer-maszyna_q5ause.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148401/hamer-maszyna_lykjnl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [
              muscleGroupsIds.chest,
              muscleGroupsIds.triceps,
              muscleGroupsIds.shoulders,
            ],
            equipment: [equipmentIds.hammerMachine, equipmentIds.weightPlates],
            isRepsSet: true,
            duration: 270,
            restAfterSet: 60,
            sets: [
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
              { repetitions: 10, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: 23,
        name: 'Dzień rehabilitacji',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 24,
        name: 'Plecy',
        totalDuration: 3630,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 3630,
            restAfterSet: 30,
            sets: [{ duration: 3600 }],
          },
        ],
      },
      {
        dayNumber: 25,
        name: 'Cardio',
        totalDuration: 3630,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: false,
            duration: 3630,
            restAfterSet: 30,
            sets: [{ duration: 3600 }],
          },
        ],
      },
      {
        dayNumber: 26,
        name: 'Dzień masażu',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 27,
        name: 'Relaks',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: 28,
        name: 'Cardio',
        totalDuration: 330,
        isRestDay: false,
        exercises: [
          {
            originalId: exercisesIds.treadmilRunning,

            name: 'Bieg na bieżni (Treadmill Running)',
            description: `1. Stań na bieżni i ustaw odpowiednią prędkość oraz nachylenie.\n2. Zacznij od spokojnego marszu lub truchtu, aby się rozgrzać.\n3. Przejdź do biegu w wybranym tempie, utrzymując wyprostowaną sylwetkę i naturalny krok.\n4. Patrz przed siebie, nie trzymając się kurczowo poręczy - ręce powinny pracować swobodnie.\n5. Po zakończonym biegu stopniowo zmniejszaj prędkość, aby przejść do marszu i schłodzić organizm.\nĆwiczenie poprawia wydolność, spala kalorie i wzmacnia układ krążeniowo-oddechowy.`,
            image:
              'https://res.cloudinary.com/dtglpgvzi/image/upload/v1756148268/bieznia_kxsx7k.webp',
            video:
              'https://res.cloudinary.com/dtglpgvzi/video/upload/v1756148397/bieznia_wg53rl.webm',
            advancedLevel: AdvancedLevel.easy,
            muscleGroup: [muscleGroupsIds.cardio],
            equipment: [equipmentIds.treadmill],
            isRepsSet: true,
            duration: 330,
            restAfterSet: 30,
            sets: [{ repetitions: 100, weight: null }],
          },
        ],
      },
      {
        dayNumber: 29,
        name: 'Wolne',
        totalDuration: 0,
        isRestDay: true,
        exercises: [],
      },
    ],
    hashtags: ['30dni', 'rozwojciala', 'redukcjastresu', 'gibkośc', '4tygodnie'],
    createdAt: new Date(1759769515232),
    updatedAt: new Date(1759769515232),
    __v: 0,
  },
];
