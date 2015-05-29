package model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mifercre on 31/03/15.
 */
public class Trip implements Parcelable {

    private int type;

    private String tripId;

    private City cityFrom;
    private City cityTo;

    private String carrierName;
    private String carrierEmail;
    private String carrierPhone;
    private String carrierPictureVersion;
    private String carrierPictureId;
    private boolean carrierHasFacebook;

    private String departureDate;
    private String priceSmall;
    private String priceMedium;

    private String priceBig;
    private String priceExtraBig;
    private String returnDate;
    private String description;
    private boolean[] recurrentGoDays;
    private boolean[] recurrentBackDays;

    public Trip(String tripId, City cityFrom, City cityTo, String carrierName, String carrierEmail, String date, String priceSmall, String priceMedium, String priceBig, String priceExtraBig) {
        this.tripId = tripId;
        this.cityFrom = cityFrom;
        this.cityTo = cityTo;
        this.carrierName = carrierName;
        this.carrierEmail = carrierEmail;
        this.departureDate = date;
        this.priceSmall = priceSmall;
        this.priceMedium = priceMedium;
        this.priceBig = priceBig;
        this.priceExtraBig = priceExtraBig;
    }

    public Trip() {

    }

    public Trip(Parcel in) {
        readFromParcel(in);
    }

    public City getCityFrom() {
        return cityFrom;
    }

    public void setCityFrom(City cityFrom) {
        this.cityFrom = cityFrom;
    }

    public City getCityTo() {
        return cityTo;
    }

    public void setCityTo(City cityTo) {
        this.cityTo = cityTo;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getCarrierEmail() {
        return carrierEmail;
    }

    public void setCarrierEmail(String carrierEmail) {
        this.carrierEmail = carrierEmail;
    }

    /**public String getDepartureDateMyFormatDateTime() {
        MySimpleDateFormat mySimpleDateFormat = new MySimpleDateFormat();
        String dateRes = mySimpleDateFormat.formatFromISOtoMyFormatDateTime(departureDate);
        return dateRes;
    }**/

    public String getDepartureDateAndroid() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    /**public String getReturnDate() {
     MySimpleDateFormat mySimpleDateFormat = new MySimpleDateFormat();
     String dateRes = mySimpleDateFormat.formatFromISOtoMyFormatDateTime(returnDate);
     return dateRes;
     }**/

    public String getReturnDateAndroid() {
        return returnDate;
    }

    public String getPriceSmall() {
        return priceSmall;
    }

    public void setPriceSmall(String priceSmall) {
        this.priceSmall = priceSmall;
    }

    public String getPriceMedium() {
        return priceMedium;
    }

    public void setPriceMedium(String priceMedium) {
        this.priceMedium = priceMedium;
    }

    public String getPriceBig() {
        return priceBig;
    }

    public void setPriceBig(String priceBig) {
        this.priceBig = priceBig;
    }

    public String getPriceExtraBig() {
        return priceExtraBig;
    }

    public void setPriceExtraBig(String priceExtraBig) {
        this.priceExtraBig = priceExtraBig;
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    @Override
    public String toString() {
        String cityFromName = "cityFrom null";
        String cityToName = "cityTo null";
        if(cityFrom != null ) cityFromName  = cityFrom.toString();
        if(cityTo != null ) cityToName = cityTo.toString();

        return "Trip{" +
                "tripId='" + tripId + '\'' +
                ", cityFrom=" + cityFromName +
                ", cityTo=" + cityToName +
                ", carrierName='" + carrierName + '\'' +
                ", carrierEmail='" + carrierEmail + '\'' +
                ", departureDate='" + departureDate + '\'' +
                ", priceSmall='" + priceSmall + '\'' +
                ", priceMedium='" + priceMedium + '\'' +
                ", priceBig='" + priceBig + '\'' +
                ", priceExtraBig='" + priceExtraBig + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tripId);
        dest.writeString(carrierName);
        dest.writeString(carrierEmail);
        dest.writeString(departureDate);
        dest.writeString(priceSmall);
        dest.writeString(priceMedium);
        dest.writeString(priceBig);
        dest.writeString(priceExtraBig);

        dest.writeParcelable(cityFrom, flags);
        dest.writeParcelable(cityTo, flags);
    }

    private void readFromParcel(Parcel in) {
        tripId = in.readString();
        carrierName = in.readString();
        carrierEmail = in.readString();
        departureDate = in.readString();
        priceSmall = in.readString();
        priceMedium = in.readString();
        priceBig = in.readString();
        priceExtraBig = in.readString();

        cityFrom = in.readParcelable(City.class.getClassLoader());
        cityTo = in.readParcelable(City.class.getClassLoader());
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Trip createFromParcel(Parcel in) {
                    return new Trip(in);
                }

                public Trip[] newArray(int size) {
                    return new Trip[size];
                }
            };

    public void setCarrierPictureId(String carrierPictureId) {
        this.carrierPictureId = carrierPictureId;
    }

    public void setCarrierPictureVersion(String carrierPictureVersion) {
        this.carrierPictureVersion = carrierPictureVersion;
    }

    public String getCarrierPictureVersion() {
        return carrierPictureVersion;
    }

    public String getCarrierPictureId() {
        return carrierPictureId;
    }

    public void setCarrierPhone(String carrierPhone) {
        this.carrierPhone = carrierPhone;
    }

    public String getCarrierPhone() {
        return carrierPhone;
    }

    public boolean getCarrierHasFacebook() {
        return carrierHasFacebook;
    }

    public void setCarrierHasFacebook(boolean carrierHasFacebook) {
        this.carrierHasFacebook = carrierHasFacebook;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setRecurrentGoDays(boolean[] recurrentGoDays) {
        this.recurrentGoDays = recurrentGoDays;
    }

    public boolean[] getRecurrentGoDays() {
        return recurrentGoDays;
    }

    public void setRecurrentBackDays(boolean[] recurrentBackDays) {
        this.recurrentBackDays = recurrentBackDays;
    }

    public boolean[] getRecurrentBackDays() {
        return recurrentBackDays;
    }
}
