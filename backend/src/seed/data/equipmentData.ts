import Equipment from "../../models/equipmentModel";
import { ObjectId } from "mongodb";

export const seedEquipment = async () => {
  await Equipment.deleteMany();
  await Equipment.insertMany(equipment);
  console.log("Sprzęt dodany");
};

export const equipmentIds = {
  atlas: new ObjectId("68a855c7fe5b33155436dcc5"), // maszyna atlas
  treadmill: new ObjectId("68a8563482fd72cf3d3d614f"), // bieznia
  ownWeight: new ObjectId("68a8564723f9e644cb3b6471"), // wlasna waga
  pullupBar: new ObjectId("68a85673a020d1938321dce8"), // drazek do podciagania
  resistanceBands: new ObjectId("68a8568303f779a0b07bbe32"), // gumy oporowe
  dumbbells: new ObjectId("68a85690aabafab80fb83dde"), // hantle
  kettlebell: new ObjectId("68a8569f1256d9d922bf09fd"), // ketle
  bench: new ObjectId("68a856aaeb241ba80022e220"), // lawka
  declineBench: new ObjectId("68a856c7edd3cbe25dc20c76"), // lawka ujemna
  jumpRope: new ObjectId("68a856e63dcdc47d469fb499"), // skakanka
  barbell: new ObjectId("68a856f32f5b4acc92c3012d"), // sztanga
  curlBarbell: new ObjectId("68a85701ab854d2ec772c271"), // sztanga lamana
  weightPlates: new ObjectId("68a8571b0e49de23716338d4"), // talerze do sztangi
  legPress: new ObjectId("68a8572955c4ec9e30ada139"), // suwnica
  stairClimber: new ObjectId("68a8573cdbab7cd37d542c8e"), // maszyna do wchodzenia po schodach
  stationaryBike: new ObjectId("68a85c95a061f8791958ddd9"), // rowerek
  medicineBall: new ObjectId("68a85cc3506b2580e716e1a0"), // pilka lekarska
  fitnessBall: new ObjectId("68a85ce2d366174731422010"), // pilka cwiczeniowa
  trainingMat: new ObjectId("68a85d22ef729f3a97824486"), // mata do cwiczen
  hammerMachine: new ObjectId("68a85d307ef473990379d246"), // maszyna hammer
  butterflyMachine: new ObjectId("68a85d5e6781e31e1562b157"), // maszyna do rozpietek
  dipHandlebars: new ObjectId("68a85d6f7788df6e0d9d8eea"), // porecze do dipow
  legExtensionMachine: new ObjectId("68a85d81bd71387f90b03396"), // maszyna do wyprostow nog
  preacherCurlBench: new ObjectId("68a85daac46b494cca5fa638"), // modlitewnik
  abductorMachine: new ObjectId("68a85dde757bbb11f6fec938"), // maszyna do odwodzenia nog
  calfRaiseMachine: new ObjectId("68a85de36be5a85b78415223"), // maszyna na lydki
};

