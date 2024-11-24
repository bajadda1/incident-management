package ma.bonmyd.backendincident.services.incident.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.incident.SectorDTO;
import ma.bonmyd.backendincident.entities.incident.Sector;
import ma.bonmyd.backendincident.exceptions.ResourceAlreadyExistsException;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.incident.SectorRepository;
import ma.bonmyd.backendincident.repositories.incident.TypeRepository;
import ma.bonmyd.backendincident.services.incident.ISectorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class SectorServiceImpl implements ISectorService {

    private SectorRepository sectorRepository;
    private IModelMapper<Sector, SectorDTO> sectorModelMapper;
    private TypeRepository typeRepository;
    @Override
    public Sector findSectorById(Long id) {
        return this.sectorRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("sector with id=%d not found", id)
                        )
                );
    }

    @Override
    public Sector findSectorByName(String name) {
        Optional<Sector> optionalSector=this.sectorRepository.findByName(name);
        return optionalSector.orElse(null);
    }

    @Override
    public SectorDTO getSector(Long id) {
        Sector sector = this.findSectorById(id);
        return this.sectorModelMapper.convertToDto(sector, SectorDTO.class);
    }

    @Override
    public List<SectorDTO> getSectors() {
        List<Sector> sectors = this.sectorRepository.findAll();
        return this.sectorModelMapper.convertListToListDto(sectors, SectorDTO.class);
    }

    @Override
    public SectorDTO createSector(SectorDTO sectorDTO) {
        Sector existingSector = this.findSectorByName(sectorDTO.getName());
        if (existingSector != null) {
            throw new ResourceAlreadyExistsException(
                    String.format("sector with name=%s already exists", sectorDTO.getName()));
        }
        Sector sector = this.sectorModelMapper.convertToEntity(sectorDTO, Sector.class);
        sector=this.sectorRepository.save(sector);
        return this.sectorModelMapper.convertToDto(sector, SectorDTO.class);
    }

    @Override
    public SectorDTO updateSector(SectorDTO sectorDTO) {
        Sector existingSector = this.findSectorByName(sectorDTO.getName());
        if (existingSector != null && !Objects.equals(existingSector.getId(), sectorDTO.getId())) {
            throw new ResourceAlreadyExistsException(
                    String.format("sector with name=%s already exists", sectorDTO.getName()));
        }
        Sector sector = this.sectorModelMapper.convertToEntity(sectorDTO, Sector.class);
        this.sectorRepository.save(sector);
        return this.sectorModelMapper.convertToDto(sector, SectorDTO.class);
    }

    @Override
    public String deleteSector(Long id) {
        this.sectorRepository.deleteById(id);
        return String.format("sector with id=%d has been deleted successfully", id);
    }

    @Override
    public SectorDTO getSectorByTypeId(Long typeId) {
        Sector sector=this.typeRepository.findSectorByTypeId(typeId);
        return this.sectorModelMapper.convertToDto(sector,SectorDTO.class);
    }
}
