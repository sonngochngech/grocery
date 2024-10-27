package com.grocery.app.repositories;

import com.grocery.app.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepo extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceId(String deviceId);

}
