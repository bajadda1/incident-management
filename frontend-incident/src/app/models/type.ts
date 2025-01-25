import {SectorDTO} from './sector';

export interface TypeDTO {
  id: number | null,
  name: string
  sectorDTO: SectorDTO | null
}
