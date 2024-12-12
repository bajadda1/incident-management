import {SectorDTO} from './sector';
import {TypeDTO} from './type';
import {RoleDTO} from './role';


export interface UserRegisterDTO {
  id: number | null,
  fullname: string,
  username: string,
  password: string,
  sectorDTO: SectorDTO
}
