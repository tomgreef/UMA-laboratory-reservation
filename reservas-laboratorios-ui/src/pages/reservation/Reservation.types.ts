type Semester = {
  id: number;
  name: string;
};

type Degree = {
  id: number;
  name: string;
};

type Department = {
  id: number;
  name: string;
};

type Professor = {
  email: string;
  name: string;
  id: number;
};

type Responsible = {
  phone: string;
  name: string;
  id: number;
};

type Subject = {
  id: number;
  name: string;
  course: number;
  group: string;
  subgroup: string;
};

export enum ScheduleType {
  PREFERRED = "PREFERRED",
  ALTERNATIVE = "ALTERNATIVE",
}

export type Reservation = {
  id: number;
  publicId: number;
  type: string;
  schedule: ScheduleType;
  degree: Degree;
  subject: Subject;
  semester: Semester;
  professor: Professor;
  responsible: Responsible;
  department: Department;
  startDate: string;
  endDate: string;
  day: string;
  dayType: string;
  startTime: string;
  endTime: string;
  teachingType?: string;
  laboratoryPreference?: string[];
  location?: string;
  studentsNumber?: number;
  operatingSystem?: string;
  additionalEquipment?: string;
};
