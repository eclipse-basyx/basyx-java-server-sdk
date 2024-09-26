package org.eclipse.digitaltwin.basyx.examples.basyxclient.model;

import java.time.LocalDateTime;

public record MotorEntry(String motorId, String motorType, String manufacturer, LocalDateTime purchaseDate, String location, LocalDateTime lastMaintenance, LocalDateTime maintenanceSchedule, LocalDateTime warrantyPeriod, String status,
        LocalDateTime dateSold) {
}
