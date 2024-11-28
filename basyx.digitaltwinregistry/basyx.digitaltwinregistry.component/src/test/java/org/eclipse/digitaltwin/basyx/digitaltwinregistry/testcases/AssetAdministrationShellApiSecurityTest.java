/*******************************************************************************
 * Copyright (c) 2021 Robert Bosch Manufacturing Solutions GmbH and others
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.digitaltwinregistry.testcases;

import static org.eclipse.digitaltwin.basyx.digitaltwinregistry.testcases.helper.TestUtil.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Key;
import org.eclipse.digitaltwin.basyx.aasregistry.model.KeyTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Reference;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ReferenceTypes;
import org.eclipse.digitaltwin.basyx.aasregistry.model.SpecificAssetId;
import org.eclipse.digitaltwin.basyx.digitaltwinregistry.testcases.helper.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *  This class contains test to verify Authentication and RBAC based Authorization for all API endpoints.
 *  Every API endpoint is tested explicitly.
 */
public class AssetAdministrationShellApiSecurityTest extends AbstractAssetAdministrationShellApi {

   /**
    * The specificAssetId#externalSubjectId indicates which tenant is allowed to see the specificAssetId and
    * find a Shell.
    *
    * Given:
    *  - Company A creates an AAS with multiple with: 1. one specificAssetId without externalSubjectId,
    *                                                 2. one with externalSubjectId = Company B
    *                                                 3. one with externalSubjectId = Company C
    *
    *   - Rules: When Company A requests the AAS, all specificAssetIds 1,2 and are shown. Company A is the owner of the AAS.
    *               The AAS Registry has an environment property "owningTenantId" that is compared with the tenantId from the token.
    *            When Company B requests the AAS, only specificAssetIds 1 and 2 are shown.
    *            When Company C requests the AAS, only specificAssetIds 1 and 3 are shown.
    *
    *            The same logic applies also to the lookup endpoints.
    *
    */
   @Nested
   @DisplayName( "Tenant based specificAssetId visibility test" )
   class TenantBasedVisibilityTest {

      String keyPrefix;

      @BeforeEach
      void setUp() {
         keyPrefix = UUID.randomUUID().toString();
      }

      @Test
      public void testGetAllShellsWithDefaultClosedFilteredSpecificAssetIdsByTenantId() throws Exception {
         AssetAdministrationShellDescriptor shellPayload = TestUtil
               .createCompleteAasDescriptor( keyPrefix + "semanticId", "http://example.com/" );
         shellPayload.setId( keyPrefix );
         List<SpecificAssetId> shellpayloadSpecificAssetIDs = shellPayload.getSpecificAssetIds();
         shellpayloadSpecificAssetIDs.forEach( specificAssetId -> specificAssetId.setExternalSubjectId( null ) );
         shellPayload.setSpecificAssetIds( shellpayloadSpecificAssetIDs );

         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result" ).exists() );

         // test with tenant two
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result" ).exists() )
               .andExpect( jsonPath( "$.result[*].specificAssetIds[*].value",
                     not( hasItems( "identifier1ValueExample", "identifier2ValueExample", "tenantThreeAssetIdValue" ) ) ) );
      }

      @Test
      public void testGetShellWithFilteredSpecificAssetIdsByTenantId() throws Exception {
         SpecificAssetId asset1 = TestUtil.createSpecificAssetId( keyPrefix + "CustomerPartId", "tenantTwoAssetIdValue",
               List.of( jwtTokenFactory.tenantTwo().getTenantId() ) );
         SpecificAssetId asset2 = TestUtil.createSpecificAssetId( keyPrefix + "CustomerPartId2", "tenantThreeAssetIdValue",
               List.of( jwtTokenFactory.tenantThree().getTenantId() ) );
         SpecificAssetId asset3 = TestUtil.createSpecificAssetId( keyPrefix + "MaterialNumber", "withoutTenantAssetIdValue",
               List.of( jwtTokenFactory.tenantTwo().getTenantId() ) );
         // Define specificAsset with wildcard which not allowed. (Only manufacturerPartId is defined in application.yml)
         SpecificAssetId asset4 = TestUtil.createSpecificAssetId( keyPrefix + "BPID", "ignoreWildcard", List.of( getExternalSubjectIdWildcardPrefix() ) );
         // Define specificAsset with wildcard which is allowed. (Only manufacturerPartId is defined in application.yml)
         SpecificAssetId asset5 = TestUtil.createSpecificAssetId( "manufacturerPartId", keyPrefix + "wildcardAllowed",
               List.of( getExternalSubjectIdWildcardPrefix() ) );

         List<SpecificAssetId> specificAssetIds = List.of( asset1, asset2, asset3, asset4, asset5 );
         List<SpecificAssetId> expectedSpecificAssetIdsTenantTwo = List.of( asset1, asset3, asset5 );
         testGetShellWithFilteredSpecificAssetIdsByTenantId( specificAssetIds, expectedSpecificAssetIdsTenantTwo );
      }

