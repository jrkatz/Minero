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

import net.jrkatz.minero.budget.period.PeriodDefinition;

/**
 * Definition of a budget.
 */
public class Budget implements Parcelable {
    private final PeriodDefinition mPeriodDefinition;
    private final long mDistribution;
    private final String mName;

    Budget(
            final PeriodDefinition periodDefinition,
            final int distribution,
            final String name
    ) {
        mPeriodDefinition = periodDefinition;
        mDistribution = distribution;
        mName = name;
    }

    protected Budget(Parcel in) {
        mPeriodDefinition = in.readParcelable(PeriodDefinition.class.getClassLoader());
        mDistribution = in.readLong();
        mName = in.readString();
    }

    public static final Creator<Budget> CREATOR = new Creator<Budget>() {
        @Override
        public Budget createFromParcel(Parcel in) {
            return new Budget(in);
        }

        @Override
        public Budget[] newArray(int size) {
            return new Budget[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mPeriodDefinition, flags);
        dest.writeLong(mDistribution);
        dest.writeString(mName);
    }
}