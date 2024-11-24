package ma.bonmyd.backendincident.services.territoriale;

import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTO;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.entities.territoriale.Province;
import org.locationtech.jts.geom.Point;

import java.util.List;

public interface IProvinceService {
    Province findProvinceById(Long id);

    ProvinceDTO getProvince(Long id);

    List<ProvinceDTO> getProvinces();

    ProvinceDTOPagination getProvincesPage(int currentPage,int size);

    Province findProvinceContainingPoint(String wktLocation);

}
