import MuscleGroup from "../../models/muscleGroup";
import { ObjectId } from "mongodb";

export const seedMuscleGroups = async () => {
  await MuscleGroup.deleteMany();
  await MuscleGroup.insertMany(muscleGroups);
  console.log("Grupy mięśniowe dodane");
};

const muscleGroupsIds = {
  cardio: new ObjectId("68aad35e098f8656a74a8d40"), // cardio
  biceps: new ObjectId("68aad36527462a652be1a09d"), // biceps
  triceps: new ObjectId("68aad36aae944d0d0bbdfe07"), // triceps
  abs: new ObjectId("68aad370a552048bbecc9d26"), // brzuch
  chest: new ObjectId("68aad377e9dc749b96b59133"), // klatka piersiowa
  forearms: new ObjectId("68aad37d71f5903b2b66fe2f"), // przedramiona
  shoulders: new ObjectId("68aad3839264e0e11499430f"), // barki
  neck: new ObjectId("68aad38872ef8a82a021f20c"), // szyja
  hipFlexors: new ObjectId("68aad38df112581c8704fa60"), // zginacze bioder
  obliques: new ObjectId("68aad392f5907c41ba40ac72"), // miesnie skosne brzucha
  quads: new ObjectId("68aad39683ad1b8d7e0edb8e"), // miesien czworoglowy uda
  upperBack: new ObjectId("68aad39b7ad952fe47faf202"), // gora plecy
  calves: new ObjectId("68aad3a05e3f2bfc57350e11"), // lydki
  hamstrings: new ObjectId("68aad3a4c15cb9c4cc7ca968"), // miesien dwuglowy uda
  glutes: new ObjectId("68aad3a97b59a7ce210a1eb0"), // posladki
  lowerBack: new ObjectId("68aad3b29792b4d037f1c0a4"), // dol plecy
};

const muscleGroups = [
  {
    _id: muscleGroupsIds.cardio,
    name: "Cardio",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126092/cardio_tadkdv.webp",
  },
  {
    _id: muscleGroupsIds.biceps,
    name: "Biceps",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126093/biceps_ze9erp.webp",
  },
  {
    _id: muscleGroupsIds.triceps,
    name: "Triceps",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756144214/triceps_aspu9c.webp",
  },
  {
    _id: muscleGroupsIds.abs,
    name: "Mięśnie brzucha",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126092/abs_kism5p.webp",
  },
  {
    _id: muscleGroupsIds.chest,
    name: "Klatka piersiowa",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126094/klata_vuikiv.webp",
  },
  {
    _id: muscleGroupsIds.forearms,
    name: "Przedramiona",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126097/przedramiona_ppaltc.webp",
  },
  {
    _id: muscleGroupsIds.shoulders,
    name: "Barki",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126093/barki_wuy1bg.webp",
  },
  {
    _id: muscleGroupsIds.neck,
    name: "Szyja",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126097/szyja_oa2ixq.webp",
  },
  {
    _id: muscleGroupsIds.hipFlexors,
    name: "Zginacze bioder",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126098/zginacze-bioder_ctwsvk.webp",
  },
  {
    _id: muscleGroupsIds.obliques,
    name: "Mięśnie skośne brzucha",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126097/skosne-brzucha_ydzk6p.webp",
  },
  {
    _id: muscleGroupsIds.quads,
    name: "Mięsień czworogłowy uda",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126093/czworoglowe-uda_cghrdg.webp",
  },
  {
    _id: muscleGroupsIds.upperBack,
    name: "Górne plecy",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126093/gorne-plecy_owt1cb.webp",
  },
  {
    _id: muscleGroupsIds.calves,
    name: "Łydki",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126096/lydki_sq1hhd.webp",
  },
  {
    _id: muscleGroupsIds.hamstrings,
    name: "Mięsień dwugłowy uda",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126093/dwuglowe-uda_dhqnj6.webp",
  },
  {
    _id: muscleGroupsIds.glutes,
    name: "Pośladki",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126096/posladki_vacfhv.webp",
  },
  {
    _id: muscleGroupsIds.lowerBack,
    name: "Dolne plecy",
    image: "https://res.cloudinary.com/dtglpgvzi/image/upload/v1756126093/dol-plecy_fdtdjm.webp",
  },
];
