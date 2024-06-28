package com.ssd.mvd.mockitoTests;

import com.ssd.mvd.constants.ErrorResponse;
import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.controller.SerDes;

import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@ExtendWith( value = MockitoExtension.class )
@TestInstance( value = TestInstance.Lifecycle.PER_CLASS )
public final class GaiServiceAPICheckTests {
    private final String testToken = GaiServiceAPICheckTests.class.getName();
    private final String testNumber = "01D819CC";

    private SerDes serDes;

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
    private Mono< Doverennost > doverennostMono;
    private Mono< ErrorResponse > errorResponseMono;
    private Mono< ViolationsList > violationsListMono;
    private Mono< ModelForCarList > modelForCarListMono;
    private Mono< DoverennostList > doverennostListMono;
    private Mono< ViolationsInformation > violationsInformationMono;

    @Mock
    private List< ViolationsInformation > violationsInformationsList;

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
        this.doverennostMono = Mockito.mock( Mono.class );
        this.errorResponseMono = Mockito.mock( Mono.class );
        this.violationsListMono = Mockito.mock( Mono.class );
        this.modelForCarListMono = Mockito.mock( Mono.class );
        this.doverennostListMono = Mockito.mock( Mono.class );
        this.violationsInformationMono = Mockito.mock( Mono.class );
    }

    @AfterAll
    public void endUp () throws Exception {
        SerDes.getSerDes().close();
        this.autoCloseable.close();
    }

    @Test
    @DisplayName( value = "testGaiToken method" )
    public void testGaiToken () {
        Mockito.when(
                SerDes.getSerDes()
        ).thenReturn( this.serDes );

        assertThat( this.serDes ).isNotNull();

        Mockito.when( this.serDes.getTokenForGai() ).thenReturn( this.testToken );

        assertThat( this.testToken ).isBlank();

        Mockito.verify( this.serDes ).getTokenForGai();
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
                .tookLessThan( Duration.ofMillis( 5000 ) );

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

        final ModelForCar modelForCar = SerDes
                .getSerDes()
                .getGetVehicleData()
                .apply( this.testNumber )
                .block();

        assertNotNull( modelForCar );
        assertNotNull( modelForCar.getTonirovka() );
        assertFalse( modelForCar.getTonirovka().getDateBegin().isBlank() );
        assertFalse( modelForCar.getTonirovka().getDateValid().isBlank() );
        assertFalse( modelForCar.getTonirovka().getTintinType().isBlank() );
        assertFalse( modelForCar.getTonirovka().getDateOfValidotion().isBlank() );
        assertFalse( modelForCar.getTonirovka().getDateOfPermission().isBlank() );
        assertFalse( modelForCar.getTonirovka().getPermissionLicense().isBlank() );
        assertFalse( modelForCar.getTonirovka().getWhoGavePermission().isBlank() );
        assertFalse( modelForCar.getTonirovka().getOrganWhichGavePermission().isBlank() );

        assertNotNull( modelForCar.getInsurance() );
        assertNull( modelForCar.getInsurance().getErrorResponse() );

        assertFalse( modelForCar.getInsurance().getDateBegin().isBlank() );
        assertFalse( modelForCar.getInsurance().getDateValid().isBlank() );
        assertFalse( modelForCar.getInsurance().getTintinType().isBlank() );

        assertNull( modelForCar.getErrorResponse() );

        assertNotNull( modelForCar.getDoverennostList() );
        assertNotNull( modelForCar.getDoverennostList().getDoverennostsList() );
        assertFalse( modelForCar.getDoverennostList().getDoverennostsList().isEmpty() );
        assertNotNull( modelForCar.getDoverennostList().getDoverennostsList().getFirst() );

        assertFalse( modelForCar.getDoverennostList().getDoverennostsList().getFirst().getIssuedBy().isBlank() );
        assertFalse( modelForCar.getDoverennostList().getDoverennostsList().getFirst().getDateBegin().isBlank() );
        assertFalse( modelForCar.getDoverennostList().getDoverennostsList().getFirst().getDateValid().isBlank() );

        assertFalse( modelForCar.getStir().isBlank() );
        assertFalse( modelForCar.getYear().isBlank() );
        assertFalse( modelForCar.getPinpp().isBlank() );
        assertFalse( modelForCar.getPower().isBlank() );
        assertFalse( modelForCar.getSeats().isBlank() );
        assertFalse( modelForCar.getModel().isBlank() );
        assertFalse( modelForCar.getColor().isBlank() );
        assertFalse( modelForCar.getKuzov().isBlank() );
        assertFalse( modelForCar.getStands().isBlank() );
        assertFalse( modelForCar.getEngine().isBlank() );
        assertFalse( modelForCar.getPerson().isBlank() );
        assertFalse( modelForCar.getAddress().isBlank() );
        assertFalse( modelForCar.getFuelType().isBlank() );
        assertFalse( modelForCar.getFullWeight().isBlank() );
        assertFalse( modelForCar.getAdditional().isBlank() );
        assertFalse( modelForCar.getPlateNumber().isBlank() );
        assertFalse( modelForCar.getVehicleType().isBlank() );
        assertFalse( modelForCar.getEmptyWeight().isBlank() );
        assertFalse( modelForCar.getOrganization().isBlank() );
        assertFalse( modelForCar.getRegistrationDate().isBlank() );
        assertFalse( modelForCar.getTexPassportSerialNumber().isBlank() );

        modelForCar.getDoverennostList().close();
    }

    @Test
    @DisplayName( value = "testModelForCarList method" )
    public void testModelForCarList () {
        final ModelForCarList modelForCarList = SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( this.testNumber )
                .block();

        assertNotNull( modelForCarList );
        assertNull( modelForCarList.getErrorResponse() );

        assertNotNull( modelForCarList.getModelForCarList() );
        assertFalse( modelForCarList.getModelForCarList().isEmpty() );

        assertNotNull( modelForCarList.getModelForCarList().getFirst() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList() );
        assertNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getErrorResponse() );

        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().isEmpty() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().getFirst() );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                .getFirst()
                        .getIssuedBy()
                        .isBlank()
        );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                .getFirst()
                        .getDateBegin()
                        .isBlank()
        );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                .getFirst()
                        .getDateValid()
                        .isBlank()
        );

        assertFalse( modelForCarList.getModelForCarList().getFirst().getStir().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getYear().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPinpp().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPower().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getSeats().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getModel().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getColor().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getKuzov().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getStands().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getEngine().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPerson().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getAddress().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getFuelType().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getFullWeight().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getAdditional().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPlateNumber().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getVehicleType().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getEmptyWeight().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getOrganization().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getRegistrationDate().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getTexPassportSerialNumber().isBlank() );

        modelForCarList.close();
    }

    @Test
    @DisplayName( value = "testVehicleTonirovka method" )
    public void testVehicleTonirovka () {
        final Tonirovka tonirovka = SerDes
                .getSerDes()
                .getGetVehicleTonirovka()
                .apply( this.testNumber )
                .block();

        assertNotNull( tonirovka );
        assertNull( tonirovka.getErrorResponse() );

        assertFalse( tonirovka.getDateBegin().isBlank() );
        assertFalse( tonirovka.getDateValid().isBlank() );
        assertFalse( tonirovka.getTintinType().isBlank() );
        assertFalse( tonirovka.getDateOfValidotion().isBlank() );
        assertFalse( tonirovka.getDateOfPermission().isBlank() );
        assertFalse( tonirovka.getPermissionLicense().isBlank() );
        assertFalse( tonirovka.getWhoGavePermission().isBlank() );
        assertFalse( tonirovka.getOrganWhichGavePermission().isBlank() );
    }

    @Test
    @DisplayName( value = "testFindAllAboutCarList method" )
    public void testFindAllAboutCarList () {
        ModelForCarList modelForCarList = SerDes
                .getSerDes()
                .getGetModelForCarList()
                .apply( this.testNumber )
                .block();

        assertNotNull( modelForCarList );
        assertNull( modelForCarList.getErrorResponse() );

        assertNotNull( modelForCarList.getModelForCarList() );
        assertFalse( modelForCarList.getModelForCarList().isEmpty() );

        assertNotNull( modelForCarList.getModelForCarList().getFirst() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList() );
        assertNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getErrorResponse() );

        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().isEmpty() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().getFirst() );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getIssuedBy()
                        .isBlank()
        );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateBegin()
                        .isBlank()
        );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateValid()
                        .isBlank()
        );

        assertFalse( modelForCarList.getModelForCarList().getFirst().getStir().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getYear().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPinpp().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPower().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getSeats().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getModel().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getColor().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getKuzov().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getStands().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getEngine().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPerson().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getAddress().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getFuelType().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getFullWeight().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getAdditional().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPlateNumber().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getVehicleType().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getEmptyWeight().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getOrganization().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getRegistrationDate().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getTexPassportSerialNumber().isBlank() );

        modelForCarList = SerDes
                .getSerDes()
                .getFindAllAboutCarList()
                .apply( modelForCarList )
                .block();

        assertNotNull( modelForCarList );
        assertNull( modelForCarList.getErrorResponse() );

        assertNotNull( modelForCarList.getModelForCarList() );
        assertFalse( modelForCarList.getModelForCarList().isEmpty() );

        assertNotNull( modelForCarList.getModelForCarList().getFirst() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList() );
        assertNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getErrorResponse() );

        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().isEmpty() );
        assertNotNull( modelForCarList.getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().getFirst() );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getIssuedBy()
                        .isBlank()
        );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateBegin()
                        .isBlank()
        );

        assertFalse(
                modelForCarList
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateValid()
                        .isBlank()
        );

        assertFalse( modelForCarList.getModelForCarList().getFirst().getStir().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getYear().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPinpp().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPower().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getSeats().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getModel().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getColor().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getKuzov().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getStands().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getEngine().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPerson().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getAddress().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getFuelType().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getFullWeight().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getAdditional().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getPlateNumber().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getVehicleType().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getEmptyWeight().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getOrganization().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getRegistrationDate().isBlank() );
        assertFalse( modelForCarList.getModelForCarList().getFirst().getTexPassportSerialNumber().isBlank() );

        modelForCarList.close();
    }

    @Test
    @DisplayName( value = "testVehicleViolationList method" )
    public void testVehicleViolationList () {
        final ViolationsList violationsList = SerDes
                .getSerDes()
                .getGetViolationList()
                .apply( this.testNumber )
                .block();

        assertNotNull( violationsList );
        assertNotNull( violationsList.getErrorResponse() );

        assertNotNull( violationsList );
        assertNotNull( violationsList.getViolationsInformationsList() );
        assertFalse( violationsList.getViolationsInformationsList().isEmpty() );
        assertNotNull( violationsList.getViolationsInformationsList().getFirst() );

        assertFalse( violationsList.getViolationsInformationsList().getFirst().getAmount() < 0 );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getDecreeStatus() == 0 );

        assertFalse( violationsList.getViolationsInformationsList().getFirst().getBill().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getModel().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getOwner().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getPayDate().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getAddress().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getArticle().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getDivision().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getViolation().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getDecreeSerialNumber().isBlank() );

        violationsList.close();
    }

    @Test
    @DisplayName( value = "testVehicleDoverennostList method" )
    public void testVehicleDoverennostList () {
        final DoverennostList doverennostList = SerDes
                .getSerDes()
                .getGetDoverennostList()
                .apply( this.testNumber )
                .block();

        assertNotNull( doverennostList );
        assertNull( doverennostList.getErrorResponse() );

        assertNotNull( doverennostList );
        assertNotNull( doverennostList.getDoverennostsList() );
        assertFalse( doverennostList.getDoverennostsList().isEmpty() );
        assertNotNull( doverennostList.getDoverennostsList().getFirst() );

        assertFalse( doverennostList.getDoverennostsList().getFirst().getIssuedBy().isBlank() );
        assertFalse( doverennostList.getDoverennostsList().getFirst().getDateBegin().isBlank() );
        assertFalse( doverennostList.getDoverennostsList().getFirst().getDateValid().isBlank() );

        doverennostList.close();
    }
}