const equipment = [
  {
    _id: equipmentIds.atlas,
    name: "Atlas do ćwiczeń",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147039/atlas_epjcpp.webp",
    imageCredit:
      "https://unsplash.com/photos/interior-of-gym-2mz9IKab7DE?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.treadmill,
    name: "Bieżnia",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147039/bieznia_grhyso.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-with-exercise-equipment-WmfcbtR2uuU?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.ownWeight,
    name: "Własny ciężar ciała",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147040/ciezar-ciala_fyerez.webp",
    imageCredit:
      "https://unsplash.com/photos/a-woman-doing-push-ups-in-a-gym-shr1Zgtt8mI?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.pullupBar,
    name: "Drążek do podciągania",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147041/drazek_qpzpr1.webp",
    imageCredit:
      "https://unsplash.com/photos/man-in-white-crew-neck-t-shirt-and-black-pants-holding-black-exercise-equipment-kVX6MBa2uNk?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.resistanceBands,
    name: "Taśmy oporowe",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147064/tasmy-oporowe_egq5sv.webp",
    imageCredit:
      "https://unsplash.com/photos/shallow-focus-photography-of-yellow-and-black-stretch-rope-rWBBDErPXcY?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.dumbbells,
    name: "Hantle",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147042/hantle_jagwz2.webp",
    imageCredit:
      "https://unsplash.com/photos/a-couple-of-black-dumbs-sitting-on-top-of-each-other-H-qxKCedhcc?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.kettlebell,
    name: "Kettlebell",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147042/kettleball_c0wacc.webp",
    imageCredit:
      "https://unsplash.com/photos/red-and-black-kettle-bell-ugNQPnqhlXw?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.bench,
    name: "Ławka do ćwiczeń",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147043/lawka_uiiypv.webp",
    imageCredit:
      "https://unsplash.com/photos/a-black-bench-sitting-on-top-of-a-gray-floor-A1I0n1dIv9Q?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.declineBench,
    name: "Ławka ujemna",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147044/lawka-ujemna_cigzzm.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-with-a-variety-of-exercise-equipment-aQ2sYG2v5iM?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.jumpRope,
    name: "Skakanka",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147058/skakanka_fqychq.webp",
    imageCredit:
      "https://unsplash.com/photos/person-holding-gray-wire-Pz2Yhpx2ZdQ?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.barbell,
    name: "Sztanga",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147060/sztanga_ptgbfm.webp",
    imageCredit:
      "https://unsplash.com/photos/green-and-black-barbell-on-black-steel-stand-mdroHvBda4g?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.curlBarbell,
    name: "Sztanga łamana",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147062/sztanga-lamana_snlmwy.webp",
    imageCredit:
      "https://unsplash.com/photos/a-black-and-white-photo-of-a-row-of-gym-equipment-gnJqUTCPzzg?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.weightPlates,
    name: "Talerze do sztangi",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147062/talerze_canxvh.webp",
    imageCredit:
      "https://unsplash.com/photos/black-and-silver-dumbbells-on-gray-floor-8APlajFIoco?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.legPress,
    name: "Suwnica",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147059/suwnica_gudst3.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-filled-with-machines-and-weight-plates-rv55xsA8cjI?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.stairClimber,
    name: "Schody treningowe",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147057/schody_o6dne5.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-with-exercise-equipment-R11BDG6k1J8?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.stationaryBike,
    name: "Rower stacjonarny",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147055/rowerek_e37wzw.webp",
    imageCredit:
      "https://unsplash.com/photos/black-stationary-bike-4WcuyZ_f5ns?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.medicineBall,
    name: "Piłka lekarska",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147053/pilka-lekarska_krhheu.webp",
    imageCredit:
      "https://unsplash.com/photos/a-bunch-of-black-and-blue-balls-are-stacked-on-top-of-each-other-0zXAWVP78zI?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.fitnessBall,
    name: "Piłka do ćwiczeń",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147053/pilka-do-cwiczen_qjwkiz.webp",
    imageCredit:
      "https://unsplash.com/photos/gym-equipment-including-boxes-barbell-and-ball-FgrtPE9n6W8?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.trainingMat,
    name: "Mata do ćwiczeń",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147050/mata_o4apj9.webp",
    imageCredit:
      "https://unsplash.com/photos/black-and-white-speakers-on-white-shelf-biX8sBfNcPc?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.hammerMachine,
    name: "Maszyna hammer do wyciskania",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147048/maszyna-hammer_mfair6.webp",
    imageCredit:
      "https://unsplash.com/photos/a-chair-in-a-room-FaHKxm7WBJo?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.butterflyMachine,
    name: "Maszyna do rozpiętek",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147047/maszyna-do-rozpietek_cf3qbk.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-with-a-lot-of-machines-and-windows-K7yzYCEFQ-s?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.dipHandlebars,
    name: "Poręcze do dipów",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147054/porecze-do-dipow_t5g4zn.webp",
    imageCredit:
      "https://unsplash.com/photos/man-in-black-tank-top-wearing-white-cowboy-hat-AdbNssrnq84?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.legExtensionMachine,
    name: "Maszyna do prostowania nóg",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147046/maszyna-do-prostowania-nog_dtgoaz.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-with-a-lot-of-equipment-in-it-DLhXl7A1ds0?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.preacherCurlBench,
    name: "Modlitewnik do ćwiczeń",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147051/modlitewnik_byxofq.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-filled-with-lots-of-machines-and-equipment-v5w5NbV2dmI?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.abductorMachine,
    name: "Maszyna do odwodzenia nóg",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147045/maszyna-do-odwodzenia-nog_baw8qt.webp",
    imageCredit:
      "https://unsplash.com/photos/black-fitness-machine-UoLs0E75ckI?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
  {
    _id: equipmentIds.calfRaiseMachine,
    name: "Maszyna na łydki",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756147049/maszyna-na-lydki_kunrr5.webp",
    imageCredit:
      "https://unsplash.com/photos/a-gym-filled-with-lots-of-machines-and-equipment-R6A3VP5YeXs?utm_content=creditCopyText&utm_medium=referral&utm_source=unsplash",
  },
];

