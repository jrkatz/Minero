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

package net.jrkatz.minero.budget.debit;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.LocalDateTime;

import java.util.Date;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public class Debit implements Parcelable {

    private final long mId;
    private final long mAmount;
    private final String mDescription;
    private final LocalDateTime mTime;

    protected Debit(long id, long amount, String description, LocalDateTime time) {
        this.mId = id;
        this.mAmount = amount;
        this.mDescription = description;
        this.mTime = time;
    }

    public static final Creator<Debit> CREATOR = new Creator<Debit>() {
        @Override
        public Debit createFromParcel(Parcel in) {
            return new Debit(in);
        }

        @Override
        public Debit[] newArray(int size) {
            return new Debit[size];
        }
    };

    public long getAmount() {
        return mAmount;
    }

    public String getDescription() {
        return mDescription;
    }

    public LocalDateTime getTime() {
        return mTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mAmount);
        dest.writeString(mDescription);
        dest.writeLong(mTime.toDate().getTime());
    }

    protected Debit(Parcel in) {
        mId = in.readLong();
        mAmount = in.readLong();
        mDescription = in.readString();
        mTime = LocalDateTime.fromDateFields(new Date(in.readLong()));
    }
}