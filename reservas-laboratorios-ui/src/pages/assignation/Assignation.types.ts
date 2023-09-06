import dayjs from "dayjs";
import { Laboratory } from "../laboratory/Laboratory.types";
import { Semester } from "../semester/semester.types";
import { Reservation } from "../reservation/Reservation.types";

export const START_TIME = dayjs().startOf("day").add(8, "hours").add(45, "minutes");
export const END_TIME = dayjs().startOf("day").add(21, "hours").add(15, "minutes");
export const BREAK_START_TIME = dayjs().startOf("day").add(14, "hours").add(30, "minutes");
export const BREAK_END_TIME = dayjs().startOf("day").add(15, "hours").add(30, "minutes");
export const DURATION_HOURS = 2;

export type AssignmentDto = {
  task: Task;
  assignments: Assignment[];
  conflicts: Conflict[];
};

export type Assignment = {
  id: number;
  reservation: Reservation;
  laboratory: Laboratory;
  semester: Semester;
};

export type Conflict = {
  id: number;
  reservation1: Reservation;
  reservation2: Reservation;
  laboratory: Laboratory;
  semester: Semester;
  startDate: string;
  endDate: string;
  day: string;
  startTime: string;
  endTime: string;
};

type Task = {
  id: number;
  semester: Semester;
  status: TaskStatus;
  errorMessages: string;
  created: Date;
  updated: Date;
};

export enum TaskStatus {
  QUEUED = "QUEUED",
  RUNNING = "RUNNING",
  COMPLETED = "COMPLETED",
  ERROR = "ERROR",
  OUTDATED = "OUTDATED",
}

export const WeekDays = {
  1: "Lunes",
  2: "Martes",
  3: "Miércoles",
  4: "Jueves",
  5: "Viernes",
};

export type TimeSlot = {
  day: number;
  startTime: string;
  endTime: string;
};

export type ExportData = {
  centro: string;
  titulación: string;
  asignatura: string;
  curso: string;
  grupo: string;
  fecha_inicio: string;
  fecha_fin: string;
  dia: string;
  hora_inicio: string;
  hora_fin: string;
  aula: string;
  descripción?: string;
  tipo: string;
};

export const generateSpanishDate = (date: string) => {
  const [year, month, day] = date.split("-");
  return `${day}/${month}/${year}`;
};
