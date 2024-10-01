/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

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
