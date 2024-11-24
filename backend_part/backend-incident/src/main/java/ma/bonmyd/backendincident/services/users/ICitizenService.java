package ma.bonmyd.backendincident.services.users;

import ma.bonmyd.backendincident.dtos.users.CitizenDTO;
import ma.bonmyd.backendincident.entities.users.Citizen;

import java.util.List;

public interface ICitizenService {

    Citizen findCitizenById(Long id);

    Citizen findCitizenByIMEI(String imei);

    CitizenDTO getCitizenById(Long id);

    CitizenDTO getCitizenByIMEI(String imei);

    List<CitizenDTO> getCitizens();

    CitizenDTO createCitizen(CitizenDTO citizenDTO);

    String deleteCitizen(String imei);

    String deleteCitizenById(Long id);
}
