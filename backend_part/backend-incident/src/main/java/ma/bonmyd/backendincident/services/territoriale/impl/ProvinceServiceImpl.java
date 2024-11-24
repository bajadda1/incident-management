package ma.bonmyd.backendincident.services.territoriale.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTO;
import ma.bonmyd.backendincident.dtos.territoriale.ProvinceDTOPagination;
import ma.bonmyd.backendincident.entities.territoriale.Province;
import ma.bonmyd.backendincident.exceptions.ResourceNotFoundException;
import ma.bonmyd.backendincident.mappers.IModelMapper;
import ma.bonmyd.backendincident.repositories.territoriale.ProvinceRepository;
import ma.bonmyd.backendincident.services.territoriale.IProvinceService;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class ProvinceServiceImpl implements IProvinceService {

    private ProvinceRepository provinceRepository;
    private IModelMapper<Province, ProvinceDTO> provinceModelMapper;

    @Override
    public Province findProvinceById(Long id) {
        return this.provinceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("province with id=%d not found", id)));
    }

    @Override
    public ProvinceDTO getProvince(Long id) {
        Province province=this.findProvinceById(id);
        return this.provinceModelMapper.convertToDto(province,ProvinceDTO.class);
    }

    @Override
    public List<ProvinceDTO> getProvinces() {
        List<Province> provinces=this.provinceRepository.findAll();
        return this.provinceModelMapper.convertListToListDto(provinces,ProvinceDTO.class);
    }

    @Override
    public ProvinceDTOPagination getProvincesPage(int currentPage, int size) {
        Pageable pageable= PageRequest.of(currentPage,size);
        Page<Province> provinces=this.provinceRepository.findAll(pageable);
        Page<ProvinceDTO> provinceDTOS=this.provinceModelMapper.convertPageToPageDto(provinces,ProvinceDTO.class);
        return ProvinceDTOPagination.
                builder().
                currentPage(currentPage).
                pageSize(size).
                totalPages(provinceDTOS.getTotalPages()).
                provinceDTOS(provinceDTOS.getContent()).
                build();
    }

    @Override
    public Province findProvinceContainingPoint(String wktLocation) {
        Province province=this.provinceRepository.findProvinceContainingPoint(wktLocation);
        if (province==null){
            throw new ResourceNotFoundException(String.format("your location is outside the Morocco !"));
        }
        return province;
    }

}
