package org.eclipse.digitaltwin.basyx.aasxfileserver.http;

import jakarta.validation.Valid;
import org.eclipse.digitaltwin.aas4j.v3.model.PackageDescription;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.model.PackageDescriptionList;
import org.eclipse.digitaltwin.basyx.aasxfileserver.pagination.GetPackageDescriptionResult;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.pagination.Base64UrlEncodedCursor;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResult;
import org.eclipse.digitaltwin.basyx.http.pagination.PagedResultPagingMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * API Controller for the AASX File Server HTTP API
 *
 * @author fried
 */
@RestController
public class AASXFileServerHttpApiController implements AASXFileServerHttpApi {

    private final AASXFileServer aasxFileServer;

    @Autowired
    public AASXFileServerHttpApiController(AASXFileServer aasxFileServer) {
        this.aasxFileServer = aasxFileServer;
    }

    @Override
    public ResponseEntity<Void> deleteAASXByPackageId(Base64UrlEncodedIdentifier packageId) {
        boolean exists = doesPackageExist(packageId);
        if(!exists){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        aasxFileServer.deleteAASXByPackageId(packageId.getIdentifier());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Resource> getAASXByPackageId(Base64UrlEncodedIdentifier packageId) {
        try {
            InputStream aasxPackage = aasxFileServer.getAASXByPackageId(packageId.getIdentifier());
            InputStreamResource resource = new InputStreamResource(aasxPackage);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/asset-administration-shell-package"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.aasx")
                    .body(resource);
        } catch(ElementDoesNotExistException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<PagedResult> getAllAASXPackageIds(Base64UrlEncodedIdentifier aasId, Integer limit, Base64UrlEncodedCursor cursor) {

        if (limit == null) {
            limit = 100;
        }

        String decodedCursor = "";
        if (cursor != null) {
            decodedCursor = cursor.getDecodedCursor();
        }

        PaginationInfo pInfo = new PaginationInfo(limit, decodedCursor);

        if(aasId == null){
            aasId = new Base64UrlEncodedIdentifier("");
        }

        CursorResult<List<PackageDescription>> cursorResult = aasxFileServer.getAllAASXPackageIds(aasId.getIdentifier(),pInfo);

        GetPackageDescriptionResult packageDescriptionResult = new GetPackageDescriptionResult();

        String encodedCursor = getEncodedCursorFromCursorResult(cursorResult);

        packageDescriptionResult.result(new ArrayList<>(cursorResult.getResult()));
        packageDescriptionResult.setPagingMetadata(new PagedResultPagingMetadata().cursor(encodedCursor));
        return new ResponseEntity<PagedResult>(packageDescriptionResult, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PackageDescription> postAASXPackage(List<Base64UrlEncodedIdentifier> aasIds, MultipartFile file, Base64UrlEncodedIdentifier fileName) {
        try {
            InputStream fileStream = file.getInputStream();

            PackageDescription packageDescription = aasxFileServer.createAASXPackage(convertBase64UrlEncodedIdentifierListToStringList(aasIds), fileStream, fileName.getIdentifier());
            return new ResponseEntity<>(packageDescription, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Override
    public ResponseEntity<Void> putAASXByPackageId(Base64UrlEncodedIdentifier packageId, List<Base64UrlEncodedIdentifier> aasIds, MultipartFile file, Base64UrlEncodedIdentifier fileName) {
        boolean exists = doesPackageExist(packageId);
        if(!exists){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        try {
            InputStream fileStream = file.getInputStream();
            aasxFileServer.updateAASXByPackageId(packageId.getIdentifier(), convertBase64UrlEncodedIdentifierListToStringList(aasIds), fileStream, fileName.getIdentifier());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean doesPackageExist(Base64UrlEncodedIdentifier packageId) {
        return aasxFileServer.getAASXByPackageId(packageId.getIdentifier()) != null;
    }

    private String getEncodedCursorFromCursorResult(CursorResult<?> cursorResult) {
        if (cursorResult == null || cursorResult.getCursor() == null) {
            return null;
        }

        return Base64UrlEncodedCursor.encodeCursor(cursorResult.getCursor());
    }

    private List<String> convertBase64UrlEncodedIdentifierListToStringList(List<Base64UrlEncodedIdentifier> aasIds){
        List<String> aasIdsString = new ArrayList<>();
        for(Base64UrlEncodedIdentifier aasId : aasIds){
            aasIdsString.add(aasId.getIdentifier());
        }
        return aasIdsString;
    }
}