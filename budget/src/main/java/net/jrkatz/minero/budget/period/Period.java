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

package net.jrkatz.minero.budget.period;

import android.os.Parcel;
import android.os.Parcelable;

import net.jrkatz.minero.budget.R;

import org.joda.time.LocalDate;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public class Period implements Parcelable {
    private final LocalDate mStart;
    private final LocalDate mEnd;

    public Period(final LocalDate start, final LocalDate end) {
        mStart = start;
        mEnd = end;
    }

    protected Period(Parcel in) {
        mStart = new LocalDate(in.readInt(), in.readInt(), in.readInt());
        mEnd = new LocalDate(in.readInt(), in.readInt(), in.readInt());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mStart.getYear());
        dest.writeInt(mStart.getMonthOfYear());
        dest.writeInt(mStart.getDayOfMonth());
        dest.writeInt(mEnd.getYear());
        dest.writeInt(mEnd.getMonthOfYear());
        dest.writeInt(mEnd.getDayOfMonth());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Period> CREATOR = new Creator<Period>() {
        @Override
        public Period createFromParcel(Parcel in) {
            return new Period(in);
        }

        @Override
        public Period[] newArray(int size) {
            return new Period[size];
        }
    };

    public LocalDate getStart() {
        return mStart;
    }

    public LocalDate getEnd() {
        return mEnd;
    }
}
