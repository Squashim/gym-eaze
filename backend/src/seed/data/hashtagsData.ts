import { Types } from "mongoose";
import Hashtag from "../../models/hashtagsModel";

export const seedHashtags = async () => {
  await Hashtag.deleteMany();
  await Hashtag.insertMany(hashtags);
  console.log("Hashtagi dodane");
};

const hashtags = [
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d601"),
    name: "fitness",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d602"),
    name: "trening",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d603"),
    name: "siłownia",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d604"),
    name: "zdrowie",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d605"),
    name: "motywacja",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d606"),
    name: "cardio",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d607"),
    name: "siła",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d608"),
    name: "odżywianie",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d609"),
    name: "regeneracja",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d610"),
    name: "kondycja",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d611"),
    name: "wytrzymałość",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d612"),
    name: "siłacz",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d613"),
    name: "stretching",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d614"),
    name: "pilates",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d615"),
    name: "joga",
    createdAt: new Date(),
  },
  {
    _id: new Types.ObjectId("64b2f3a0c8e4c1a2b3c4d616"),
    name: "zdrowyStylŻycia",
    createdAt: new Date(),
  },
];
