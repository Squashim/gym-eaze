import { AdvancedLevel } from '../../constants/advancedLevel';
import { Goal } from '../../constants/goals';
import { PrivacySettings } from '../../constants/privacySettings';
import TrainingPlan from '../../models/trainingPlanModel';
import { userIds } from './userData';

export const seedTrainingPlans = async () => {
  await TrainingPlan.deleteMany();
  await TrainingPlan.insertMany(trainingPlans);
  console.log('Plany treningowe dodane');
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
            id: { $oid: '68ab19d2e8aab2de4bf3742c' },
            isRepsSet: true,
            duration: { $numberInt: '264' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '65' } },
            ],
          },
          {
            id: { $oid: '68ab19c54f5c3f4cc45c6c26' },
            isRepsSet: true,
            duration: { $numberInt: '246' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '6' }, weight: { $numberInt: '45' } },
            ],
          },
          {
            id: { $oid: '68ab19c1878213b40be90134' },
            isRepsSet: false,
            duration: { $numberInt: '165' },
            restAfterSet: { $numberInt: '30' },
            sets: [{ duration: { $numberInt: '60' } }, { duration: { $numberInt: '45' } }],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '1' },
        name: 'Wtorek - Regeneracja',
        totalDuration: { $numberInt: '0' },
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: { $numberInt: '2' },
        name: 'Środa - Push Day',
        totalDuration: { $numberInt: '504' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19b42b29905506116f0e' },
            isRepsSet: true,
            duration: { $numberInt: '246' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '6' }, weight: { $numberInt: '35' } },
            ],
          },
          {
            id: { $oid: '68ab18ea1256ce2083b8a5e8' },
            isRepsSet: true,
            duration: { $numberInt: '258' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '8' }, weight: null },
              { repetitions: { $numberInt: '8' }, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '3' },
        name: 'Czwartek - Rest',
        totalDuration: { $numberInt: '0' },
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: { $numberInt: '4' },
        name: 'Piątek - Pull Day',
        totalDuration: { $numberInt: '609' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab1915b8979af11c4c87d2' },
            isRepsSet: true,
            duration: { $numberInt: '285' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '5' }, weight: { $numberInt: '80' } },
              { repetitions: { $numberInt: '5' }, weight: { $numberInt: '80' } },
              { repetitions: { $numberInt: '5' }, weight: { $numberInt: '90' } },
            ],
          },
          {
            id: { $oid: '68ab193a91ae213d21c2c7ef' },
            isRepsSet: true,
            duration: { $numberInt: '324' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '8' }, weight: null },
              { repetitions: { $numberInt: '4' }, weight: null },
              { repetitions: { $numberInt: '6' }, weight: null },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '5' },
        name: 'Sobota - Rest',
        totalDuration: { $numberInt: '0' },
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: { $numberInt: '6' },
        name: 'Niedziela - Full Body (Light)',
        totalDuration: { $numberInt: '390' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19a4e8989dab314f0737' },
            isRepsSet: true,
            duration: { $numberInt: '102' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
            ],
          },
          {
            id: { $oid: '68ab19968d2327f0f1255103' },
            isRepsSet: true,
            duration: { $numberInt: '288' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '14' }, weight: null },
              { repetitions: { $numberInt: '12' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
        ],
      },
    ],
    hashtags: ['fullbody', 'strength', 'hypertrophy'],
    createdAt: { $date: { $numberLong: '1759616818984' } },
    updatedAt: { $date: { $numberLong: '1759616818984' } },
    __v: { $numberInt: '0' },
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
    likes: { $numberInt: '0' },
    days: [
      {
        dayNumber: { $numberInt: '0' },
        name: 'Poniedziałek - Push (Klatka, Barki, Triceps)',
        totalDuration: { $numberInt: '5712' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19e35706ebf1c5905f16' },
            isRepsSet: true,
            duration: { $numberInt: '1365' },
            restAfterSet: { $numberInt: '300' },
            sets: [
              { repetitions: { $numberInt: '11' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '65' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '70' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '70' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '75' } },
            ],
          },
          {
            id: { $oid: '68ab19d756fb7610fe11ebb9' },
            isRepsSet: true,
            duration: { $numberInt: '708' },
            restAfterSet: { $numberInt: '180' },
            sets: [
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab19ce525ddeeeda3a79a2' },
            isRepsSet: true,
            duration: { $numberInt: '600' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '10' } },
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '10' } },
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '10' } },
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '10' } },
            ],
          },
          {
            id: { $oid: '68ab19b42b29905506116f0e' },
            isRepsSet: true,
            duration: { $numberInt: '348' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
            ],
          },
          {
            id: { $oid: '68ab19d2e8aab2de4bf3742c' },
            isRepsSet: true,
            duration: { $numberInt: '255' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab19bb91f2287b4dc4f516' },
            isRepsSet: true,
            duration: { $numberInt: '456' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab199ad97fa081b81d082b' },
            isRepsSet: true,
            duration: { $numberInt: '720' },
            restAfterSet: { $numberInt: '180' },
            sets: [
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '35' } },
            ],
          },
          {
            id: { $oid: '68ab19212b1d30dd1c86e1af' },
            isRepsSet: true,
            duration: { $numberInt: '1260' },
            restAfterSet: { $numberInt: '180' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '15' } },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '1' },
        name: 'Wtorek - Pull (Plecy, Biceps)',
        totalDuration: { $numberInt: '3072' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19465728480da57d7761' },
            isRepsSet: true,
            duration: { $numberInt: '480' },
            restAfterSet: { $numberInt: '180' },
            sets: [
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '45' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '50' } },
            ],
          },
          {
            id: { $oid: '68ab193659639ceff45d2af8' },
            isRepsSet: true,
            duration: { $numberInt: '945' },
            restAfterSet: { $numberInt: '180' },
            sets: [
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '5' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '5' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '5' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '5' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '5' } },
            ],
          },
          {
            id: { $oid: '68ab192e79afe422ffd531c1' },
            isRepsSet: true,
            duration: { $numberInt: '336' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '40' } },
            ],
          },
          {
            id: { $oid: '68ab191a5eacd768dccee31a' },
            isRepsSet: true,
            duration: { $numberInt: '495' },
            restAfterSet: { $numberInt: '180' },
            sets: [
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '2' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '2' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '2' } },
            ],
          },
          {
            id: { $oid: '68ab18fa98741a6075456334' },
            isRepsSet: true,
            duration: { $numberInt: '204' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '25' } },
            ],
          },
          {
            id: { $oid: '68ab18f2433bb6ec10b6e936' },
            isRepsSet: true,
            duration: { $numberInt: '612' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '40' } },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '2' },
        name: 'Środa - Legs (Nogi)',
        totalDuration: { $numberInt: '2133' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19f1fdceb70c0de087c7' },
            isRepsSet: true,
            duration: { $numberInt: '600' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '20' }, weight: { $numberInt: '60' } },
            ],
          },
          {
            id: { $oid: '68ab19ea2bcdfa35c6d27ee5' },
            isRepsSet: true,
            duration: { $numberInt: '366' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '20' } },
            ],
          },
          {
            id: { $oid: '68ab19c54f5c3f4cc45c6c26' },
            isRepsSet: true,
            duration: { $numberInt: '672' },
            restAfterSet: { $numberInt: '180' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '15' } },
            ],
          },
          {
            id: { $oid: '68ab19a891ecc594b230b2af' },
            isRepsSet: true,
            duration: { $numberInt: '495' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '60' } },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '3' },
        name: 'Czwartek - Rest',
        totalDuration: { $numberInt: '0' },
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: { $numberInt: '4' },
        name: 'Piątek - Full Body Conditioning (15 ćwiczeń!)',
        totalDuration: { $numberInt: '10086' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab18f2433bb6ec10b6e936' },
            isRepsSet: true,
            duration: { $numberInt: '210' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
            ],
          },
          {
            id: { $oid: '68ab18ef3e0ffdb8b2623499' },
            isRepsSet: true,
            duration: { $numberInt: '222' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '0' } },
              { repetitions: { $numberInt: '12' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab18ea1256ce2083b8a5e8' },
            isRepsSet: true,
            duration: { $numberInt: '210' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab18e523c64093963755e2' },
            isRepsSet: false,
            duration: { $numberInt: '7380' },
            restAfterSet: { $numberInt: '180' },
            sets: [{ duration: { $numberInt: '3600' } }, { duration: { $numberInt: '3600' } }],
          },
          {
            id: { $oid: '68ab18e1d25feb03854d21b1' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab191a5eacd768dccee31a' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab1915b8979af11c4c87d2' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab1910cf9236df9bb4177a' },
            isRepsSet: true,
            duration: { $numberInt: '330' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '25' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab190b43128610f148b7a7' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab1907a571916292f16fef' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab1902057bb86a4f6c76ee' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab18fe0916eda607cadac6' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab18fa98741a6075456334' },
            isRepsSet: true,
            duration: { $numberInt: '45' },
            restAfterSet: { $numberInt: '60' },
            sets: [{ repetitions: { $numberInt: '15' }, weight: { $numberInt: '30' } }],
          },
          {
            id: { $oid: '68ab18f7510d4280872ff1c3' },
            isRepsSet: true,
            duration: { $numberInt: '189' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '25' } },
              { repetitions: { $numberInt: '11' }, weight: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab19c84dbe0394756e6d1e' },
            isRepsSet: true,
            duration: { $numberInt: '450' },
            restAfterSet: { $numberInt: '120' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '5' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '10' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '25' } },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '5' },
        name: 'Sobota - Cardio + Mobility',
        totalDuration: { $numberInt: '7500' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab18e523c64093963755e2' },
            isRepsSet: false,
            duration: { $numberInt: '3600' },
            restAfterSet: { $numberInt: '300' },
            sets: [{ duration: { $numberInt: '3600' } }],
          },
          {
            id: { $oid: '68ab19968d2327f0f1255103' },
            isRepsSet: false,
            duration: { $numberInt: '3900' },
            restAfterSet: { $numberInt: '300' },
            sets: [{ duration: { $numberInt: '3600' } }],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '6' },
        name: 'Niedziela - Rest',
        totalDuration: { $numberInt: '0' },
        isRestDay: true,
        exercises: [],
      },
    ],
    hashtags: ['split', 'strength', 'conditioning', 'advanced', '7days', 'push', 'pull', 'legs'],
    createdAt: { $date: { $numberLong: '1759665022798' } },
    updatedAt: { $date: { $numberLong: '1759665022798' } },
    __v: { $numberInt: '0' },
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
    likes: { $numberInt: '0' },
    days: [
      {
        dayNumber: { $numberInt: '0' },
        name: 'Całe ciało w jeden dzień!',
        totalDuration: { $numberInt: '2754' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab18e523c64093963755e2' },
            isRepsSet: false,
            duration: { $numberInt: '600' },
            restAfterSet: { $numberInt: '30' },
            sets: [{ duration: { $numberInt: '600' } }],
          },
          {
            id: { $oid: '68ab199219e28211a4a9eaa1' },
            isRepsSet: true,
            duration: { $numberInt: '270' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '35' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '0' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
            ],
          },
          {
            id: { $oid: '68ab19d2e8aab2de4bf3742c' },
            isRepsSet: true,
            duration: { $numberInt: '252' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '20' } },
            ],
          },
          {
            id: { $oid: '68ab19c84dbe0394756e6d1e' },
            isRepsSet: true,
            duration: { $numberInt: '258' },
            restAfterSet: { $numberInt: '75' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '10' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '15' } },
            ],
          },
          {
            id: { $oid: '68ab19c1878213b40be90134' },
            isRepsSet: true,
            duration: { $numberInt: '312' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '15' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '15' } },
            ],
          },
          {
            id: { $oid: '68ab19a14c8b1ec3df206ba1' },
            isRepsSet: true,
            duration: { $numberInt: '291' },
            restAfterSet: { $numberInt: '75' },
            sets: [
              { repetitions: { $numberInt: '18' }, weight: { $numberInt: '0' } },
              { repetitions: { $numberInt: '17' }, weight: null },
              { repetitions: { $numberInt: '12' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab1941f5762bc3a9c931f6' },
            isRepsSet: true,
            duration: { $numberInt: '288' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: null },
              { repetitions: { $numberInt: '12' }, weight: null },
              { repetitions: { $numberInt: '12' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab19465728480da57d7761' },
            isRepsSet: true,
            duration: { $numberInt: '483' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '13' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '8' }, weight: { $numberInt: '50' } },
            ],
          },
        ],
      },
    ],
    hashtags: ['siła', 'redukcja', 'sztanga', 'hantle', 'mata'],
    createdAt: { $date: { $numberLong: '1759760965331' } },
    updatedAt: { $date: { $numberLong: '1759760965331' } },
    __v: { $numberInt: '0' },
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
    likes: { $numberInt: '0' },
    days: [
      {
        dayNumber: { $numberInt: '0' },
        name: 'Cardio & Core Blast',
        totalDuration: { $numberInt: '2772' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab18e523c64093963755e2' },
            isRepsSet: false,
            duration: { $numberInt: '480' },
            restAfterSet: { $numberInt: '30' },
            sets: [{ duration: { $numberInt: '480' } }],
          },
          {
            id: { $oid: '68ab19a4e8989dab314f0737' },
            isRepsSet: false,
            duration: { $numberInt: '840' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { duration: { $numberInt: '600' } },
              { duration: { $numberInt: '30' } },
              { duration: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab19968d2327f0f1255103' },
            isRepsSet: false,
            duration: { $numberInt: '300' },
            restAfterSet: { $numberInt: '30' },
            sets: [{ duration: { $numberInt: '300' } }],
          },
          {
            id: { $oid: '68ab199eedb42c6e6d632360' },
            isRepsSet: true,
            duration: { $numberInt: '150' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab199ad97fa081b81d082b' },
            isRepsSet: true,
            duration: { $numberInt: '162' },
            restAfterSet: { $numberInt: '30' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '50' } },
            ],
          },
          {
            id: { $oid: '68ab19a14c8b1ec3df206ba1' },
            isRepsSet: true,
            duration: { $numberInt: '240' },
            restAfterSet: { $numberInt: '75' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab198a6998bd2935b9ddcb' },
            isRepsSet: true,
            duration: { $numberInt: '600' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' }, weight: null },
              { repetitions: { $numberInt: '10' } },
              { repetitions: { $numberInt: '10' } },
            ],
          },
        ],
      },
    ],
    hashtags: ['wytrzymałość', 'mobilność', 'szybkitrening', 'bezsprzetu'],
    createdAt: { $date: { $numberLong: '1759761852114' } },
    updatedAt: { $date: { $numberLong: '1759761852114' } },
    __v: { $numberInt: '0' },
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
    likes: { $numberInt: '0' },
    days: [
      {
        dayNumber: { $numberInt: '0' },
        name: 'Push (klatka, barki, triceps)',
        totalDuration: { $numberInt: '1788' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19f577b7e7866c704428' },
            isRepsSet: true,
            duration: { $numberInt: '255' },
            restAfterSet: { $numberInt: '75' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '60' } },
            ],
          },
          {
            id: { $oid: '68ab19e6042ded9285eb1469' },
            isRepsSet: true,
            duration: { $numberInt: '270' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
            ],
          },
          {
            id: { $oid: '68ab19e35706ebf1c5905f16' },
            isRepsSet: true,
            duration: { $numberInt: '222' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '50' } },
            ],
          },
          {
            id: { $oid: '68ab19d756fb7610fe11ebb9' },
            isRepsSet: true,
            duration: { $numberInt: '186' },
            restAfterSet: { $numberInt: '45' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '90' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '80' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '70' } },
            ],
          },
          {
            id: { $oid: '68ab19ce525ddeeeda3a79a2' },
            isRepsSet: true,
            duration: { $numberInt: '231' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
            ],
          },
          {
            id: { $oid: '68ab19c84dbe0394756e6d1e' },
            isRepsSet: true,
            duration: { $numberInt: '306' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab19b7a5c30a82fc9f2f70' },
            isRepsSet: true,
            duration: { $numberInt: '318' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '16' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '18' }, weight: { $numberInt: '90' } },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '1' },
        name: 'Dzień 2',
        totalDuration: { $numberInt: '0' },
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: { $numberInt: '2' },
        name: 'Pull (plecy, biceps)',
        totalDuration: { $numberInt: '1564' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19b7a5c30a82fc9f2f70' },
            isRepsSet: true,
            duration: { $numberInt: '210' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
            ],
          },
          {
            id: { $oid: '68ab19b42b29905506116f0e' },
            isRepsSet: true,
            duration: { $numberInt: '261' },
            restAfterSet: { $numberInt: '75' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '35' } },
            ],
          },
          {
            id: { $oid: '68ab19bb91f2287b4dc4f516' },
            isRepsSet: true,
            duration: { $numberInt: '270' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '10' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab19bed0863ada93fddfec' },
            isRepsSet: true,
            duration: { $numberInt: '252' },
            restAfterSet: { $numberInt: '75' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
            ],
          },
          {
            id: { $oid: '68ab19c1878213b40be90134' },
            isRepsSet: true,
            duration: { $numberInt: '283' },
            restAfterSet: { $numberInt: '80' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '65' } },
              { repetitions: { $numberInt: '17' }, weight: null },
            ],
          },
          {
            id: { $oid: '68ab19d2e8aab2de4bf3742c' },
            isRepsSet: true,
            duration: { $numberInt: '288' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '60' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '65' } },
            ],
          },
        ],
      },
      {
        dayNumber: { $numberInt: '3' },
        name: 'Dzień 4',
        totalDuration: { $numberInt: '0' },
        isRestDay: true,
        exercises: [],
      },
      {
        dayNumber: { $numberInt: '4' },
        name: 'Legs & Core (nogi i brzuch)',
        totalDuration: { $numberInt: '1788' },
        isRestDay: false,
        exercises: [
          {
            id: { $oid: '68ab19f577b7e7866c704428' },
            isRepsSet: true,
            duration: { $numberInt: '255' },
            restAfterSet: { $numberInt: '75' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '60' } },
            ],
          },
          {
            id: { $oid: '68ab19e6042ded9285eb1469' },
            isRepsSet: true,
            duration: { $numberInt: '270' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '40' } },
            ],
          },
          {
            id: { $oid: '68ab19e35706ebf1c5905f16' },
            isRepsSet: true,
            duration: { $numberInt: '222' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '50' } },
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '50' } },
            ],
          },
          {
            id: { $oid: '68ab19d756fb7610fe11ebb9' },
            isRepsSet: true,
            duration: { $numberInt: '186' },
            restAfterSet: { $numberInt: '45' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '90' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '80' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '70' } },
            ],
          },
          {
            id: { $oid: '68ab19ce525ddeeeda3a79a2' },
            isRepsSet: true,
            duration: { $numberInt: '231' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '15' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '10' }, weight: { $numberInt: '20' } },
            ],
          },
          {
            id: { $oid: '68ab19c84dbe0394756e6d1e' },
            isRepsSet: true,
            duration: { $numberInt: '306' },
            restAfterSet: { $numberInt: '90' },
            sets: [
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '14' }, weight: { $numberInt: '30' } },
            ],
          },
          {
            id: { $oid: '68ab19b7a5c30a82fc9f2f70' },
            isRepsSet: true,
            duration: { $numberInt: '318' },
            restAfterSet: { $numberInt: '60' },
            sets: [
              { repetitions: { $numberInt: '12' }, weight: { $numberInt: '20' } },
              { repetitions: { $numberInt: '16' }, weight: { $numberInt: '30' } },
              { repetitions: { $numberInt: '18' }, weight: { $numberInt: '90' } },
            ],
          },
        ],
      },
    ],
    hashtags: ['postura', 'mięśnie', 'stabilność', '3dni'],
    createdAt: { $date: { $numberLong: '1759763146449' } },
    updatedAt: { $date: { $numberLong: '1759763146449' } },
    __v: { $numberInt: '0' },
  },
  /*
  {
    _id: "6888e00e3086d991b4fdcd80",
    author: "68875a0b053920a2eca69cbe", //Monika
    title: "Plan na masę",
    description: "Plan treningowy na masę mięśniową, 2 dni w tygodniu",
    image: "https://ibb.co/HLk4MrZj",
    goal: Goal.muscleGain,
    visibility: PrivacySettings.public,
    advancedLevel: AdvancedLevel.easy,
    likes: 2,
    days: [
      {
        day: {
          _id: "6888e0ea984bad83690eae99",
          name: "Poniedziałek",
          restBetweenExercises: 60,
          totalDuration: 800,
          isRestDay: false,
          exercises: [
            {
              exercise: {
                id: "68875afe9d9533738f442263",
                duration: 160,
                sets: [
                  { repetitions: 8, weight: 80, restAfterSet: 80 },
                  { repetitions: 8, weight: 80, restAfterSet: 80 },
                ],
              },
            },
            {
              exercise: {
                id: "68875b48a6d40cbbe47d1d9a",
                duration: 220,
                sets: [
                  { repetitions: 6, weight: 100, restAfterSet: 120 },
                  { repetitions: 6, weight: 100, restAfterSet: 120 },
                ],
              },
            },
          ],
        },
      },
      {
        day: {
          _id: "6888e104bdc36c2d039c9b8e",
          name: "Czwartek",
          restBetweenExercises: 60,
          totalDuration: 640,
          isRestDay: false,
          exercises: [
            {
              exercise: {
                id: "6887c69ea560a11573963786",
                duration: 320,
                sets: [
                  { repetitions: 8, weight: 70, restAfterSet: 90 },
                  { repetitions: 8, weight: 70, restAfterSet: 90 },
                ],
              },
            },
            {
              exercise: {
                id: "6887c6be1f99e50d61923bb2",
                duration: 320,
                sets: [
                  { repetitions: 6, weight: 0, restAfterSet: 120 },
                  { repetitions: 6, weight: 0, restAfterSet: 120 },
                ],
              },
            },
          ],
        },
      },
    ],
    hashtags: ["masa", "trening", "siła", "mięśnie"],
    createdAt: new Date(),
    updatedAt: new Date(),
  },
  */
];
