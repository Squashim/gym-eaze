import { Types } from "mongoose";
import Equipment from "../../models/equipmentModel";

export const seedEquipment = async () => {
  await Equipment.deleteMany();
  await Equipment.insertMany(equipments);
  console.log("Sprzęt dodany");
};

const equipments = [
  {
    _id: new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e1"),
    name: "Hantle",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e2"),
    name: "Sztanga",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e3"),
    name: "Kettlebell",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e4"),
    name: "Skakanka",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("64b0f3a0c8e4c1a2b3c4d5e5"),
    name: "Mata do ćwiczeń",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882570d94e4dd7a359cc9b4"),
    name: "Piłka fitness",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882577a998fe6143adc8184"),
    name: "Ławka treningowa",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257902366ffc765818f7e"),
    name: "Drążek do podciągania",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257978eb36c89bc777470"),
    name: "Taśmy oporowe",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882579f35394654be351566"),
    name: "Rowerek treningowy",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257a746ddfe6b5cfef6f7"),
    name: "Bieżnia",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257b0dbf1aebd599a3965"),
    name: "Stepper",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257b7f0c6e0e120dce1d6"),
    name: "Orbitrek",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257c4add110bebb41b404"),
    name: "Maszyna do nóg",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257cfca441feefc11676f"),
    name: "Maszyna do pleców",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257d998929c84c5360186"),
    name: "Wyciąg górny",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257e34d943080526501fe"),
    name: "Wyciąg dolny",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257ea34b5c5790322e6cb"),
    name: "Poręcze do dipów",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257f0f7825140ccce8151"),
    name: "Platforma do podskoków",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688257fbd49c52bd3868c986"),
    name: "Piłka lekarska",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("68825800526c224ecef3b125"),
    name: "Roller do masażu",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882585760ddcac43e4d5e53"),
    name: "Kamizelka obciążeniowa",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882586131b42cbdc8caade7"),
    name: "Gumy treningowe",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882586879047006eb882c04"),
    name: "Trampolina fitness",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882586ff22b43c0cba2946f"),
    name: "Półkula balansująca (BOSU)",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("68825877452042e54d471a5e"),
    name: "Drabinka gimnastyczna",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882587e36e3dcbb7d236898"),
    name: "Uchwyty do pompek",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882588753eb88e47a1f6359"),
    name: "Piłka do pilatesu",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("6882588fea1fb71b76cb9e84"),
    name: "Obciążniki na kostki",
    image: "https://ibb.co/HLnZzkkC",
  },
  {
    _id: new Types.ObjectId("688258966ad0e7bc6d4bfca4"),
    name: "Pas kulturystyczny",
    image: "https://ibb.co/HLnZzkkC",
  },
];
