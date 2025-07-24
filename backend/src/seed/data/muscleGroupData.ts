import MuscleGroup from "../../models/muscleGroup";

export const seedMuscleGroups = async () => {
  await MuscleGroup.deleteMany();
  await MuscleGroup.insertMany(muscleGroups);
  console.log("Grupy mięśniowe dodane");
};

const muscleGroups = [
  {
    _id: "68825a20bd70090c1322c6a9",
    name: "Klatka piersiowa",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825a2739b1644b05f41baa",
    name: "Plecy",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825a2eb5b7ba6fb98cbfb8",
    name: "Barki",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825a370b497082d4918e2b",
    name: "Biceps",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825a3f2f2351ccb33b3dc6",
    name: "Triceps",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825a4660b5b044fd789516",
    name: "Brzuch",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825a812383da02f31ac12a",
    name: "Uda",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825aab333b8f74d2ea55dd",
    name: "Pośladki",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825a9e37a5ab8035bd7d3b",
    name: "Łydki",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825ab10915542d84d4148f",
    name: "Przedramiona",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825ab9658591ac85380bf7",
    name: "Mięśnie głębokie",
    image: "https://ibb.co/Ndhj7WDr",
  },
  {
    _id: "68825abf65fa3aecba2c7960",
    name: "Mięśnie stabilizujące",
    image: "https://ibb.co/Ndhj7WDr",
  },
];
