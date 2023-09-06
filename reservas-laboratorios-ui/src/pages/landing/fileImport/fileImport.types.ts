export type Error = {
  type: string; // A generalization of the errorsubjectId
  code: string; // Standardized error code
  message: string; // Human-readable details
  row?: number; // Row index of parsed data where error is
};
export type Meta = {
  delimiter?: string; // Delimiter used
  linebreak?: string; // Line break sequence used
  aborted?: string; // Whether process was aborted
  fields?: string[]; // Array of field names
  truncated?: string; // Whether preview consumed all input
};
export type Data = {
  data: ReservationDto[];
  errors: Error[];
  meta: Meta[];
};

export type ReservationDto = {
  publicId: string;
  teachingType: string;
  degreeName: string;
  subjectName: string;
  subjectCourse: string;
  subjectGroup: string;
  subjectSubgroup: string;
  professorName: string;
  professorEmail: string;
  departmentName: string;
  startDate: string;
  endDate: string;
  dayAndTimeSlot: string;
  location: string;
  laboratoryPreference: string;
  studentsNumber: string;
  type: string;
  responsibleName: string;
  responsiblePhone: string;
  schedule: string;
  operatingSystem: string;
  additionalEquipment: string;
};

export type ReservationDtoWithError = {
  dto: ReservationDto;
  rowNumber: number;
  error: string;
};

export const RESERVATION_DTO_MAPPING: { [k: number]: string } = {
  0: "publicId",
  1: "teachingType",
  2: "degreeName",
  3: "subjectName",
  4: "subjectCourse",
  5: "subjectGroup",
  6: "subjectSubgroup",
  7: "professorName",
  8: "professorEmail",
  9: "departmentName",
  10: "startDate",
  11: "endDate",
  12: "dayAndTimeSlot",
  13: "location",
  14: "laboratoryPreference",
  15: "studentsNumber",
  16: "type",
  17: "responsibleName",
  18: "responsiblePhone",
  19: "schedule",
  20: "operatingSystem",
  21: "additionalEquipment",
};
