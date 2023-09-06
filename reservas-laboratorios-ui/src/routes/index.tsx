import { ReactNode } from "react";
import LandingPage from "../pages/landing/Landing";
import ReservationsPage from "../pages/reservation/Reservation";
import SemestersPage from "../pages/semester/Semesters";
import AssignationPage from "../pages/assignation/Assignation";
import { PathConstants } from "./PathConstants";
import LaboratoriesPage from "../pages/laboratory/Laboratories";

type Route = {
  path: string;
  name: string;
  element: ReactNode;
  requiresSemester?: boolean;
  ignoreInMenu?: boolean;
};

const routes: Route[] = [
  { path: PathConstants.HOME, name: "Importar", element: <LandingPage /> },
  { path: PathConstants.ASSIGNATIONS, name: "Asignaciones", element: <AssignationPage />, requiresSemester: true },
  { path: PathConstants.RESERVATIONS, name: "Reservas", element: <ReservationsPage />, requiresSemester: true },
  { path: PathConstants.SEMESTERS, name: "Cuatrimestres", element: <SemestersPage /> },
  { path: PathConstants.LABORATORIES, name: "Laboratorios", element: <LaboratoriesPage /> },
  { path: PathConstants.RESERVATIONS_BY_ID, name: "Reservas", element: <ReservationsPage />, ignoreInMenu: true },
];

export default routes;
