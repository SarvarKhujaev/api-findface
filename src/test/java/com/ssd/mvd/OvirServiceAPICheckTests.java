package com.ssd.mvd;

import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.inspectors.EntitiesInstances;
import com.ssd.mvd.entity.response.ApiResponseModel;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.inspectors.SerDes;
import com.ssd.mvd.entity.response.Status;

import junit.framework.TestCase;
import java.util.Date;

public final class OvirServiceAPICheckTests extends TestCase {
    private final ApiResponseModel apiResponseModel = ApiResponseModel
            .builder()
            .status(
                    Status
                            .builder()
                            .code( 200L )
                            .message( "30096545789812" )
                            .build()
            ).build();

    @Override
    public void setUp () {
        SerDes.getSerDes();
    }

    @Override
    public void tearDown () {
        SerDes.getSerDes().close();
    }

    public void testCrossBoardInfo () {
        EntitiesInstances.CROSS_BOARD_INFO.set(
                SerDes
                        .getSerDes()
                        .getGetCrossBoardInfo()
                        .apply( this.apiResponseModel.status().getMessage() )
                        .block()
        );

        assertNotNull( EntitiesInstances.CROSS_BOARD_INFO.get() );
        assertNull( EntitiesInstances.CROSS_BOARD_INFO.get().getErrorResponse() );

        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getResult().isBlank() );

