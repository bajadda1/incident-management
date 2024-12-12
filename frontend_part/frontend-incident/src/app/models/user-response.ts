import {SectorDTO} from './sector';
import {TypeDTO} from './type';
import {RoleDTO} from './role';


export interface UserResponseDTO {
  id: number,
  fullname: string,
  username: string,
  sectorDTO: SectorDTO,
  enabled: boolean
}
