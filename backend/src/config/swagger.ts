import swaggerJsdoc from "swagger-jsdoc";
import swaggerUi from "swagger-ui-express";

const options: swaggerJsdoc.Options = {
  definition: {
    openapi: "3.0.4",
    info: {
      title: "GymEaze API",
      version: "1.0.0",
      description: "Endpointy dla aplikacji społecznościowej GymEaze",
    },
  },
  apis: ["./src/controllers/*.ts"],
};

const swaggerSpec = swaggerJsdoc(options);

export { swaggerUi, swaggerSpec };
