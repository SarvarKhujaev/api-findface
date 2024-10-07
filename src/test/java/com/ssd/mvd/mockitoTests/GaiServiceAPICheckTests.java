package com.ssd.mvd.mockitoTests;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.inspectors.SerDes;

import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.Mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.*;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.CopyOnWriteArrayList;
import java.time.Duration;
import java.util.Date;

@ExtendWith( value = MockitoExtension.class )
@TestInstance( value = TestInstance.Lifecycle.PER_CLASS )
public final class GaiServiceAPICheckTests {
    private final String testDate = new Date().toString();
    private final String testNumber = "01D819CC";

    private SerDes serDes;
    private final Duration duration = Duration.ofMillis( 5000 );

    private Insurance insurance;
    private Tonirovka tonirovka;
    private ModelForCar modelForCar;
    private Doverennost doverennost;
    private ErrorResponse errorResponse;
    private ViolationsList violationsList;
    private ModelForCarList modelForCarList;
    private DoverennostList doverennostList;
    private ViolationsInformation violationsInformation;

    private Mono< Insurance > insuranceMono;
    private Mono< Tonirovka > tonirovkaMono;
    private Mono< ModelForCar > modelForCarMono;
    private Mono< ViolationsList > violationsListMono;
    private Mono< ModelForCarList > modelForCarListMono;
    private Mono< DoverennostList > doverennostListMono;

    @Mock
    private CopyOnWriteArrayList< Doverennost > doverennosts;

    @Mock
    private CopyOnWriteArrayList< ModelForCar > modelForCars;

    @Mock
    private CopyOnWriteArrayList< ViolationsInformation > violationsInformationsList;

    private final AutoCloseable autoCloseable = MockitoAnnotations.openMocks( this );

    @BeforeEach
    public void setUp () {
        SerDes.getSerDes();

        this.serDes = Mockito.mock( SerDes.class );
        this.insurance = Mockito.mock( Insurance.class );
        this.tonirovka = Mockito.mock( Tonirovka.class );
        this.modelForCar = Mockito.mock( ModelForCar.class );
        this.doverennost = Mockito.mock( Doverennost.class );
        this.errorResponse = Mockito.mock( ErrorResponse.class );
        this.violationsList = Mockito.mock( ViolationsList.class );
        this.modelForCarList = Mockito.mock( ModelForCarList.class );
        this.doverennostList = Mockito.mock( DoverennostList.class );
        this.violationsInformation = Mockito.mock( ViolationsInformation.class );

        this.insuranceMono = Mockito.mock( Mono.class );
        this.tonirovkaMono = Mockito.mock( Mono.class );
        this.modelForCarMono = Mockito.mock( Mono.class );
        this.violationsListMono = Mockito.mock( Mono.class );
        this.modelForCarListMono = Mockito.mock( Mono.class );
        this.doverennostListMono = Mockito.mock( Mono.class );
    }

    @AfterAll
    public void endUp () throws Exception {
        SerDes.getSerDes().close();
        this.autoCloseable.close();
    }

    @Test
    @DisplayName( value = "testInsurance method" )
    public void testInsurance () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when(
                this.serDes
                        .getInsurance()
                        .apply( this.testNumber )
        ).thenReturn( this.insuranceMono );

        StepVerifier.create( this.insuranceMono )
                .expectNext( this.insurance )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( this.duration );

        Mockito.verify( this.serDes )
                .getInsurance()
                .apply( this.testNumber );

        assertThat( this.insurance ).isNotNull();

        Mockito.when( this.insurance.getErrorResponse() ).thenReturn( this.errorResponse );

        assertThat( this.errorResponse ).isNotNull();

        Mockito.verify( this.insurance ).getErrorResponse();

