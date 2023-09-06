export type Laboratory = {
  id: number;
  name: string;
  capacity: number;
  location: string;
  operatingSystem?: string;
  additionalEquipment?: string;
  adjacentLaboratories: number[];
};
