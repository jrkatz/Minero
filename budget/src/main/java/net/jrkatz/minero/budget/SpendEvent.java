/*
 *     Minero is a minimal budget application
 *     Copyright (C) 2017 Jacob Katz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jrkatz.minero.budget;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalDateTime;

import java.util.Date;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public class SpendEvent implements Parcelable {
    private final int mId;
    private final long mAmount;
    private final String mDescription;
    private final LocalDateTime mTime;

    public SpendEvent(int id, long amount, String description, LocalDateTime time) {
        this.mId = id;
        this.mAmount = amount;
        this.mDescription = description;
        this.mTime = time;
    }

    protected SpendEvent(Parcel in) {
        mId = in.readInt();
        mAmount = in.readLong();
        mDescription = in.readString();
        mTime = LocalDateTime.fromDateFields(new Date(in.readLong()));
    }

    public static final Creator<SpendEvent> CREATOR = new Creator<SpendEvent>() {
        @Override
        public SpendEvent createFromParcel(Parcel in) {
            return new SpendEvent(in);
        }

        @Override
        public SpendEvent[] newArray(int size) {
            return new SpendEvent[size];
        }
    };

    public long getAmount() {
        return mAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeLong(mAmount);
        dest.writeString(mDescription);
        dest.writeLong(mTime.toDate().getTime());
    }
}
