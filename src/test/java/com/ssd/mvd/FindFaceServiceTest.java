package com.ssd.mvd;

import com.ssd.mvd.component.FindFaceComponent;
import com.ssd.mvd.entity.Results;

import junit.framework.TestCase;
import java.util.List;

public final class FindFaceServiceTest extends TestCase {
    private final String pinfl = "12345678901234";

    @Override
    public void setUp () {
        FindFaceComponent.getInstance();
    }

    @Override
    public void tearDown () {
        FindFaceComponent.getInstance().close();
    }

    public void testPapilonList () {
        final Results results = FindFaceComponent
                .getInstance()
                .getPapilonList
                .apply( this.pinfl )
                .block();

        assertNotNull( results );
        assertFalse( results.getResult_code() == 0 );

        assertNotNull( results.getResults() );
        assertFalse( results.getResults().isEmpty() );
        assertFalse( results.getResults().getFirst().getRank() == 0 );
        assertFalse( results.getResults().getFirst().getScore() == 0 );
        assertFalse( results.getResults().getFirst().getScore() == 0.0 );

        assertFalse( results.getResults().getFirst().getName().isBlank() );
        assertFalse( results.getResults().getFirst().getPhoto().isBlank() );
        assertFalse( results.getResults().getFirst().getBirth().isBlank() );
        assertFalse( results.getResults().getFirst().getCountry().isBlank() );
        assertFalse( results.getResults().getFirst().getPassport().isBlank() );
        assertFalse( results.getResults().getFirst().getPersonal_code().isBlank() );

        assertNotNull( results.getViolationList() );
        assertFalse( results.getViolationList().isEmpty() );

        assertNotNull( results.getViolationList().getFirst() );
        assertFalse( results.getViolationList().getFirst().getProtocol_id() == 0L );

        assertFalse( results.getViolationList().getFirst().getPinpp().isBlank() );
        assertFalse( results.getViolationList().getFirst().getDecision().isBlank() );
        assertFalse( results.getViolationList().getFirst().getPunishment().isBlank() );
        assertFalse( results.getViolationList().getFirst().getLast_name_lat().isBlank() );
        assertFalse( results.getViolationList().getFirst().getViolation_time().isBlank() );
        assertFalse( results.getViolationList().getFirst().getFirst_name_lat().isBlank() );
        assertFalse( results.getViolationList().getFirst().getProtocol_series().isBlank() );
        assertFalse( results.getViolationList().getFirst().getProtocol_number().isBlank() );
        assertFalse( results.getViolationList().getFirst().getSecond_name_lat().isBlank() );
        assertFalse( results.getViolationList().getFirst().getResolution_time().isBlank() );
        assertFalse( results.getViolationList().getFirst().getAdm_case_number().isBlank() );
        assertFalse( results.getViolationList().getFirst().getAdm_case_series().isBlank() );
        assertFalse( results.getViolationList().getFirst().getViolation_article().isBlank() );

        results.close();
    }

    public void testViolationListByPinfl () {
        final List< ? > someList = FindFaceComponent
                .getInstance()
                .getViolationListByPinfl
                .apply( this.pinfl )
                .block();

        assertNotNull( someList );
        assertFalse( someList.isEmpty() );
        assertNotNull( someList.getFirst() );
    }
}