        Mockito.when( this.insurance.getDateBegin() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotNull();
        assertThat( this.testNumber ).isNotEmpty();

        Mockito.verify( this.insurance ).getDateBegin();

        Mockito.when( this.insurance.getDateValid() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotNull();
        assertThat( this.testNumber ).isNotEmpty();

        Mockito.verify( this.insurance ).getDateValid();

        Mockito.when( this.insurance.getTintinType() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotNull();
        assertThat( this.testNumber ).isNotEmpty();

        Mockito.verify( this.insurance ).getTintinType();
    }

    @Test
    @DisplayName( value = "testVehicleData method" )
    public void testVehicleData () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when(
                this.serDes
                        .getGetVehicleData()
                        .apply( this.testNumber )
        ).thenReturn( this.modelForCarMono );

        StepVerifier.create( this.modelForCarMono )
                .expectNext( this.modelForCar )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( this.duration );

        assertThat( this.modelForCar ).isNotNull();

        Mockito.verify(
                this.serDes
        ).getGetVehicleData()
                .apply( this.testNumber );

        Mockito.when( this.modelForCar.getTonirovka() ).thenReturn( this.tonirovka );

        assertThat( this.tonirovka ).isNotNull();

        Mockito.verify( this.modelForCar ).getTonirovka();

        Mockito.when( this.tonirovka.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateBegin();

        Mockito.when( this.tonirovka.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateValid();

        Mockito.when( this.tonirovka.getTintinType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getTintinType();

        Mockito.when( this.tonirovka.getDateOfValidotion() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateOfValidotion();

        Mockito.when( this.tonirovka.getPermissionLicense() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getPermissionLicense();

        Mockito.when( this.tonirovka.getWhoGavePermission() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getWhoGavePermission();

        Mockito.when( this.tonirovka.getOrganWhichGavePermission() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getOrganWhichGavePermission();

        Mockito.when( this.modelForCar.getInsurance() ).thenReturn( this.insurance );

        assertThat( this.insurance ).isNotNull();

        Mockito.verify( this.modelForCar ).getTonirovka();

        Mockito.when( this.modelForCar.getErrorResponse() ).thenReturn( this.errorResponse );

        assertThat( this.errorResponse ).isNotNull();

        Mockito.verify( this.modelForCar ).getTonirovka();

        Mockito.when( this.tonirovka.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateBegin();

        Mockito.when( this.tonirovka.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateValid();

        Mockito.when( this.tonirovka.getTintinType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getTintinType();

        Mockito.when( this.modelForCar.getDoverennostList() ).thenReturn( this.doverennostList );

        assertThat( this.doverennostList ).isNotNull();

        Mockito.verify( this.modelForCar ).getDoverennostList();

        Mockito.when( this.doverennostList.getDoverennostsList() ).thenReturn( this.doverennosts );

        assertThat( this.doverennosts ).isNotNull();
        assertThat( this.doverennosts ).isNotEmpty();

        Mockito.verify( this.doverennostList ).getDoverennostsList();

        Mockito.when( this.doverennosts.get( 0 ) ).thenReturn( this.doverennost );

        assertThat( this.doverennost ).isNotNull();

        Mockito.verify( this.doverennosts ).get( 0 );

        Mockito.when( this.doverennost.getIssuedBy() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getIssuedBy();

        Mockito.when( this.doverennost.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateBegin();

        Mockito.when( this.doverennost.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateValid();

        Mockito.when( this.tonirovka.getTintinType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getTintinType();

        Mockito.when( this.modelForCar.getStir() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getStir();

        Mockito.when( this.modelForCar.getYear() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getYear();

        Mockito.when( this.modelForCar.getPinpp() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPinpp();

        Mockito.when( this.modelForCar.getPower() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPower();

        Mockito.when( this.modelForCar.getSeats() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getSeats();

        Mockito.when( this.modelForCar.getModel() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getModel();

        Mockito.when( this.modelForCar.getColor() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getColor();

        Mockito.when( this.modelForCar.getKuzov() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getKuzov();

        Mockito.when( this.modelForCar.getStands() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getStands();

        Mockito.when( this.modelForCar.getEngine() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getEngine();

        Mockito.when( this.modelForCar.getPerson() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPerson();

        Mockito.when( this.modelForCar.getAddress() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getAddress();

        Mockito.when( this.modelForCar.getFuelType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getFuelType();

        Mockito.when( this.modelForCar.getFullWeight() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getFullWeight();

        Mockito.when( this.modelForCar.getAdditional() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getAdditional();

        Mockito.when( this.modelForCar.getPlateNumber() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPlateNumber();

        Mockito.when( this.modelForCar.getVehicleType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getVehicleType();

        Mockito.when( this.modelForCar.getEmptyWeight() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getEmptyWeight();

        Mockito.when( this.modelForCar.getOrganization() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getOrganization();

        Mockito.when( this.modelForCar.getRegistrationDate() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getRegistrationDate();

        Mockito.when( this.modelForCar.getTexPassportSerialNumber() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getTexPassportSerialNumber();

        this.doverennostList.close();
    }

    @Test
    @DisplayName( value = "testModelForCarList method" )
    public void testModelForCarList () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when(
                this.serDes
                        .getGetModelForCarList()
                        .apply( this.testNumber )
        ).thenReturn( this.modelForCarListMono );

        StepVerifier.create( this.modelForCarListMono )
                .expectNext( this.modelForCarList )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( this.duration );

        assertThat( this.modelForCarList ).isNotNull();

        Mockito.verify(
                this.serDes
        ).getGetModelForCarList()
                .apply( this.testNumber );

        Mockito.when( this.modelForCarList.getErrorResponse() ).thenReturn( this.errorResponse );

        assertThat( this.errorResponse ).isNull();

        Mockito.verify( this.modelForCarList ).getErrorResponse();

        Mockito.when( this.modelForCarList.getModelForCarList() ).thenReturn( this.modelForCars );

        assertThat( this.modelForCars ).isNotNull();
        assertThat( this.modelForCars ).isNotEmpty();

        Mockito.verify( this.modelForCarList ).getModelForCarList();

        Mockito.when( this.modelForCars.get( 0 ) ).thenReturn( this.modelForCar );

        assertThat( this.modelForCar ).isNotNull();

        Mockito.verify( this.modelForCars ).get( 0 );

        Mockito.when( this.modelForCar.getDoverennostList() ).thenReturn( this.doverennostList );

        assertThat( this.doverennostList ).isNotNull();

        Mockito.verify( this.modelForCar ).getDoverennostList();

        Mockito.when( this.doverennostList.getDoverennostsList() ).thenReturn( this.doverennosts );

        assertThat( this.doverennosts ).isNotNull();
        assertThat( this.doverennosts ).isNotEmpty();

        Mockito.verify( this.doverennostList ).getDoverennostsList();

        Mockito.when( this.doverennosts.get( 0 ) ).thenReturn( this.doverennost );

        assertThat( this.doverennost ).isNotNull();

        Mockito.verify( this.doverennosts ).get( 0 );

        Mockito.when( this.doverennost.getIssuedBy() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getIssuedBy();

        Mockito.when( this.doverennost.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateBegin();

        Mockito.when( this.doverennost.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateValid();

        this.modelForCarList.close();
    }

    @Test
    @DisplayName( value = "testVehicleTonirovka method" )
    public void testVehicleTonirovka () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when(
                this.serDes
                        .getGetVehicleTonirovka()
                        .apply( this.testNumber )
        ).thenReturn( this.tonirovkaMono );

        StepVerifier.create( this.tonirovkaMono )
                .expectNext( this.tonirovka )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( this.duration );

        Mockito.when( this.tonirovka.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateBegin();

        Mockito.when( this.tonirovka.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateValid();

        Mockito.when( this.tonirovka.getTintinType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getTintinType();

        Mockito.when( this.tonirovka.getDateOfValidotion() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateOfValidotion();

        Mockito.when( this.tonirovka.getPermissionLicense() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getPermissionLicense();

        Mockito.when( this.tonirovka.getWhoGavePermission() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getWhoGavePermission();

        Mockito.when( this.tonirovka.getOrganWhichGavePermission() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getOrganWhichGavePermission();
    }

    @Test
    @DisplayName( value = "testFindAllAboutCarList method" )
    public void testFindAllAboutCarList () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when(
                this.serDes
                        .getGetModelForCarList()
                        .apply( this.testNumber )
        ).thenReturn( this.modelForCarListMono );

        StepVerifier.create( this.modelForCarListMono )
                .expectNext( this.modelForCarList )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( this.duration );

        assertThat( this.modelForCarList ).isNotNull();

        Mockito.when( this.modelForCar.getErrorResponse() ).thenReturn( this.errorResponse );

        assertThat( this.errorResponse ).isNotNull();

        Mockito.verify( this.modelForCar ).getErrorResponse();

        Mockito.when( this.modelForCar.getTonirovka() ).thenReturn( this.tonirovka );

        assertThat( this.tonirovka ).isNotNull();

        Mockito.verify( this.modelForCar ).getTonirovka();

        Mockito.when( this.tonirovka.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateBegin();

        Mockito.when( this.tonirovka.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateValid();

        Mockito.when( this.tonirovka.getTintinType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getTintinType();

        Mockito.when( this.tonirovka.getDateOfValidotion() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateOfValidotion();

        Mockito.when( this.tonirovka.getPermissionLicense() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getPermissionLicense();

        Mockito.when( this.tonirovka.getWhoGavePermission() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getWhoGavePermission();

        Mockito.when( this.tonirovka.getOrganWhichGavePermission() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getOrganWhichGavePermission();

        Mockito.when( this.modelForCar.getInsurance() ).thenReturn( this.insurance );

        assertThat( this.insurance ).isNotNull();

        Mockito.verify( this.modelForCar ).getTonirovka();

        Mockito.when( this.modelForCar.getErrorResponse() ).thenReturn( this.errorResponse );

        assertThat( this.errorResponse ).isNotNull();

        Mockito.verify( this.modelForCar ).getTonirovka();

        Mockito.when( this.tonirovka.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateBegin();

        Mockito.when( this.tonirovka.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getDateValid();

        Mockito.when( this.tonirovka.getTintinType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getTintinType();

        Mockito.when( this.modelForCar.getDoverennostList() ).thenReturn( this.doverennostList );

        assertThat( this.doverennostList ).isNotNull();

        Mockito.verify( this.modelForCar ).getDoverennostList();

        Mockito.when( this.doverennostList.getDoverennostsList() ).thenReturn( this.doverennosts );

        assertThat( this.doverennosts ).isNotNull();
        assertThat( this.doverennosts ).isNotEmpty();

        Mockito.verify( this.doverennostList ).getDoverennostsList();

        Mockito.when( this.doverennosts.get( 0 ) ).thenReturn( this.doverennost );

        assertThat( this.doverennost ).isNotNull();

        Mockito.verify( this.doverennosts ).get( 0 );

        Mockito.when( this.doverennost.getIssuedBy() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getIssuedBy();

        Mockito.when( this.doverennost.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateBegin();

        Mockito.when( this.doverennost.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateValid();

        Mockito.when( this.tonirovka.getTintinType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.tonirovka ).getTintinType();

        Mockito.when( this.modelForCar.getStir() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getStir();

        Mockito.when( this.modelForCar.getYear() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getYear();

        Mockito.when( this.modelForCar.getPinpp() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPinpp();

        Mockito.when( this.modelForCar.getPower() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPower();

        Mockito.when( this.modelForCar.getSeats() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getSeats();

        Mockito.when( this.modelForCar.getModel() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getModel();

        Mockito.when( this.modelForCar.getColor() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getColor();

        Mockito.when( this.modelForCar.getKuzov() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getKuzov();

        Mockito.when( this.modelForCar.getStands() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getStands();

        Mockito.when( this.modelForCar.getEngine() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getEngine();

        Mockito.when( this.modelForCar.getPerson() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPerson();

        Mockito.when( this.modelForCar.getAddress() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getAddress();

        Mockito.when( this.modelForCar.getFuelType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getFuelType();

        Mockito.when( this.modelForCar.getFullWeight() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getFullWeight();

        Mockito.when( this.modelForCar.getAdditional() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getAdditional();

        Mockito.when( this.modelForCar.getPlateNumber() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getPlateNumber();

        Mockito.when( this.modelForCar.getVehicleType() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getVehicleType();

        Mockito.when( this.modelForCar.getEmptyWeight() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getEmptyWeight();

        Mockito.when( this.modelForCar.getOrganization() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getOrganization();

        Mockito.when( this.modelForCar.getRegistrationDate() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getRegistrationDate();

        Mockito.when( this.modelForCar.getTexPassportSerialNumber() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.modelForCar ).getTexPassportSerialNumber();

        this.doverennostList.close();
        this.modelForCarList.close();
    }

    @Test
    @DisplayName( value = "testVehicleViolationList method" )
    public void testVehicleViolationList () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when(
                this.serDes
                        .getGetViolationList()
                        .apply( this.testNumber )
        ).thenReturn( this.violationsListMono );

        StepVerifier.create( this.violationsListMono )
                .expectNext( this.violationsList )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( this.duration );

        assertThat( this.violationsList ).isNotNull();

        Mockito.verify(
                this.serDes
        ).getGetViolationList()
                .apply( this.testNumber );

        Mockito.when( this.violationsList.getViolationsInformationsList() ).thenReturn( this.violationsInformationsList );

        assertThat( this.violationsInformationsList ).isNotNull();
        assertThat( this.violationsInformationsList ).isNotEmpty();

        Mockito.verify( this.violationsList ).getViolationsInformationsList();

        Mockito.when( this.violationsInformationsList.get( 0 ) ).thenReturn( this.violationsInformation );

        assertThat( this.violationsInformation ).isNotNull();

        Mockito.verify( this.violationsInformationsList ).get( 0 );

        final int testValue = 0;

        Mockito.when( this.violationsInformation.getAmount() ).thenReturn( testValue );

        assertThat( testValue ).isGreaterThanOrEqualTo( 0 );

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getDecreeStatus() ).thenReturn( testValue );

        assertThat( testValue ).isGreaterThanOrEqualTo( 0 );

        Mockito.verify( this.violationsInformation ).getDecreeStatus();

        Mockito.when( this.violationsInformation.getBill() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getModel() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getOwner() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getPayDate() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getAddress() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getArticle() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getDivision() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getViolation() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        Mockito.when( this.violationsInformation.getDecreeSerialNumber() ).thenReturn( this.testNumber );

        assertThat( this.testNumber ).isNotBlank();

        Mockito.verify( this.violationsInformation ).getAmount();

        this.violationsList.close();
    }

    @Test
    @DisplayName( value = "testVehicleDoverennostList method" )
    public void testVehicleDoverennostList () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when(
                this.serDes
                        .getGetDoverennostList()
                        .apply( this.testNumber )
        ).thenReturn( this.doverennostListMono );

        StepVerifier.create( this.doverennostListMono )
                .expectNext( this.doverennostList )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( this.duration );

        assertThat( this.doverennostList ).isNotNull();

        Mockito.when( this.doverennostList.getDoverennostsList() ).thenReturn( this.doverennosts );

        assertThat( this.doverennosts ).isNotNull();
        assertThat( this.doverennosts ).isNotEmpty();

        Mockito.verify( this.doverennostList ).getDoverennostsList();

        Mockito.when( this.doverennosts.get( 0 ) ).thenReturn( this.doverennost );

        assertThat( this.doverennost ).isNotNull();

        Mockito.verify( this.doverennosts ).get( 0 );

        Mockito.when( this.doverennost.getIssuedBy() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getIssuedBy();

        Mockito.when( this.doverennost.getDateBegin() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateBegin();

        Mockito.when( this.doverennost.getDateValid() ).thenReturn( this.testDate );

        assertThat( this.testDate ).isNotNull();
        assertThat( this.testDate ).isNotBlank();
        assertThat( this.testDate ).isNotEmpty();

        Mockito.verify( this.doverennost ).getDateValid();

        this.doverennostList.close();
    }
}
