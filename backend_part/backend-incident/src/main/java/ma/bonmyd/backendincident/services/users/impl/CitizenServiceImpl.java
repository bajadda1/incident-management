package ma.bonmyd.backendincident.services.users.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.entities.users.Citizen;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.users.CitizenRepository;
import ma.bonmyd.backendincident.services.users.ICitizenService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CitizenServiceImpl implements ICitizenService {

    private IModelMapper<Citizen, CitizenDTO> citizenModelMapper;
    private CitizenRepository citizenRepository;

    @Override
    public Citizen findCitizenById(Long id) {
        return this.citizenRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("citizen with id=%d not found", id)));

    }

    @Override
    public Citizen findCitizenByIMEI(String imei) {
        Optional<Citizen> optionalCitizen = this.citizenRepository.findByImei(imei);
        return optionalCitizen.orElse(null);
    }

    @Override
    public CitizenDTO getCitizenById(Long id) {
        Citizen citizen = this.findCitizenById(id);
        return this.citizenModelMapper.convertToDto(citizen, CitizenDTO.class);
    }

    @Override
    public CitizenDTO getCitizenByIMEI(String imei) {
        Citizen citizen = this.findCitizenByIMEI(imei);
        return this.citizenModelMapper.convertToDto(citizen, CitizenDTO.class);
    }

    @Override
    public List<CitizenDTO> getCitizens() {
        List<Citizen> citizens = this.citizenRepository.findAll();
        return this.citizenModelMapper.convertListToListDto(citizens, CitizenDTO.class);
    }

    @Override
    public CitizenDTO createCitizen(CitizenDTO citizenDTO) {
        Citizen citizen = this.citizenModelMapper.convertToEntity(citizenDTO, Citizen.class);
        this.citizenRepository.save(citizen);
        return this.citizenModelMapper.convertToDto(citizen, CitizenDTO.class);
    }

    @Override
    public String deleteCitizen(String imei) {
        Citizen citizen = this.findCitizenByIMEI(imei);
        this.citizenRepository.delete(citizen);
        return String.format("citizen with imei=%s has been deleted successfully", imei);
    }

    @Override
    public String deleteCitizenById(Long id) {
        Citizen citizen = this.findCitizenById(id);
        this.citizenRepository.delete(citizen);
        return String.format("citizen with id=%s has been deleted successfully", id);
    }
}
