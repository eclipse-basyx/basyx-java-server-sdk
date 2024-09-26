package org.eclipse.digitaltwin.basyx.examples.basyxclient.model;

import java.time.LocalDateTime;

import org.eclipse.digitaltwin.basyx.examples.basyxclient.utils.LocalDateTimeConverter;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;

public class MotorEntry {
        @CsvBindByName
        private String motorId;

        @CsvBindByName
        private String motorType;

        @CsvBindByName
        private String manufacturer;

        @CsvCustomBindByName(converter = LocalDateTimeConverter.class)
        private LocalDateTime purchaseDate;

        @CsvBindByName
        private String location;

        @CsvCustomBindByName(converter = LocalDateTimeConverter.class)
        private LocalDateTime lastMaintenance;

        @CsvCustomBindByName(converter = LocalDateTimeConverter.class)
        private LocalDateTime maintenanceSchedule;

        @CsvCustomBindByName(converter = LocalDateTimeConverter.class)
        private LocalDateTime warrantyPeriod;

        @CsvBindByName
        private String status;

        @CsvCustomBindByName(converter = LocalDateTimeConverter.class)
        private LocalDateTime dateSold;

        public String getMotorId() {
                return motorId;
        }

        public void setMotorId(String motorId) {
                this.motorId = motorId;
        }

        public String getMotorType() {
                return motorType;
        }

        public void setMotorType(String motorType) {
                this.motorType = motorType;
        }

        public String getManufacturer() {
                return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
                this.manufacturer = manufacturer;
        }

        public LocalDateTime getPurchaseDate() {
                return purchaseDate;
        }

        public void setPurchaseDate(LocalDateTime purchaseDate) {
                this.purchaseDate = purchaseDate;
        }

        public String getLocation() {
                return location;
        }

        public void setLocation(String location) {
                this.location = location;
        }

        public LocalDateTime getLastMaintenance() {
                return lastMaintenance;
        }

        public void setLastMaintenance(LocalDateTime lastMaintenance) {
                this.lastMaintenance = lastMaintenance;
        }

        public LocalDateTime getMaintenanceSchedule() {
                return maintenanceSchedule;
        }

        public void setMaintenanceSchedule(LocalDateTime maintenanceSchedule) {
                this.maintenanceSchedule = maintenanceSchedule;
        }

        public LocalDateTime getWarrantyPeriod() {
                return warrantyPeriod;
        }

        public void setWarrantyPeriod(LocalDateTime warrantyPeriod) {
                this.warrantyPeriod = warrantyPeriod;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

        public LocalDateTime getDateSold() {
                return dateSold;
        }

        public void setDateSold(LocalDateTime dateSold) {
                this.dateSold = dateSold;
        }
}