      public void testGetShellWithFilteredSpecificAssetIdsByTenantId( List<SpecificAssetId> specificAssetIds, List<SpecificAssetId> expectedSpecificAssetIds )
            throws Exception {
         AssetAdministrationShellDescriptor shellPayload = TestUtil
               .createCompleteAasDescriptor( keyPrefix + "semanticId", "http://example.com/" );
         shellPayload.setId( keyPrefix );
         shellPayload.setSpecificAssetIds( specificAssetIds );
         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         String shellId = shellPayload.getId();
         String encodedShellId = getEncodedValue( shellId );

         // Owner of tenant has access to all specificAssetIds
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SINGLE_SHELL_BASE_PATH, encodedShellId )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.id", equalTo( shellId ) ) )
               .andExpect( jsonPath( "$.specificAssetIds[*].name", hasItems( specificAssetIds.stream().map( SpecificAssetId::getName ).toArray() ) ) )
               .andExpect( jsonPath( "$.specificAssetIds[*].value", hasItems( specificAssetIds.stream().map( SpecificAssetId::getValue ).toArray() ) ) );

         // test with tenant two
         ArrayList<SpecificAssetId> hiddenSpecificAssetIds = new ArrayList<>( specificAssetIds );
         hiddenSpecificAssetIds.removeAll( expectedSpecificAssetIds );

         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SINGLE_SHELL_BASE_PATH, encodedShellId )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.id", equalTo( shellId ) ) )
               .andExpect( jsonPath( "$.specificAssetIds[*].name", hasItems( expectedSpecificAssetIds.stream().map( SpecificAssetId::getName ).toArray() ) ) )
               .andExpect( jsonPath( "$.specificAssetIds[*].value", hasItems( expectedSpecificAssetIds.stream().map( SpecificAssetId::getValue ).toArray() ) ) )
               .andExpect(
                     jsonPath( "$.specificAssetIds[*].value", not( hasItems( hiddenSpecificAssetIds.stream().map( SpecificAssetId::getValue ).toArray() ) ) ) );
      }

      @Test
      @Disabled( "Test will be ignored, because the new api does not provided batch, fetch and query. This will be come later in version 0.3.1" )
      void testFetchShellsWithFilteredSpecificAssetIdsByTenantId() throws Exception {
         ObjectNode shellPayload = createBaseIdPayload( "example", "example" );
         String tenantTwoAssetIdValue = "tenantTwofgkj129293";
         String tenantThreeAssetIdValue = "tenantThree543412394";
         String withoutTenantAssetIdValue = "withoutTenant329347192jf18";
         shellPayload.set( "specificAssetIds", emptyArrayNode()
               .add( specificAssetId( "CustomerPartId", tenantTwoAssetIdValue, jwtTokenFactory.tenantTwo().getTenantId() ) )
               .add( specificAssetId( "CustomerPartId", tenantThreeAssetIdValue, jwtTokenFactory.tenantThree().getTenantId() ) )
               .add( specificAssetId( "MaterialNumber", withoutTenantAssetIdValue ) )
         );
         performShellCreateRequest( toJson( shellPayload ) );
         String shellId = getId( shellPayload );

         ArrayNode queryPayload = emptyArrayNode().add( shellId );
         mvc.perform(
                     MockMvcRequestBuilders
                           .post( SHELL_BASE_PATH + "/fetch" )
                           .content( toJson( queryPayload ) )
                           .contentType( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.items[*].identification", hasItem( shellId ) ) )
               .andExpect( jsonPath( "$.items[*].specificAssetIds[*].value",
                     hasItems( tenantTwoAssetIdValue, tenantThreeAssetIdValue, withoutTenantAssetIdValue ) ) );

         // test with tenant two
         mvc.perform(
                     MockMvcRequestBuilders
                           .post( SHELL_BASE_PATH + "/fetch" )
                           .contentType( MediaType.APPLICATION_JSON )
                           .content( toJson( queryPayload ) )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.items[*].identification", hasItem( shellId ) ) )
               .andExpect( jsonPath( "$.items[*].specificAssetIds[*].value", hasItems( tenantTwoAssetIdValue, withoutTenantAssetIdValue ) ) )
               .andExpect( jsonPath( "$.items[*].specificAssetIds[*].value", not( hasItem( tenantThreeAssetIdValue ) ) ) );
      }

      @Test
      public void testGetSpecificAssetIdsFilteredByTenantId() throws Exception {
         AssetAdministrationShellDescriptor shellPayload = TestUtil.createCompleteAasDescriptor();
         shellPayload.setId( UUID.randomUUID().toString() );
         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         // Update specificIds only with one specificAssetId for tenantOne
         SpecificAssetId specificAssetId = new SpecificAssetId();
         Reference externalSubjectId = new Reference();
         Key key = new Key();
         key.setType( KeyTypes.SUBMODEL );
         key.setValue( jwtTokenFactory.tenantOne().getTenantId() );
         externalSubjectId.setKeys( List.of( key ) );
         externalSubjectId.setType( ReferenceTypes.EXTERNALREFERENCE );
         specificAssetId.setName( "findExternal_1_tenantOne" );
         specificAssetId.setValue( "value_1:tenantOne" );
         specificAssetId.setExternalSubjectId( externalSubjectId );

         String shellId = shellPayload.getId();
         mvc.perform(
                     MockMvcRequestBuilders
                           .post( SINGLE_LOOKUP_SHELL_BASE_PATH, getEncodedValue( shellId ) )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .accept( MediaType.APPLICATION_JSON )
                           .contentType( MediaType.APPLICATION_JSON )
                           .content( mapper.writeValueAsString( List.of( specificAssetId ) ) )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isCreated() )
               .andExpect( content().json( mapper.writeValueAsString( List.of( specificAssetId ) ) ) );

         String encodedObject = Base64.getUrlEncoder().encodeToString( serialize( specificAssetId ) );

         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "assetIds", encodedObject )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               .andExpect( jsonPath( "$.result", contains( shellPayload.getId() ) ) );

         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .accept( MediaType.APPLICATION_JSON )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "assetIds", encodedObject )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 0 ) ) );
      }

      @Test
      public void testFindExternalShellIdsBySpecificAssetIdsWithTenantBasedVisibilityExpectSuccess() throws Exception {
         // the keyPrefix ensures that this test can run against a persistent database multiple times
         String keyPrefix = UUID.randomUUID().toString();
         // first shell
         AssetAdministrationShellDescriptor shellPayload = TestUtil.createCompleteAasDescriptor();
         shellPayload.setSpecificAssetIds( null );
         shellPayload.setId( UUID.randomUUID().toString() );
         SpecificAssetId asset1 = TestUtil.createSpecificAssetId( keyPrefix + "findExternal_2", "value_2", null );
         SpecificAssetId asset2 = TestUtil.createSpecificAssetId( keyPrefix + "findExternal_2_1", "value_2_1",
               List.of( jwtTokenFactory.tenantTwo().getTenantId() ) );
         SpecificAssetId asset3 = TestUtil.createSpecificAssetId( keyPrefix + "findExternal_2_2", "value_2_2",
               List.of( jwtTokenFactory.tenantThree().getTenantId() ) );

         shellPayload.setSpecificAssetIds( List.of( asset1, asset2, asset3 ) );

         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         SpecificAssetId sa1 = TestUtil.createSpecificAssetId( keyPrefix + "findExternal_2_1", "value_2_1",
               List.of( jwtTokenFactory.tenantTwo().getTenantId() ) );
         SpecificAssetId sa2 = TestUtil.createSpecificAssetId( keyPrefix + "findExternal_2_2", "value_2_2",
               List.of( jwtTokenFactory.tenantThree().getTenantId() ) );

         String encodedSa1 = Base64.getUrlEncoder().encodeToString( serialize( sa1 ) );
         String encodedSa2 = Base64.getUrlEncoder().encodeToString( serialize( sa2 ) );
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "assetIds", encodedSa1 )
                           .queryParam( "assetIds", encodedSa2 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               // ensure that only three results match
               .andExpect( jsonPath( "$.result", contains( shellPayload.getId() ) ) );

         // test with tenantTwo assetId included

         SpecificAssetId specificAssetIdsWithTenantTwoIncluded = TestUtil.createSpecificAssetId( keyPrefix + "findExternal_2_2", "value_2_2", null );
         String encodedSaWithTenantTwoIncluded = Base64.getUrlEncoder().encodeToString( serialize( specificAssetIdsWithTenantTwoIncluded ) );
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "assetIds", encodedSaWithTenantTwoIncluded )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 0 ) ) );

         // Test lookup with one assetId for tenant two and one without tenantId

         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "assetIds", encodedSa1 )
                           .queryParam( "assetIds", encodedSa2 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               // ensure that only three results match
               .andExpect( jsonPath( "$.result", contains( shellPayload.getId() ) ) );
      }

      @Test
      public void testFindExternalShellIdsBySpecificAssetIdsWithTenantBasedVisibilityAndWildcardExpectSuccess() throws Exception {
         // the keyPrefix ensures that this test can run against a persistent database multiple times
         AssetAdministrationShellDescriptor shellPayload = TestUtil
               .createCompleteAasDescriptor( keyPrefix + "semanticId", "http://example.com/" );
         shellPayload.setId( keyPrefix );
         shellPayload.setSpecificAssetIds( null );

         // asset1 is only visible for the owner because the externalSubjectId = null
         SpecificAssetId asset1 = TestUtil.createSpecificAssetId( keyPrefix + "defaultClosed", "value_1", null );
         // asset2 is visible for everyone, because externalSubjectId = PUBLIC_READABLE and specificAssetKey is manufacturerPartId (which is in the list of allowedTypes via application.yml)
         SpecificAssetId asset2 = TestUtil.createSpecificAssetId( "manufacturerPartId", keyPrefix + "value_2",
               List.of( getExternalSubjectIdWildcardPrefix() ) );
         // asset3 is visible only for the owner, because externalSubjectId = PUBLIC_READABLE but specificAssetKey is bpId (which is not in the list of allowedTypes via application.yml)
         SpecificAssetId asset3 = TestUtil.createSpecificAssetId( keyPrefix + "bpId", "value_3", List.of( getExternalSubjectIdWildcardPrefix() ) );
         // asset3 is visible for tenantTwo and tenantThree
         SpecificAssetId asset4 = TestUtil.createSpecificAssetId( keyPrefix + "tenantTwo_tenantThree", "value_3",
               List.of( jwtTokenFactory.tenantTwo().getTenantId(), jwtTokenFactory.tenantThree().getTenantId() ) );
         // asset4 is visible for tenantTwo, because externalSubjectId = tenantTwo
         SpecificAssetId asset5 = TestUtil.createSpecificAssetId( keyPrefix + "tenantTwo", "value_2_private",
               List.of( jwtTokenFactory.tenantTwo().getTenantId() ) );

         shellPayload.setSpecificAssetIds( List.of( asset1, asset2, asset3, asset4, asset5 ) );

         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         SpecificAssetId sa1 = TestUtil.createSpecificAssetId( keyPrefix + "defaultClosed", "value_1", null );
         SpecificAssetId sa2 = TestUtil.createSpecificAssetId( "manufacturerPartId", keyPrefix + "value_2", null );
         SpecificAssetId sa3 = TestUtil.createSpecificAssetId( keyPrefix + "bpId", "value_3", null );
         SpecificAssetId sa4 = TestUtil.createSpecificAssetId( keyPrefix + "tenantTwo_tenantThree", "value_3", null );
         SpecificAssetId sa5 = TestUtil.createSpecificAssetId( keyPrefix + "tenantTwo", "value_2_private", null );

         String encodedSa1 = Base64.getUrlEncoder().encodeToString( serialize( sa1 ) );
         String encodedSa2 = Base64.getUrlEncoder().encodeToString( serialize( sa2 ) );
         String encodedSa3 = Base64.getUrlEncoder().encodeToString( serialize( sa3 ) );
         String encodedSa4 = Base64.getUrlEncoder().encodeToString( serialize( sa4 ) );
         String encodedSa5 = Base64.getUrlEncoder().encodeToString( serialize( sa5 ) );

         // Make request with bpn of the owner
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "assetIds", encodedSa1 )
                           .queryParam( "assetIds", encodedSa2 )
                           .queryParam( "assetIds", encodedSa3 )
                           .queryParam( "assetIds", encodedSa4 )
                           .queryParam( "assetIds", encodedSa5 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               .andExpect( jsonPath( "$.result", contains( shellPayload.getId() ) ) );

         // test with tenantTwo: returns shellId because the specificAssetIds matched
         // sa2 = manufacturerPartId (public for everyone)
         // sa5 = match bpn of tenantTwo
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "assetIds", encodedSa2 )
                           .queryParam( "assetIds", encodedSa5 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               .andExpect( jsonPath( "$.result", contains( shellPayload.getId() ) ) );

         // test with tenantTwo: returns no shellId because the specificAssetId sa3 is set to public but the key is not in the list of public allowed types.
         // sa2 = manufacturerPartId (public for everyone)
         // sa3 = visible only for owner because key is not in the list of public allowed types.
         // sa5 = match bpn of tenantTwo
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "assetIds", encodedSa2 )
                           .queryParam( "assetIds", encodedSa3 )
                           .queryParam( "assetIds", encodedSa5 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 0 ) ) );

         // test with tenantThree: returns no shellId because the specificAssetId sa5 is only visible for tenantTwo
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantThree().getTenantId() )
                           .queryParam( "assetIds", encodedSa2 )
                           .queryParam( "assetIds", encodedSa5 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 0 ) ) );
      }

      @Test
      public void testFindExternalShellIdsBySpecificAssetIdsWithDefaultClosedTenantBasedVisibilityExpectSuccess() throws Exception {
         // the keyPrefix ensures that this test can run against a persistent database multiple times
         String keyPrefix = UUID.randomUUID().toString();
         AssetAdministrationShellDescriptor shellPayload = TestUtil.createCompleteAasDescriptor();
         shellPayload.setSpecificAssetIds( null );
         shellPayload.setId( UUID.randomUUID().toString() );

         // asset1 is only visible for the owner because the externalSubjectId = null (owner is TENANT_ONE)
         SpecificAssetId asset1 = TestUtil.createSpecificAssetId( keyPrefix + "defaultClosed", "value_1", null );
         // asset2 is visible for everyone, because externalSubjectId = PUBLIC_READABLE
         SpecificAssetId asset2 = TestUtil.createSpecificAssetId( keyPrefix + "public_visible", "value_2", List.of( getExternalSubjectIdWildcardPrefix() ) );
         shellPayload.setSpecificAssetIds( List.of( asset1, asset2 ) );

         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         SpecificAssetId sa1 = TestUtil.createSpecificAssetId( keyPrefix + "defaultClosed", "value_1", null );
         SpecificAssetId sa2 = TestUtil.createSpecificAssetId( keyPrefix + "public_visible", "value_2", null );

         String encodedSa1 = Base64.getUrlEncoder().encodeToString( serialize( sa1 ) );
         String encodedSa2 = Base64.getUrlEncoder().encodeToString( serialize( sa2 ) );

         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "assetIds", encodedSa1 )
                           .queryParam( "assetIds", encodedSa2 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               .andExpect( jsonPath( "$.result", contains( shellPayload.getId() ) ) );

         // test with tenantTwo: returns no shellId because specificAssetId sa1 is only visible for Owner.
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( LOOKUP_SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "assetIds", encodedSa1 )
                           .queryParam( "assetIds", encodedSa2 )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result", hasSize( 0 ) ) );
      }
   }

   /**
    * The specificAssetId#externalSubjectId indicates which tenant is allowed to see the shell with all properties or not.
    */
   @Nested
   @DisplayName( "Tenant based Shell visibility test" )
   class TenantBasedShellVisibilityTest {

      String keyPrefix;

      @BeforeEach
      public void before() {
         removedAllShells();
         keyPrefix = UUID.randomUUID().toString();
      }

      @Test
      public void testGetAllShellsByOwningTenantId() throws Exception {
         AssetAdministrationShellDescriptor shellPayload = TestUtil
               .createCompleteAasDescriptor( keyPrefix + "semanticId", "http://example.com/" );
         shellPayload.setId( keyPrefix );
         List<SpecificAssetId> shellpayloadSpecificAssetIDs = shellPayload.getSpecificAssetIds();
         // Make all specificAssetIds to closed with externalSubjectId==null.
         shellpayloadSpecificAssetIDs.forEach( specificAssetId -> specificAssetId.setExternalSubjectId( null ) );
         shellPayload.setSpecificAssetIds( shellpayloadSpecificAssetIDs );

         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         // Request with owner TenantId (TENANT_ONE) returns one shell
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result" ).exists() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               .andExpect( jsonPath( "$.result[0].description[*]" ).isNotEmpty() )
               .andExpect( jsonPath( "$.result[0].idShort", is( shellPayload.getIdShort() ) ) );

         // Request with TenantId (TENANT_TWO) returns no shells, because the shell not includes the externalSubjectId of Tenant_two as specificId
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result" ).exists() )
               .andExpect( jsonPath( "$.result", hasSize( 0 ) ) );
      }

      @Test
      public void testGetAllShellsWithPublicAccessByTenantId() throws Exception {
         // the keyPrefix ensures that this test can run against a persistent database multiple times
         AssetAdministrationShellDescriptor shellPayload = TestUtil
               .createCompleteAasDescriptor( keyPrefix + "semanticId", "http://example.com/" );
         shellPayload.setId( keyPrefix );
         shellPayload.setSpecificAssetIds( null );

         // asset1 is only visible for the owner because the externalSubjectId = null
         SpecificAssetId asset1 = TestUtil.createSpecificAssetId( keyPrefix + "defaultClosed", "value_1", null );
         // asset2 is visible for everyone, because externalSubjectId = PUBLIC_READABLE and specificAssetKey is manufacturerPartId (which is in the list of allowedTypes via application.yml)
         SpecificAssetId asset2 = TestUtil.createSpecificAssetId( "manufacturerPartId", keyPrefix + "value_2",
               List.of( getExternalSubjectIdWildcardPrefix() ) );
         // asset3 is visible for tenantTwo, because externalSubjectId = tenantTwo
         SpecificAssetId asset3 = TestUtil.createSpecificAssetId( keyPrefix + "tenantTwo", "value_2_public",
               List.of( jwtTokenFactory.tenantTwo().getTenantId() ) );

         shellPayload.setSpecificAssetIds( List.of( asset1, asset2, asset3 ) );
         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         // Request with TenantId (TENANT_TWO) returns one shell with extend visibility of shell-properties, because tenantId is included in the specificAssetIds
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result" ).exists() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               .andExpect( jsonPath( "$.result[0].description[*]" ).isNotEmpty() )
               .andExpect( jsonPath( "$.result[0].idShort", is( shellPayload.getIdShort() ) ) )
               .andExpect( jsonPath( "$.result[0].id", is( shellPayload.getId() ) ) )
               .andExpect( jsonPath( "$.result[0].submodelDescriptors[*]" ).exists() )
               .andExpect( jsonPath( "$.result[0].specificAssetIds[*]" ).exists() );

         // Request with TenantId (TENANT_THREE) returns one shell with only public visible shell-properties
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SHELL_BASE_PATH )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantThree().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.result" ).exists() )
               .andExpect( jsonPath( "$.result", hasSize( 1 ) ) )
               .andExpect( jsonPath( "$.result[0].description[*]" ).doesNotExist() )
               .andExpect( jsonPath( "$.result[0].idShort" ).doesNotExist() )
               .andExpect( jsonPath( "$.result[0].id", is( shellPayload.getId() ) ) )
               .andExpect( jsonPath( "$.result[0].submodelDescriptors[*]" ).exists() )
               .andExpect( jsonPath( "$.result[0].specificAssetIds[*]" ).exists() );
      }

      @Test
      public void testGetShellByExternalIdByOwningTenantId() throws Exception {
         AssetAdministrationShellDescriptor shellPayload = TestUtil
               .createCompleteAasDescriptor( keyPrefix + "semanticId", "http://example.com/" );
         shellPayload.setId( keyPrefix );
         List<SpecificAssetId> shellpayloadSpecificAssetIDs = shellPayload.getSpecificAssetIds();
         // Make all specificAssetIds to closed with externalSubjectId==null.
         shellpayloadSpecificAssetIDs.forEach( specificAssetId -> specificAssetId.setExternalSubjectId( null ) );
         shellPayload.setSpecificAssetIds( shellpayloadSpecificAssetIDs );

         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         // Request with owner TenantId (TENANT_ONE) returns one shell
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SINGLE_SHELL_BASE_PATH, getEncodedValue( shellPayload.getId() ) )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantOne().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.description[*]" ).isNotEmpty() )
               .andExpect( jsonPath( "$.idShort", is( shellPayload.getIdShort() ) ) )
               .andExpect( jsonPath( "$.globalAssetId", is( shellPayload.getGlobalAssetId() ) ) );

         // Request with TenantId (TENANT_TWO) returns no shell, because the shell not includes the externalSubjectId of Tenant_two as specificId
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SINGLE_SHELL_BASE_PATH, getEncodedValue( shellPayload.getId() ) )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isNotFound() );
      }

      @Test
      public void testGetAllShellByExternalIdWithPublicAccessByTenantId() throws Exception {
         // the keyPrefix ensures that this test can run against a persistent database multiple times
         AssetAdministrationShellDescriptor shellPayload = TestUtil
               .createCompleteAasDescriptor( keyPrefix + "semanticId", "http://example.com/" );
         shellPayload.setId( keyPrefix );
         shellPayload.setSpecificAssetIds( null );

         // asset1 is only visible for the owner because the externalSubjectId = null
         SpecificAssetId asset1 = TestUtil.createSpecificAssetId( keyPrefix + "defaultClosed", "value_1", null );
         // asset2 is visible for everyone, because externalSubjectId = PUBLIC_READABLE and specificAssetKey is manufacturerPartId (which is in the list of allowedTypes via application.yml)
         SpecificAssetId asset2 = TestUtil.createSpecificAssetId( "manufacturerPartId", keyPrefix + "value_2",
               List.of( getExternalSubjectIdWildcardPrefix() ) );
         // asset3 is visible for tenantTwo, because externalSubjectId = tenantTwo
         SpecificAssetId asset3 = TestUtil.createSpecificAssetId( keyPrefix + "tenantTwo", "value_2_public",
               List.of( jwtTokenFactory.tenantTwo().getTenantId() ) );

         shellPayload.setSpecificAssetIds( List.of( asset1, asset2, asset3 ) );
         performShellCreateRequest( mapper.writeValueAsString( shellPayload ) );

         // Request with TenantId (TENANT_TWO) returns one shell with extend visibility of shell-properties, because tenantId is included in the specificAssetIds
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SINGLE_SHELL_BASE_PATH, getEncodedValue( shellPayload.getId() ) )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantTwo().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.description[*]" ).isNotEmpty() )
               .andExpect( jsonPath( "$.globalAssetId", is( shellPayload.getGlobalAssetId() ) ) )
               .andExpect( jsonPath( "$.idShort", is( shellPayload.getIdShort() ) ) )
               .andExpect( jsonPath( "$.id", is( shellPayload.getId() ) ) )
               .andExpect( jsonPath( "$.submodelDescriptors[*]" ).exists() )
               .andExpect( jsonPath( "$.specificAssetIds[*]" ).exists() );

         // Request with TenantId (TENANT_THREE) returns one shell with only public visible shell-properties
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( SINGLE_SHELL_BASE_PATH, getEncodedValue( shellPayload.getId() ) )
                           .header( EXTERNAL_SUBJECT_ID_HEADER, jwtTokenFactory.tenantThree().getTenantId() )
                           .queryParam( "pageSize", "100" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() )
               .andExpect( jsonPath( "$.description[*]" ).doesNotExist() )
               .andExpect( jsonPath( "$.idShort" ).doesNotExist() )
               .andExpect( jsonPath( "$.globalAssetId" ).doesNotExist() )
               .andExpect( jsonPath( "$.id", is( shellPayload.getId() ) ) )
               .andExpect( jsonPath( "$.submodelDescriptors[*]" ).exists() )
               .andExpect( jsonPath( "$.specificAssetIds[*]" ).exists() );
      }
   }

   @Nested
   @DisplayName( "Description Authentication Tests" )
   class DescriptionApiTest {

      @Test
      public void testGetDescriptionOnlyDeleteRoleExpectForbidden() throws Exception {
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( "/api/v3/description" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isForbidden() );
      }

      @Test
      public void testGetDescriptionNoRoleExpectForbidden() throws Exception {
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( "/api/v3/description" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isForbidden() );
      }

      @Test
      public void testGetDescriptionReadRoleExpectSuccess() throws Exception {
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( "/api/v3/description" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isOk() );
      }

      @Test
      public void testGetDescriptionReadRoleExpectUnauthorized() throws Exception {
         mvc.perform(
                     MockMvcRequestBuilders
                           .get( "/api/v3/description" )
                           .accept( MediaType.APPLICATION_JSON )
               )
               .andDo( MockMvcResultHandlers.print() )
               .andExpect( status().isUnauthorized() );
      }
   }
}
