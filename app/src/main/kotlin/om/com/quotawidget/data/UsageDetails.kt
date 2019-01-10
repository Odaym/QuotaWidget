package om.com.quotawidget.data

import android.os.Parcel
import android.os.Parcelable

class UsageDetails(
    val monthly_max: String?,
    val total_actual_usage: String?,
    val remaining_monthly_within_max: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(monthly_max)
        parcel.writeString(total_actual_usage)
        parcel.writeString(remaining_monthly_within_max)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UsageDetails> {
        override fun createFromParcel(parcel: Parcel): UsageDetails {
            return UsageDetails(parcel)
        }

        override fun newArray(size: Int): Array<UsageDetails?> {
            return arrayOfNulls(size)
        }
    }
}