        assertNotNull( EntitiesInstances.CROSS_BOARD_INFO.get().getData() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().isEmpty() );
        assertNotNull( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getCrossBoardList().isEmpty() );
        assertNotNull( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getCrossBoardList().getFirst() );

        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPeriod_code() == 0
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrip_purpose_code() == 0
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDirection_country() == 0
        );

        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getCard_id() == 0L
        );

        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getCitizenship() == 0L
        );

        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getRegistrationDate()
                        .before( new Date() )
        );

        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPinpp()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getReg_date()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDocument()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getFull_name()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getBirth_date()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPoint_code()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getVisa_number()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getNationality()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrans_number()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrans_add_info()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDate_end_document()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDocument_type_code()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDirection_type_code()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrans_category_code()
                        .isBlank()
        );

        assertNotNull(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
        );

        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getPeriods()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getCountries()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getTripPurpose()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getDocumentType()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getNationalities()
                        .isBlank()
        );
        assertFalse(
                EntitiesInstances.CROSS_BOARD_INFO.get()
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getTransportCategory()
                        .isBlank()
        );

        assertNotNull( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson() );

        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getSex() == 0 );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getLivestatus() == 0 );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getTransaction_id() == 0 );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getCitizenshipid() == 0 );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getBirthcountryid() == 0 );

        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getNamelat().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getEngname().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getSurnamelat().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getEngsurname().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getBirth_date().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getNationality().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getPatronymlat().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getCitizenship().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getBirthcountry().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getCurrent_pinpp().isBlank() );
        assertFalse( EntitiesInstances.CROSS_BOARD_INFO.get().getData().getFirst().getPerson().getCurrent_document().isBlank() );

        EntitiesInstances.CROSS_BOARD_INFO.get().close();
    }

    public void testAnalyzeCrossData () {
        EntitiesInstances.CROSS_BOARD_INFO.set(
                SerDes
                        .getSerDes()
                        .getAnalyzeCrossData()
                        .apply( EntitiesInstances.CROSS_BOARD_INFO.get() )
                        .block()
        );

        assertNotNull( EntitiesInstances.CROSS_BOARD_INFO.get() );

        EntitiesInstances.CROSS_BOARD_INFO.get().close();
    }

    public void testPersonTotalDataByFIO () {
        final String name = "Sarvar";
        final FIO fio = new FIO();

        fio.setPatronym( name );
        fio.setSurname( name );
        fio.setName( name );

        assertNotNull( fio );

        final PersonTotalDataByFIO personTotalDataByFIO = SerDes
                .getSerDes()
                .getGetPersonTotalDataByFIO()
                .apply( fio )
                .block();

        assertNotNull( personTotalDataByFIO );
        assertNull( personTotalDataByFIO.getErrorResponse() );

        assertTrue( personTotalDataByFIO.getAnswereId() != 0 );
        assertFalse( personTotalDataByFIO.getAnswereMessage().isBlank() );
        assertFalse( personTotalDataByFIO.getAnswereComment().isBlank() );

        assertNotNull( personTotalDataByFIO.getData() );
        assertFalse( personTotalDataByFIO.getData().isEmpty() );
        assertNotNull( personTotalDataByFIO.getData().getFirst() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getPinpp().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getCadastre().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDateOfBirth().isBlank() );

        assertFalse( personTotalDataByFIO.getData().getFirst().getNameLatin().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getNameCyrillic().isBlank() );

        assertFalse( personTotalDataByFIO.getData().getFirst().getPatronymLatin().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getPatronymCyrillic().isBlank() );

        assertFalse( personTotalDataByFIO.getData().getFirst().getSurnameLatin().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getSurnameCyrillic().isBlank() );

        assertFalse( personTotalDataByFIO.getData().getFirst().getBirthPlace().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getPersonImage().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getRegion() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getRegion().getId() == 0 );
        assertFalse( personTotalDataByFIO.getData().getFirst().getRegion().getValue().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getSex() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getSex().getId() == 0 );
        assertFalse( personTotalDataByFIO.getData().getFirst().getSex().getValue().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getCountry() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getCountry().getId() == 0 );
        assertFalse( personTotalDataByFIO.getData().getFirst().getCountry().getValue().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getDistrict() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDistrict().getId() == 0 );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDistrict().getValue().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getDocument() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getIssuedBy().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getSerialNumber().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getDocument().getDocumentType() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDocumentType().getId() == 0 );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDocumentType().getValue().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getDocument().getDateIssue() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDateIssue().getDate().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDateIssue().getDateFrom().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDateIssue().getDateTill().isBlank() );

        assertNotNull( personTotalDataByFIO.getData().getFirst().getDocument().getDateValid() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDateValid().getDate().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDateValid().getDateFrom().isBlank() );
        assertFalse( personTotalDataByFIO.getData().getFirst().getDocument().getDateValid().getDateTill().isBlank() );
    }

    public void testPsychologyCardByData () {
        final PsychologyCard psychologyCard = SerDes
                .getSerDes()
                .getGetPsychologyCardByData()
                .apply( EntitiesInstances.MODEL_FOR_PASSPORT.get(), this.apiResponseModel )
                .block();

        assertNotNull( psychologyCard );
        assertNull( psychologyCard.getErrorResponse() );

        assertNotNull( psychologyCard.getModelForAddress() );
        assertNull( psychologyCard.getModelForAddress().getErrorResponse() );
        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration() );

        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPAddress().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPCadastre().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration() );
        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst() );

        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPAddress().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPCadastre().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPValidDate().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForPassport() );
        assertNull( psychologyCard.getModelForPassport().getErrorResponse() );

        assertFalse( psychologyCard.getModelForPassport().getAnswereId() == 0 );
        assertFalse( psychologyCard.getModelForPassport().getAnswereComment().isBlank() );
        assertFalse( psychologyCard.getModelForPassport().getAnswereMessage().isBlank() );

        assertNotNull( psychologyCard.getPinpp() );
        assertNull( psychologyCard.getPinpp().getErrorResponse() );

        assertFalse( psychologyCard.getPinpp().getName().isBlank() );
        assertFalse( psychologyCard.getPinpp().getPinpp().isBlank() );
        assertFalse( psychologyCard.getPinpp().getSurname().isBlank() );
        assertFalse( psychologyCard.getPinpp().getPatronym().isBlank() );
        assertFalse( psychologyCard.getPinpp().getCadastre().isBlank() );
        assertFalse( psychologyCard.getPinpp().getBirthDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr() );
        assertNull( psychologyCard.getModelForCadastr().getErrorResponse() );

        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().isEmpty() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ) );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ) );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getPapilonData() );
        assertFalse( psychologyCard.getPapilonData().isEmpty() );

        assertNotNull( psychologyCard.getPapilonData().getFirst() );

        assertFalse( psychologyCard.getPapilonData().getFirst().getRank() == 0 );
        assertFalse( psychologyCard.getPapilonData().getFirst().getScore() == 0 );

        assertFalse( psychologyCard.getPapilonData().getFirst().getName().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getBirth().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPhoto().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getCountry().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPassport().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPersonal_code().isBlank() );

        assertFalse( psychologyCard.getPersonImage().isEmpty() );

        assertNotNull( psychologyCard.getViolationList() );
        assertFalse( psychologyCard.getViolationList().isEmpty() );
        assertNotNull( psychologyCard.getViolationList().getFirst() );

        assertFalse( psychologyCard.getViolationList().getFirst().getPinpp().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getDecision().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getPunishment().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_id() == 0 );
        assertFalse( psychologyCard.getViolationList().getFirst().getLast_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getViolation_time().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getFirst_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getAdm_case_number().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getAdm_case_series().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_number().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_series().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getSecond_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getResolution_time().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getViolation_article().isBlank() );

        assertNotNull( psychologyCard.getModelForCarList() );
        assertNull( psychologyCard.getModelForCarList().getErrorResponse() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().isEmpty() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst() );

        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList() );
        assertNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getErrorResponse() );

        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().isEmpty() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().getFirst() );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getIssuedBy()
                        .isBlank()
        );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateBegin()
                        .isBlank()
        );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateValid()
                        .isBlank()
        );

        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getStir().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getYear().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPinpp().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPower().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getSeats().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getModel().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getColor().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getKuzov().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getStands().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getEngine().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getAddress().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getFuelType().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getFullWeight().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getAdditional().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPlateNumber().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getVehicleType().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getEmptyWeight().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getOrganization().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getRegistrationDate().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getTexPassportSerialNumber().isBlank() );

        psychologyCard.close();
    }

    public void testPsychologyCardByPinfl () {
        final PsychologyCard psychologyCard = SerDes
                .getSerDes()
                .getGetPsychologyCardByPinfl()
                .apply( this.apiResponseModel )
                .block();

        assertNotNull( psychologyCard );
        assertNull( psychologyCard.getErrorResponse() );

        assertNotNull( psychologyCard.getModelForAddress() );
        assertNull( psychologyCard.getModelForAddress().getErrorResponse() );
        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration() );

        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPAddress().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPCadastre().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration() );
        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst() );

        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPAddress().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPCadastre().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPValidDate().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForPassport() );
        assertNull( psychologyCard.getModelForPassport().getErrorResponse() );

        assertFalse( psychologyCard.getModelForPassport().getAnswereId() == 0 );
        assertFalse( psychologyCard.getModelForPassport().getAnswereComment().isBlank() );
        assertFalse( psychologyCard.getModelForPassport().getAnswereMessage().isBlank() );

        assertNotNull( psychologyCard.getPinpp() );
        assertNull( psychologyCard.getPinpp().getErrorResponse() );

        assertFalse( psychologyCard.getPinpp().getName().isBlank() );
        assertFalse( psychologyCard.getPinpp().getPinpp().isBlank() );
        assertFalse( psychologyCard.getPinpp().getSurname().isBlank() );
        assertFalse( psychologyCard.getPinpp().getPatronym().isBlank() );
        assertFalse( psychologyCard.getPinpp().getCadastre().isBlank() );
        assertFalse( psychologyCard.getPinpp().getBirthDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr() );
        assertNull( psychologyCard.getModelForCadastr().getErrorResponse() );

        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().isEmpty() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ) );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ) );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getPapilonData() );
        assertFalse( psychologyCard.getPapilonData().isEmpty() );

        assertNotNull( psychologyCard.getPapilonData().getFirst() );

        assertFalse( psychologyCard.getPapilonData().getFirst().getRank() == 0 );
        assertFalse( psychologyCard.getPapilonData().getFirst().getScore() == 0 );

        assertFalse( psychologyCard.getPapilonData().getFirst().getName().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getBirth().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPhoto().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getCountry().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPassport().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPersonal_code().isBlank() );

        assertFalse( psychologyCard.getPersonImage().isEmpty() );

        assertNotNull( psychologyCard.getViolationList() );
        assertFalse( psychologyCard.getViolationList().isEmpty() );
        assertNotNull( psychologyCard.getViolationList().getFirst() );

        assertFalse( psychologyCard.getViolationList().getFirst().getPinpp().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getDecision().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getPunishment().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_id() == 0 );
        assertFalse( psychologyCard.getViolationList().getFirst().getLast_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getViolation_time().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getFirst_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getAdm_case_number().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getAdm_case_series().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_number().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_series().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getSecond_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getResolution_time().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getViolation_article().isBlank() );

        assertNotNull( psychologyCard.getModelForCarList() );
        assertNull( psychologyCard.getModelForCarList().getErrorResponse() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().isEmpty() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst() );

        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList() );
        assertNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getErrorResponse() );

        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().isEmpty() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().getFirst() );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getIssuedBy()
                        .isBlank()
        );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateBegin()
                        .isBlank()
        );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateValid()
                        .isBlank()
        );

        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getStir().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getYear().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPinpp().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPower().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getSeats().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getModel().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getColor().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getKuzov().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getStands().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getEngine().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getAddress().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getFuelType().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getFullWeight().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getAdditional().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPlateNumber().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getVehicleType().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getEmptyWeight().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getOrganization().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getRegistrationDate().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getTexPassportSerialNumber().isBlank() );

        psychologyCard.close();
    }

    public void testPsychologyCardByPinflInitial () {
        final PsychologyCard psychologyCard = SerDes
                .getSerDes()
                .getGetPsychologyCardByPinflInitial()
                .apply( this.apiResponseModel )
                .block();

        assertNotNull( psychologyCard );
        assertNull( psychologyCard.getErrorResponse() );

        assertNotNull( psychologyCard.getModelForAddress() );
        assertNull( psychologyCard.getModelForAddress().getErrorResponse() );
        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration() );

        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPAddress().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPCadastre().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPRegion().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict() );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getPermanentRegistration().getPDistrict().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration() );
        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst() );

        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPAddress().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPCadastre().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPValidDate().isBlank() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPRegion().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict() );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict().getId() == 0 );
        assertFalse( psychologyCard.getModelForAddress().getTemproaryRegistration().getFirst().getPDistrict().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForPassport() );
        assertNull( psychologyCard.getModelForPassport().getErrorResponse() );

        assertFalse( psychologyCard.getModelForPassport().getAnswereId() == 0 );
        assertFalse( psychologyCard.getModelForPassport().getAnswereComment().isBlank() );
        assertFalse( psychologyCard.getModelForPassport().getAnswereMessage().isBlank() );

        assertNotNull( psychologyCard.getPinpp() );
        assertNull( psychologyCard.getPinpp().getErrorResponse() );

        assertFalse( psychologyCard.getPinpp().getName().isBlank() );
        assertFalse( psychologyCard.getPinpp().getPinpp().isBlank() );
        assertFalse( psychologyCard.getPinpp().getSurname().isBlank() );
        assertFalse( psychologyCard.getPinpp().getPatronym().isBlank() );
        assertFalse( psychologyCard.getPinpp().getCadastre().isBlank() );
        assertFalse( psychologyCard.getPinpp().getBirthDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr() );
        assertNull( psychologyCard.getModelForCadastr().getErrorResponse() );

        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().isEmpty() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ) );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().get( 0 ).getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ) );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().get( 0 ).getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getPapilonData() );
        assertFalse( psychologyCard.getPapilonData().isEmpty() );

        assertNotNull( psychologyCard.getPapilonData().getFirst() );

        assertFalse( psychologyCard.getPapilonData().getFirst().getRank() == 0 );
        assertFalse( psychologyCard.getPapilonData().getFirst().getScore() == 0 );

        assertFalse( psychologyCard.getPapilonData().getFirst().getName().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getBirth().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPhoto().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getCountry().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPassport().isBlank() );
        assertFalse( psychologyCard.getPapilonData().getFirst().getPersonal_code().isBlank() );

        assertFalse( psychologyCard.getPersonImage().isEmpty() );

        assertNotNull( psychologyCard.getViolationList() );
        assertFalse( psychologyCard.getViolationList().isEmpty() );
        assertNotNull( psychologyCard.getViolationList().getFirst() );

        assertFalse( psychologyCard.getViolationList().getFirst().getPinpp().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getDecision().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getPunishment().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_id() == 0 );
        assertFalse( psychologyCard.getViolationList().getFirst().getLast_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getViolation_time().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getFirst_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getAdm_case_number().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getAdm_case_series().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_number().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getProtocol_series().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getSecond_name_lat().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getResolution_time().isBlank() );
        assertFalse( psychologyCard.getViolationList().getFirst().getViolation_article().isBlank() );

        assertNotNull( psychologyCard.getModelForCarList() );
        assertNull( psychologyCard.getModelForCarList().getErrorResponse() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().isEmpty() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst() );

        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList() );
        assertNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getErrorResponse() );

        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().isEmpty() );
        assertNotNull( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getDoverennostList().getDoverennostsList().getFirst() );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getIssuedBy()
                        .isBlank()
        );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateBegin()
                        .isBlank()
        );

        assertFalse(
                psychologyCard
                        .getModelForCarList()
                        .getModelForCarList()
                        .getFirst()
                        .getDoverennostList()
                        .getDoverennostsList()
                        .getFirst()
                        .getDateValid()
                        .isBlank()
        );

        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getStir().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getYear().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPinpp().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPower().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getSeats().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getModel().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getColor().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getKuzov().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getStands().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getEngine().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getAddress().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getFuelType().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getFullWeight().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getAdditional().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getPlateNumber().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getVehicleType().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getEmptyWeight().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getOrganization().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getRegistrationDate().isBlank() );
        assertFalse( psychologyCard.getModelForCarList().getModelForCarList().getFirst().getTexPassportSerialNumber().isBlank() );

        psychologyCard.close();
    }
}
