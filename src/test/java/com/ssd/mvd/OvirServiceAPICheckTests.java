package com.ssd.mvd;

import com.ssd.mvd.entity.modelForFioOfPerson.PersonTotalDataByFIO;
import com.ssd.mvd.entity.modelForPassport.ModelForPassport;
import com.ssd.mvd.entity.boardCrossing.CrossBoardInfo;
import com.ssd.mvd.entity.modelForFioOfPerson.FIO;
import com.ssd.mvd.entity.ApiResponseModel;
import com.ssd.mvd.entity.PsychologyCard;
import com.ssd.mvd.controller.SerDes;
import com.ssd.mvd.entity.Status;

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
            ).success( true )
            .build();

    @Override
    public void setUp () {
        SerDes.getSerDes();
    }

    @Override
    public void tearDown () {
        SerDes.getSerDes().close();
    }

    public void testCrossBoardInfo () {
        final CrossBoardInfo crossBoardInfo = SerDes
                .getSerDes()
                .getGetCrossBoardInfo()
                .apply( this.apiResponseModel.getStatus().getMessage() )
                .block();

        assertNotNull( crossBoardInfo );
        assertNull( crossBoardInfo.getErrorResponse() );

        assertFalse( crossBoardInfo.getResult().isBlank() );

        assertNotNull( crossBoardInfo.getData() );
        assertFalse( crossBoardInfo.getData().isEmpty() );
        assertNotNull( crossBoardInfo.getData().getFirst() );
        assertFalse( crossBoardInfo.getData().getFirst().getCrossBoardList().isEmpty() );
        assertNotNull( crossBoardInfo.getData().getFirst().getCrossBoardList().getFirst() );

        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPeriod_code() == 0
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrip_purpose_code() == 0
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDirection_country() == 0
        );

        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getCard_id() == 0L
        );

        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getCitizenship() == 0L
        );

        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getRegistrationDate()
                        .before( new Date() )
        );

        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPinpp()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getReg_date()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDocument()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getFull_name()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getBirth_date()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPoint_code()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getVisa_number()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getNationality()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrans_number()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrans_add_info()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDate_end_document()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDocument_type_code()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getDirection_type_code()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getTrans_category_code()
                        .isBlank()
        );

        assertNotNull(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
        );

        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getPeriods()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getCountries()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getTripPurpose()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getDocumentType()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getNationalities()
                        .isBlank()
        );
        assertFalse(
                crossBoardInfo
                        .getData()
                        .getFirst()
                        .getCrossBoardList()
                        .getFirst()
                        .getPurpose()
                        .getTransportCategory()
                        .isBlank()
        );

        assertNotNull( crossBoardInfo.getData().getFirst().getPerson() );

        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getSex() == 0 );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getLivestatus() == 0 );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getTransaction_id() == 0 );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getCitizenshipid() == 0 );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getBirthcountryid() == 0 );

        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getNamelat().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getEngname().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getSurnamelat().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getEngsurname().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getBirth_date().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getNationality().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getPatronymlat().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getCitizenship().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getBirthcountry().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getCurrent_pinpp().isBlank() );
        assertFalse( crossBoardInfo.getData().getFirst().getPerson().getCurrent_document().isBlank() );

        crossBoardInfo.close();
    }

    public void testAnalyzeCrossData () {
        final CrossBoardInfo crossBoardInfo = SerDes
                .getSerDes()
                .getAnalyzeCrossData()
                .apply( new CrossBoardInfo() )
                .block();

        assertNotNull( crossBoardInfo );

        crossBoardInfo.close();
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
                .apply( new ModelForPassport(), this.apiResponseModel )
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

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus().getValue().isBlank() );

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

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus().getValue().isBlank() );

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

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getPermanentRegistration().getFirst().getPStatus().getValue().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPPsp().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPPerson().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPCitizen().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPDateBirth().isBlank() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPRegistrationDate().isBlank() );

        assertNotNull( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus() );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus().getId() == 0 );
        assertFalse( psychologyCard.getModelForCadastr().getTemproaryRegistration().getFirst().getPStatus().getValue().isBlank() );

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
