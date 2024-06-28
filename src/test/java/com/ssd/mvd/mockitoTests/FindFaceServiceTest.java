package com.ssd.mvd.mockitoTests;

import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.PapilonData;
import com.ssd.mvd.entity.Violation;
import com.ssd.mvd.entity.Results;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.*;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@ExtendWith( value = MockitoExtension.class )
@TestInstance( value = TestInstance.Lifecycle.PER_CLASS )
public final class FindFaceServiceTest {
    private final String name = "SARVAR";
    private final String pinfl = "12345678901234";

    private Results results;
    private Violation violation;
    private PapilonData papilonData;

    private Mono< List > listMono;
    private Mono< Results > resultsMono;

    private FindFaceComponent findFaceComponent;

    @Mock
    private List< PapilonData > resultsList;

    @Mock
    private List< Violation > violationList;

    private final AutoCloseable autoCloseable = MockitoAnnotations.openMocks( this );

    @BeforeEach
    public void setUp () {
        this.results = Mockito.mock( Results.class );
        this.violation = Mockito.mock( Violation.class );
        this.resultsMono = Mockito.mock( Mono.class );
        this.papilonData = Mockito.mock( PapilonData.class );
        this.findFaceComponent = Mockito.mock( FindFaceComponent.class );
    }

    @AfterAll
    public void endUp () throws Exception {
        FindFaceComponent.getInstance().close();
        this.autoCloseable.close();
        this.results.close();
    }

    @Test
    @DisplayName( value = "testPapilonList method" )
    public void testPapilonList () {
        Mockito.when(
                FindFaceComponent.getInstance()
        ).thenReturn( this.findFaceComponent );

        assertThat( this.findFaceComponent ).isNotNull();

        Mockito.when(
                this.findFaceComponent.getPapilonList.apply( this.pinfl )
        ).thenReturn( this.resultsMono );

        StepVerifier.create( this.resultsMono )
                .expectNext( this.results )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( Duration.ofMillis( 5000 ) );

        assertThat( this.results ).isNotNull();
        assertThat( results.getResult_code() == 0 ).isTrue();

        Mockito.verify(
                this.findFaceComponent
        ).getPapilonList.apply( this.pinfl );

        Mockito.when(
                this.results.getResults()
        ).thenReturn( this.resultsList );

        assertThat( results.getResults() ).isNotNull();
        assertThat( results.getResults() ).isNotEmpty();

        Mockito.verify( this.results ).getResults();

        Mockito.when(
                this.resultsList.getFirst()
        ).thenReturn( this.papilonData );

        assertThat( this.papilonData ).isNotNull();

        Mockito.verify( this.resultsList ).getFirst();

        Mockito.when( this.papilonData.getRank() ).thenReturn( 0 );

        assertThat( papilonData.getRank() == 0 ).isFalse();

        Mockito.verify( this.papilonData ).getRank();

        Mockito.when( this.papilonData.getScore() ).thenReturn( 0.0 );

        assertThat( papilonData.getScore() == 0.0 ).isFalse();

        Mockito.verify( this.papilonData ).getScore();

        Mockito.when( this.papilonData.getName() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.papilonData ).getName();

        Mockito.when( this.papilonData.getPhoto() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.papilonData ).getPhoto();

        Mockito.when( this.papilonData.getBirth() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.papilonData ).getBirth();

        Mockito.when( this.papilonData.getCountry() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.papilonData ).getCountry();

        Mockito.when( this.papilonData.getPassport() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.papilonData ).getPassport();

        Mockito.when( this.papilonData.getPersonal_code() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.papilonData ).getPersonal_code();

        Mockito.when( this.results.getViolationList() ).thenReturn( this.violationList );

        assertThat( this.results.getViolationList() ).isNotNull();
        assertThat( this.results.getViolationList() ).isNotEmpty();

        Mockito.verify( this.results ).getViolationList();

        assertThat( this.violation ).isNotNull();

        Mockito.when( this.violationList.getFirst() ).thenReturn( this.violation );

        Mockito.when( this.violation.getProtocol_id() ).thenReturn( 0L );

        assertThat( this.violation.getProtocol_id() ).isEqualTo( 0L );

        Mockito.verify( this.violation ).getProtocol_id();

        Mockito.when( this.violation.getPinpp() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getPinpp();

        Mockito.when( this.violation.getDecision() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getDecision();

        Mockito.when( this.violation.getPunishment() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getPunishment();

        Mockito.when( this.violation.getLast_name_lat() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getLast_name_lat();

        Mockito.when( this.violation.getFirst_name_lat() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getFirst_name_lat();

        Mockito.when( this.violation.getProtocol_series() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getProtocol_series();

        Mockito.when( this.violation.getProtocol_number() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getProtocol_number();

        Mockito.when( this.violation.getSecond_name_lat() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getSecond_name_lat();

        Mockito.when( this.violation.getResolution_time() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getResolution_time();

        Mockito.when( this.violation.getAdm_case_number() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getAdm_case_number();

        Mockito.when( this.violation.getAdm_case_series() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getAdm_case_series();

        Mockito.when( this.violation.getViolation_article() ).thenReturn( this.name );

        assertThat( this.name.isBlank() ).isFalse();

        Mockito.verify( this.violation ).getViolation_article();
    }

    @Test
    @DisplayName( value = "testViolationListByPinfl method" )
    public void testViolationListByPinfl () {
        Mockito.when(
                FindFaceComponent.getInstance()
        ).thenReturn( this.findFaceComponent );

        assertThat( this.findFaceComponent ).isNotNull();

        Mockito.when(
                this.findFaceComponent
                        .getViolationListByPinfl
                        .apply( this.pinfl )
        ).thenReturn( this.listMono );

        StepVerifier.create( this.listMono )
                .expectNext( this.violationList )
                .expectComplete()
                .verifyThenAssertThat()
                .tookLessThan( Duration.ofMillis( 5000 ) );

        Mockito.verify(
                this.findFaceComponent
        ).getViolationListByPinfl.apply( this.pinfl );
    }
}
