package com.tpt.capstone_ecommerce.ecommerce.repository;

import com.tpt.capstone_ecommerce.ecommerce.entity.Location;
import com.tpt.capstone_ecommerce.ecommerce.enums.LOCATION_TYPE;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {
    Location findByName(@NotBlank(message = "Location name cannot be blank") @Size(min = 1, max = 255, message = "Location name length is invalid") String name);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM locations WHERE id = ?1 OR parent_id = ?1", nativeQuery = true)
    void deleteProvinceOrDistrict(String locationId);

    List<Location> findAllByType(LOCATION_TYPE type);

    List<Location> findAllByParentId(String parentId);
}
