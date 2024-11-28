package ma.bonmyd.backendincident.services.incident.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.TypeDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.entities.incident.Type;
import ma.bonmyd.backendincident.exceptions.ResourceAlreadyExistsException;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.incident.SectorRepository;
import ma.bonmyd.backendincident.repositories.incident.TypeRepository;
import ma.bonmyd.backendincident.services.incident.ISectorService;
import ma.bonmyd.backendincident.services.incident.ITypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class TypeServiceImpl implements ITypeService {

    private TypeRepository typeRepository;
    private IModelMapper<Type, TypeDTO> typeModelMapper;
    private ISectorService sectorService;

    @Override
    public Type findTypeById(Long id) {
        return this.typeRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("type with id=%d not found", id)
                        )
                );
    }

    @Override
    public Type findTypeByName(String name) {
        Optional<Type> optionalType=this.typeRepository
                .findByName(name);
        return optionalType.orElse(null);
    }

    @Override
    public TypeDTO getType(Long id) {
        Type type = this.findTypeById(id);
        return this.typeModelMapper.convertToDto(type, TypeDTO.class);
    }

    @Override
    public List<TypeDTO> getTypes() {
        List<Type> types = this.typeRepository.findAll();
        return this.typeModelMapper.convertListToListDto(types, TypeDTO.class);
    }

    @Override
    public TypeDTO createType(Long sectorId,TypeDTO typeDTO) {
        Sector sector=this.sectorService.findSectorById(sectorId);
        Type existingType = this.findTypeByName(typeDTO.getName());
        if (existingType != null) {
            throw new ResourceAlreadyExistsException(
                    String.format("type with name=%s already exists", typeDTO.getName()));
        }
        Type type = this.typeModelMapper.convertToEntity(typeDTO, Type.class);
        type.setSector(sector);
        this.typeRepository.save(type);
        return this.typeModelMapper.convertToDto(type, TypeDTO.class);
    }

    @Override
    public TypeDTO updateType(Long sectorId,TypeDTO typeDTO) {
        Type existingType = this.findTypeByName(typeDTO.getName());
        if (existingType != null && !Objects.equals(existingType.getId(), typeDTO.getId())) {
            throw new ResourceAlreadyExistsException(
                    String.format("type with name=%s already exists", typeDTO.getName()));
        }
        Sector sector=this.sectorService.findSectorById(sectorId);
        Type type = this.typeModelMapper.convertToEntity(typeDTO, Type.class);
        type.setSector(sector);
        type=this.typeRepository.save(type);
        return this.typeModelMapper.convertToDto(type, TypeDTO.class);
    }

    @Override
    public String deleteType(Long id) {
        this.typeRepository.deleteById(id);
        return String.format("type with id=%d has been deleted successfully", id);
    }

    @Override
    public List<TypeDTO> getTypesBySectorId(Long sectorId) {
        List<Type> types=this.typeRepository.findBySectorId(sectorId);
        return this.typeModelMapper.convertListToListDto(types,TypeDTO.class);
    }
}
