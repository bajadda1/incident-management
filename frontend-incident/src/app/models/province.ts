import {RegionDTO} from './region';

export interface ProvinceDTO {
  id: number;          // Corresponds to Long in Java
  name: string;        // Corresponds to String in Java
  area: number;        // Corresponds to double in Java
  perimeter: number;   // Corresponds to double in Java
  geom: string;        // Corresponds to String in Java
  regionDTO: RegionDTO; // Uncomment if RegionDTO is implemented in TypeScript
}
