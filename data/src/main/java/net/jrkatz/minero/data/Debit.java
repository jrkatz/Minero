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

package net.jrkatz.minero.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * @Author jrkatz
 * @Date 2/19/2017.
 */
public class Debit implements Parcelable, Comparable<Debit> {

    public static final int NO_PARENT = -1;
    private final long mId;
    private final long mBudgetId;
    private final long mBudgetPeriodId;
    private final int mAmount;
    private final String mDescription;
    private final DateTime mTime;
    private final long mParentId;

    protected Debit(long id, long budgetId, long budgetPeriodId, int amount, String description, DateTime time, @Nullable Long parentId) {
        mId = id;
        mBudgetId = budgetId;
        mBudgetPeriodId = budgetPeriodId;
        mAmount = amount;
        mDescription = description;
        mTime = time;
        mParentId = parentId == null ? NO_PARENT : parentId;
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

    public long getId() {
        return mId;
    }

    public long getBudgetId() {
        return mBudgetId;
    }

    public Long getParentId() {
        return mParentId != NO_PARENT ? mParentId : null;
    }

    public long getBudgetPeriodId() {
        return mBudgetPeriodId;
    }

    public int getAmount() {
        return mAmount;
    }

    public String getDescription() {
        return mDescription;
    }

    public DateTime getTime() {
        return mTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mBudgetId);
        dest.writeLong(mBudgetPeriodId);
        dest.writeInt(mAmount);
        dest.writeString(mDescription);
        dest.writeLong(mTime.toDate().getTime());
        dest.writeString(mTime.getZone().getID());
        dest.writeLong(mParentId);
    }

    protected Debit(Parcel in) {
        mId = in.readLong();
        mBudgetId = in.readLong();
        mBudgetPeriodId = in.readLong();
        mAmount = in.readInt();
        mDescription = in.readString();
        mTime = new DateTime(in.readLong())
                .withZone(DateTimeZone.forID(in.readString()));
        mParentId = in.readLong();
    }

    @Override
    public int compareTo(@NonNull Debit o) {
        return this.getTime().compareTo(o.getTime());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || ! (o instanceof Debit)) {
            return false;
        }

        final Debit d = (Debit) o;
        return d.getId() == getId();
    }

    @Override
    public int hashCode() {
        return new Long(getId()).hashCode();
    }
}