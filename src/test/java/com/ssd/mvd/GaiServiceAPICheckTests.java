package com.ssd.mvd;

import com.ssd.mvd.entity.ModelForCarList;
import com.ssd.mvd.entity.modelForGai.*;
import com.ssd.mvd.controller.SerDes;
import junit.framework.TestCase;

public final class GaiServiceAPICheckTests extends TestCase {
    private final String testNumber = "01D819CC";

    @Override
    public void setUp () {
        SerDes.getSerDes();
    }

    @Override
    public void tearDown () {
        SerDes.getSerDes().close();
    }

    public void testGaiToken () {
        assertFalse(
                SerDes
                        .getSerDes()
                        .getTokenForGai()
                        .isBlank()
        );
    }

    public void testInsurance () {
        final Insurance insurance = SerDes
                .getSerDes()
                .getInsurance()
                .apply( this.testNumber )
                .block();

        assertNotNull( insurance );
        assertNull( insurance.getErrorResponse() );

        assertFalse( insurance.getDateBegin().isBlank() );
        assertFalse( insurance.getDateValid().isBlank() );
        assertFalse( insurance.getTintinType().isBlank() );
    }

    public void testVehicleData () {
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

        assertFalse( violationsList.getViolationsInformationsList().getFirst().getBill().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getModel().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getOwner().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getPayDate().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getAddress().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getArticle().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getDivision().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getAmount() < 0 );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getViolation().isBlank() );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getDecreeStatus() == 0 );
        assertFalse( violationsList.getViolationsInformationsList().getFirst().getDecreeSerialNumber().isBlank() );

        violationsList.close();
    }

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
