package ma.bonmyd.backendincident.services.incident;

import ma.bonmyd.backendincident.dtos.incident.TypeDTO;
import ma.bonmyd.backendincident.entities.incident.Type;

import java.util.List;

public interface ITypeService {

    Type findTypeById(Long id);
    Type findTypeByName(String name);
    TypeDTO getType(Long id);
    List<TypeDTO> getTypes();
    TypeDTO createType(Long sectorId,TypeDTO typeDTO);
    TypeDTO updateType(Long sectorId,TypeDTO typeDTO);
    String deleteType(Long id);
    List<TypeDTO> getTypesBySectorId(Long sectorId);
}
