import {SectorDTO} from './sector';
import {TypeDTO} from './type';
import {Status} from '../enums/status';
import {ProvinceDTO} from './province';

export interface IncidentDTO {
  id: number; // Corresponds to Long in Java
  photo: string; // Image or file path
  createdAt: Date; // Java Date
  updatedAt: Date; // Java Date
  description: string; // Incident description
  status: Status; // Status interface
  typeDTO: TypeDTO; // Type details
  sectorDTO: SectorDTO; // Sector details
  location: string; // Location information
  // citizenDTO?: citizenDTO; // Uncomment if needed
  provinceDTO: ProvinceDTO
}
